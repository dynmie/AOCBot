package me.dynmie.aoc.yukino.commands.impl.info;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.utils.DurationUtils;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import me.dynmie.jeorge.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

/**
 * @author dynmie
 */
public class AboutCommand implements YukinoCommand {

    @Inject
    private Yukino yukino;

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("about", "Get information about this bot");
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        EmbedBuilder builder = EmbedUtils.getDefaultEmbed()
                .setTitle("About " + event.getJDA().getSelfUser().getName())
                .setDescription("This is the official discord bot for the Adopt our Campus Club!")
                .addField(":computer: Developer", "dynmie", true)
                .addField(":ping_pong: Ping", event.getJDA().getGatewayPing() + "ms", true)
                .addField(":calendar: Uptime", DurationUtils.formatLong(System.currentTimeMillis() - yukino.getStartMillis()) + " mins", true)
                .addField(":globe_with_meridians: Language", "Java", true)
                .addField(":film_frames: Memory Usage", ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024) / 1024 + "MB", true)
                .addField(":coffee: Repository", "https://github.com/dynmie/AOCBot/", true);

        event.replyEmbeds(builder.build()).setEphemeral(false).queue();
    }
}
