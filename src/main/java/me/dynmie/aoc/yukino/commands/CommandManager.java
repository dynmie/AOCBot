package me.dynmie.aoc.yukino.commands;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.commands.impl.aoc.*;
import me.dynmie.aoc.yukino.commands.impl.info.AboutCommand;
import me.dynmie.aoc.yukino.commands.impl.info.HelpCommand;
import me.dynmie.aoc.yukino.commands.impl.info.PingCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    private final Yukino yukino = Yukino.getInstance();

    public static final Map<CommandCategory, YukinoCommand[]> COMMANDS = new HashMap<>() {{
        put(CommandCategory.INFO, new YukinoCommand[]{
                new AboutCommand(),
                new HelpCommand(),
                new PingCommand()
        });

        put(CommandCategory.AOC, new YukinoCommand[]{
                new LookupCommand(),
                new MemberCommand(),
                new ClickCommand(),
                new StrikeCommand(),
                new ResetStrikesCommand(),
                new HoursCommand(),
                new LeaderboardCommand()
        });
    }};

    private void registerCommand(YukinoCommand command) {
        for (Method method : command.getClass().getMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                continue;
            }
            yukino.getJDA().addEventListener(command);
            break;
        }
    }

    public void registerGuild(String guildId) {
        Guild guild = yukino.getJDA().getGuildById(guildId);
        if (guild == null) return;

        CommandListUpdateAction action = guild.updateCommands();

        for (YukinoCommand[] commands : COMMANDS.values()) {
            for (YukinoCommand command : commands) {
                registerCommand(command);
                action = action.addCommands(command.getSlashCommandData());
            }
        }

        action.queue(commands -> Yukino.LOGGER.info("Registered " + commands.size() + " commands for guild '" + guild.getName() + "' (" + guildId + ")."), Throwable::printStackTrace);
    }

    public void unregisterGuild(String guildId) {
        Guild guild = yukino.getJDA().getGuildById(guildId);
        if (guild == null) return;

        for (YukinoCommand[] commands : COMMANDS.values()) {
            for (YukinoCommand command : commands) {
                registerCommand(command);
            }
        }

        guild.updateCommands().queue();
    }

}
