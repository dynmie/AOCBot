package me.dynmie.aoc.yukino.utils;

import me.dynmie.aoc.yukino.locale.Lang;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.time.Instant;

public final class EmbedUtils {

    private EmbedUtils() {}

    public static EmbedBuilder getDefaultEmbed(JDA jda) {
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

    public static EmbedBuilder getDefaultEmbed(JDA jda, EmbedLevel level) {
        return getDefaultEmbed(jda).setColor(level.getColor());
    }

    public static EmbedBuilder getErrorEmbed(JDA jda) {
        return getDefaultEmbed(jda).setDescription(Lang.ERROR.get());
    }

}
