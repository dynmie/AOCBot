package me.dynmie.aoc.yukino.commands;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.utils.BotConfig;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import me.dynmie.jeorge.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class CommandListener {

    private final BotConfig config;
    private final Yukino yukino;

    @Inject
    public CommandListener(BotConfig config, Yukino yukino) {
        this.config = config;
        this.yukino = yukino;
    }

    @SubscribeEvent
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!config.getGuildId().equals(guild.getId())) {
            return;
        }

        Collection<List<YukinoCommand>> category = yukino.getCommandManager().getCommands().values();

        Yukino.LOGGER.info(String.format("Received new slash interaction from '%s' for '%s'", event.getUser().getAsTag(), event.getInteraction().getName()));
        for (List<YukinoCommand> commands : category) {
            for (YukinoCommand command : commands) {
                if (event.getName().equals(command.getSlashCommandData().getName())) {
                    try {
                        command.executeSlashCommand(event);
                    } catch (Exception e) {
                        event.replyEmbeds(EmbedUtils.getErrorEmbed(event.getJDA()).build()).setEphemeral(true).queue();
                        Yukino.LOGGER.severe(String.format("An error occurred while executing '%s'.", event.getInteraction().getName()));
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }

}
