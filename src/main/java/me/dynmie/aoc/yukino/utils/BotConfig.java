package me.dynmie.aoc.yukino.utils;

public class BotConfig {

    private final Config config;

    public BotConfig(Config config) {
        this.config = config;
    }

    public String getStatus() {
        return config.getProperty("STATUS", "the latest");
    }

    public String getGuildId() {
        return config.getProperty("GUILD", "");
    }

    public String getDatabaseType() {
        return config.getProperty("DATABASE");
    }

    public String getDatabaseHost() {
        return config.getProperty("DATABASE_HOST");
    }

    public String getDatabasePort() {
        return config.getProperty("DATABASE_PORT");
    }

    public String getDatabaseUsername() {
        return config.getProperty("DATABASE_USERNAME");
    }

    public String getDatabasePassword() {
        return config.getProperty("DATABASE_PASSWORD");
    }

    public String getDatabaseNameMembers() {
        return config.getProperty("DATABASE_DB_MEMBERS");
    }

    public String getDatabaseNameApplications() {
        return config.getProperty("DATABASE_DB_APPLICATIONS");
    }

    public String getMongoDatabase() {
        return config.getProperty("MONGO_DATABASE");
    }

    public String getMongoURI() {
        return config.getProperty("MONGO_URI");
    }

    public String getAOCApplicationLink() {
        return config.getProperty("AOC_APPLICATION_LINK");
    }

    public void save() {
        config.save();
    }
}
