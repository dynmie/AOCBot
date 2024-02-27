package me.dynmie.aoc.yukino.database;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.utils.BotConfig;

public class DatabaseManager {

    private final BotConfig config;

    private Database database;

    public DatabaseManager(BotConfig config) {
        this.config = config;
    }

    public void init() {
        String databaseType = config.getDatabaseType();

        database = Database.of(databaseType, config);

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
