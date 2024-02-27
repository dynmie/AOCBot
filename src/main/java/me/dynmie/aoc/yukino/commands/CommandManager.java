package me.dynmie.aoc.yukino.commands;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.commands.impl.aoc.*;
import me.dynmie.aoc.yukino.commands.impl.info.AboutCommand;
import me.dynmie.aoc.yukino.commands.impl.info.HelpCommand;
import me.dynmie.aoc.yukino.commands.impl.info.PingCommand;
import me.dynmie.jeorge.Injector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager {

    private final Injector injector;
    private final JDA jda;


    private final Map<CommandCategory, List<YukinoCommand>> commands = new HashMap<>();

    public CommandManager(Injector injector, JDA jda) {
        this.injector = injector;
        this.jda = jda;

        addCommands();
    }

    private void addCommands() {
        commands.clear();
        Map<CommandCategory, List<Class<? extends YukinoCommand>>> toAdd = new HashMap<>() {{
            put(CommandCategory.INFO, List.of(
                    AboutCommand.class,
                    HelpCommand.class,
                    PingCommand.class
            ));

            put(CommandCategory.AOC, List.of(
                    LookupCommand.class,
                    MemberCommand.class,
                    ClickCommand.class,
                    StrikeCommand.class,
                    ResetStrikesCommand.class,
                    HoursCommand.class,
                    LeaderboardCommand.class
            ));
        }};

        for (CommandCategory category : toAdd.keySet()) {
            for (Class<? extends YukinoCommand> clazz : toAdd.get(category)) {
                List<YukinoCommand> list = commands.computeIfAbsent(category, commandCategory -> new ArrayList<>());
                list.add(injector.createInstance(clazz));
            }
        }
    }

    private void registerCommand(YukinoCommand command) {
        for (Method method : command.getClass().getMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                continue;
            }
            jda.addEventListener(command);
            break;
        }
    }

    public void registerGuild(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        CommandListUpdateAction action = guild.updateCommands();

        for (List<YukinoCommand> commands : commands.values()) {
            for (YukinoCommand command : commands) {
                registerCommand(command);
                action = action.addCommands(command.getSlashCommandData());
            }
        }

        action.queue(commands -> Yukino.LOGGER.info("Registered " + commands.size() + " commands for guild '" + guild.getName() + "' (" + guildId + ")."), Throwable::printStackTrace);
    }

    public void unregisterGuild(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        for (List<YukinoCommand> commands : commands.values()) {
            for (YukinoCommand command : commands) {
                registerCommand(command);
            }
        }

        guild.updateCommands().queue();
    }

    public Map<CommandCategory, List<YukinoCommand>> getCommands() {
        return commands;
    }
}
