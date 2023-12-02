package me.dynmie.aoc.yukino.utils;

import me.dynmie.aoc.yukino.Yukino;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.time.Instant;

public final class EmbedUtils {

    private static final Yukino yukino = Yukino.getInstance();
    private static final JDA jda = yukino.getJDA();

    private EmbedUtils() {}

    public static EmbedBuilder getDefaultEmbed() {
        return new EmbedBuilder()
                .setColor(Colors.DEFAULT)
                .setFooter(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.now());
    }

    public static EmbedBuilder getClearEmbed() {
        return new EmbedBuilder()
                .setColor(Colors.DEFAULT);
    }

    public static EmbedBuilder getClearEmbed(EmbedLevel level) {
        return getClearEmbed().setColor(level.getColor());
    }

    public static EmbedBuilder getDefaultEmbed(EmbedLevel level) {
        return getDefaultEmbed().setColor(level.getColor());
    }

}
