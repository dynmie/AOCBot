package me.dynmie.aoc.yukino.utils;

import java.time.Duration;

/**
 * @author dynmie
 */
public final class DurationUtils {

    private DurationUtils() {}

    public static String formatLong(long wow) {
        return Duration.ofMillis(wow).toMinutes() + "";
    }

}
