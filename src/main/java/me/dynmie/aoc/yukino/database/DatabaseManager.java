package me.dynmie.aoc.yukino.database;

import me.dynmie.aoc.yukino.Yukino;

public class DatabaseManager {

    private static final Yukino yukino = Yukino.getInstance();

    private Database database;

    public void init() {
        String databaseType = yukino.getConfig().getDatabaseType();

        database = DatabaseFactory.getDatabase(databaseType);

        if (database == null) {
            Yukino.LOGGER.severe("The database hasn't been set up properly! Make sure you set up the database before you use the bot.");
            throw new RuntimeException("Failed to initialize the database.");
        }

        database.connect();
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isClosed() {
        if (database == null) return true;
        return database.isClosed();
    }

}
