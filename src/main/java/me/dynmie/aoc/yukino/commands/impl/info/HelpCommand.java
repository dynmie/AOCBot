package me.dynmie.aoc.yukino.commands.impl.info;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.commands.CommandCategory;
import me.dynmie.aoc.yukino.commands.CommandManager;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import me.dynmie.jeorge.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HelpCommand implements YukinoCommand {

    private final CommandManager commandManager;

    @Inject
    public HelpCommand(Yukino yukino) {
        this.commandManager = yukino.getCommandManager();
    }

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("help", "Bring up the help menu");
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {

        EmbedBuilder builder = EmbedUtils.getDefaultEmbed(event.getJDA())
                .setTitle("Help");

        for (Map.Entry<CommandCategory, List<YukinoCommand>> entry : commandManager.getCommands().entrySet()) {
            CommandCategory category = entry.getKey();
            List<YukinoCommand> commands = entry.getValue();

            if (commands.isEmpty()) continue;

            StringJoiner joiner = new StringJoiner("\n");
            for (YukinoCommand command : commands) {
                joiner.add(String.format("/`%s` - %s", command.getSlashCommandData().getName(), command.getSlashCommandData().getDescription()));
            }

            builder.addField(category.getEmoji() + " " + category.getNiceName(), joiner.toString(), true);
        }

        event.replyEmbeds(builder.build())
                .setEphemeral(true)
                .queue();
    }
}
