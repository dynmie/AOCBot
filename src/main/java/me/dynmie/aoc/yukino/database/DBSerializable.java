package me.dynmie.aoc.yukino.database;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * You must have a static deserialize method to use this!
 *
 * @author dynmie
 */
public interface DBSerializable {

    /**
     * Creates a Map representation of this class.
     *
     * @return Map containing the current state of this class
     */
    @NotNull
    Map<String, Object> serialize();

    static <T extends DBSerializable> T deserialize(Map<String, Object> map, Class<T> clazz) {
        Method method;
        try {
            method = clazz.getMethod("deserialize", Map.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("There is no static deserialize method in this class!", e);
        }

        Object object;
        try {
            object = method.invoke(null, map);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return clazz.cast(object);
    }

}
