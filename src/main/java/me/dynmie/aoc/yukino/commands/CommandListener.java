package me.dynmie.aoc.yukino.commands;

import me.dynmie.aoc.yukino.Yukino;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Collection;

public class CommandListener {

    @SubscribeEvent
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        if (!Yukino.getInstance().getConfig().getGuildId().equals(guild.getId())) {
            return;
        }

        Collection<YukinoCommand[]> category = CommandManager.COMMANDS.values();

        Yukino.LOGGER.info(String.format("Received new slash interaction from '%s' for '%s'", event.getUser().getAsTag(), event.getInteraction().getName()));
        for (YukinoCommand[] commands : category) {
            for (YukinoCommand command : commands) {
                if (event.getName().equals(command.getSlashCommandData().getName())) {
                    try {
                        command.executeSlashCommand(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Yukino.LOGGER.severe(String.format("An error occurred while executing '%s'.", event.getInteraction().getName()));
                    }
                }
            }
        }

    }

}
