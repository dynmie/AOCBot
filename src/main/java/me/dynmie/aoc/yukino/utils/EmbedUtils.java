package me.dynmie.aoc.yukino.utils;

import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.jeorge.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

import java.time.Instant;

public final class EmbedUtils {

    @Inject
    private static JDA jda;

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

    public static EmbedBuilder getErrorEmbed() {
        return getDefaultEmbed().setDescription(Lang.ERROR.get());
    }

}
