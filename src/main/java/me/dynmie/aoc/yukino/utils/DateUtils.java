package me.dynmie.aoc.yukino.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author dynmie
 */
public class DateUtils {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

    static {
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
    }

    public static String formatDate(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String formatMillis(long millis) {
        return simpleDateFormat.format(new Date(millis));
    }

}
