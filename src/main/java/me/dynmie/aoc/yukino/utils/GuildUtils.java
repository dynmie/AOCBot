package me.dynmie.aoc.yukino.utils;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author dynmie
 */
public class GuildUtils {

    public static boolean isActiveGuild(@NotNull String guildId, @Nullable Guild guild) {
        if (guild == null) return false;

        return guildId.equals(guild.getId());
    }

}
