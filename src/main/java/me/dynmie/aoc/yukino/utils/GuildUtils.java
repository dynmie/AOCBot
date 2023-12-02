package me.dynmie.aoc.yukino.utils;

import me.dynmie.aoc.yukino.Yukino;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

/**
 * @author dynmie
 */
public class GuildUtils {

    public static boolean isActiveGuild(@Nullable Guild guild) {
        if (guild == null) return false;

        return Yukino.getInstance().getConfig().getGuildId().equals(guild.getId());
    }

}
