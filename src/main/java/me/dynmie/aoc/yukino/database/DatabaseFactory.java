package me.dynmie.aoc.yukino.database;

import me.dynmie.aoc.yukino.database.impl.YMongoDatabase;

public final class DatabaseFactory {

    private DatabaseFactory() {}

    public static Database getDatabase(String type) {
        if (type == null) return null;

        if ("mongo".equalsIgnoreCase(type)) {
            return new YMongoDatabase();
        }

        return null;
    }

}
