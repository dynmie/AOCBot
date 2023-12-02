package me.dynmie.aoc.yukino.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public interface YukinoCommand {
    @NotNull SlashCommandData getSlashCommandData();

    void executeSlashCommand(@NotNull SlashCommandInteractionEvent event);
}