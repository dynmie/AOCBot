package me.dynmie.aoc.yukino.commands.impl.info;

import me.dynmie.aoc.yukino.commands.CommandCategory;
import me.dynmie.aoc.yukino.commands.CommandManager;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.StringJoiner;

public class HelpCommand implements YukinoCommand {

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("help", "Bring up the help menu");
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {

        EmbedBuilder builder = EmbedUtils.getDefaultEmbed()
                .setTitle("Help");

        for (Map.Entry<CommandCategory, YukinoCommand[]> entry : CommandManager.COMMANDS.entrySet()) {
            CommandCategory category = entry.getKey();
            YukinoCommand[] commands = entry.getValue();

            if (commands.length == 0) continue;

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
