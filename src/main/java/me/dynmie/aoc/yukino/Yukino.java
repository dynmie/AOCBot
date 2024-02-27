package me.dynmie.aoc.yukino;

import me.dynmie.aoc.yukino.commands.CommandManager;
import me.dynmie.aoc.yukino.database.DatabaseManager;
import me.dynmie.aoc.yukino.dependency.YukinoBinder;
import me.dynmie.aoc.yukino.listeners.ListenerManager;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.BotConfig;
import me.dynmie.aoc.yukino.utils.Config;
import me.dynmie.aoc.yukino.utils.Token;
import me.dynmie.jeorge.Injector;
import me.dynmie.jeorge.Jeorge;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.File;
import java.util.Date;
import java.util.EnumSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class Yukino {
    public static final Logger LOGGER = Logger.getLogger(Yukino.class.getName());

    static {
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return String.format("[%1$tF %1$tT] [%2$s] %3$s %n",
                        new Date(record.getMillis()),
                        record.getLevel().getLocalizedName(),
                        record.getMessage());
            }
        });
        LOGGER.addHandler(handler);
    }

    private final Token token;
    private final BotConfig config;

    private Injector injector;

    private DatabaseManager databaseManager;

    private ListenerManager listenerManager;
    private CommandManager commandManager;

    private long startMillis = 0;

    private Yukino(Token token, BotConfig config) {
        this.token = token;
        this.config = config;
    }

    public void load() throws InterruptedException {
        //noinspection ResultOfMethodCallIgnored
        getFolderPath().mkdir();
        Lang.init();

        // LOAD JDA
        JDA jda = JDABuilder.create(token.token(), EnumSet.allOf(GatewayIntent.class))
                .setEventManager(new AnnotatedEventManager())
                .setActivity(Activity.listening(config.getStatus()))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        // LOAD MANAGERS
        this.databaseManager = new DatabaseManager(config);
        databaseManager.init();

        injector = Jeorge.createInjector(new YukinoBinder(this, jda, config, databaseManager));

        this.commandManager = new CommandManager(injector, jda);

        this.listenerManager = new ListenerManager(injector, jda);
        listenerManager.register();

        // WAIT FOR JDA TO CONNECT TO DISCORD
        jda.awaitReady();
        startMillis = System.currentTimeMillis();

        commandManager.registerGuild(config.getGuildId());
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ListenerManager getListenerManager() {
        return listenerManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public Injector getInjector() {
        return injector;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public static File getFolderPath() {
        return new File("yukino");
    }

    public static void main(String[] args) {
        LOGGER.info("""
        
        
        ==============================================================================
        Welcome to Yukino (AOC).
        Created by dynmie, all rights reserved.
        If any issues occur while using this program, contact the bot developer
        for help.
        ==============================================================================
        """);

        Config conf = Config.getDefaultConfig(new File("yukino/yukino.properties"), "yukino.properties");
        BotConfig botConfig = new BotConfig(conf);

        final Token token;
        if (args.length > 0) {
            token = new Token(args[0]);
            LOGGER.info("Using argument defined token.");
        } else {
            token = new Token(conf.getProperty("TOKEN", ""));
        }

        Yukino yukino = new Yukino(token, botConfig);

        try {
            yukino.load();
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.severe("Failed to load.");
            System.exit(-1);
        }
    }
}