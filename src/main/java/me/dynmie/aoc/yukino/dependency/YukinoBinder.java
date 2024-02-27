package me.dynmie.aoc.yukino.dependency;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.database.DatabaseManager;
import me.dynmie.aoc.yukino.utils.BotConfig;
import me.dynmie.jeorge.Binder;
import net.dv8tion.jda.api.JDA;

/**
 * @author dynmie
 */
public class YukinoBinder extends Binder {
    private final Yukino yukino;
    private final JDA jda;
    private final BotConfig config;
    private final DatabaseManager databaseManager;

    public YukinoBinder(Yukino yukino, JDA jda, BotConfig config, DatabaseManager databaseManager) {
        this.yukino = yukino;
        this.jda = jda;
        this.config = config;
        this.databaseManager = databaseManager;
    }

    @Override
    public void configure() {
        bind(JDA.class, jda);
        bind(BotConfig.class, config);
        bind(Yukino.class, yukino);
        bind(Database.class, databaseManager.getDatabase());
    }
}
