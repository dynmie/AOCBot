package me.dynmie.aoc.yukino.utils;

/**
 * @author dynmie
 */
public class ObjectUtils {

    private ObjectUtils() {}

    public static <T> T coalesce(T a, T b) {
        return a == null ? b : a;
    }

    @SafeVarargs
    public static <T> T coalesce(T... params) {
        for (T param : params) {
            if (param == null) continue;
            return param;
        }
        return null;
    }


}
