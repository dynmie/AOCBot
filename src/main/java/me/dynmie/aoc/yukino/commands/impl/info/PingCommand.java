package me.dynmie.aoc.yukino.commands.impl.info;

import me.dynmie.aoc.yukino.commands.YukinoCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements YukinoCommand {

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("ping", "Check the latency");
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        event.reply(":ping_pong: Pong! API latency is `" + event.getJDA().getGatewayPing() + "ms`.")
                .queue();
    }

}
