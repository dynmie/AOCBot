package me.dynmie.aoc.yukino.listeners;

import me.dynmie.aoc.yukino.commands.CommandListener;
import me.dynmie.aoc.yukino.listeners.impl.StatusListener;
import me.dynmie.jeorge.Injector;
import net.dv8tion.jda.api.JDA;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ListenerManager {

    private final JDA jda;

    private final List<Object> listeners = new ArrayList<>();

    public ListenerManager(Injector injector, JDA jda) {
        this.jda = jda;

        listeners.addAll(Stream.of(
                // COMMANDS
                CommandListener.class,

                // STATUS
                StatusListener.class
        ).map(injector::createInstance).toList());
    }

    public void register() {
        for (Object listener : listeners) {
            jda.addEventListener(listener);
        }
    }

    public List<Object> getListeners() {
        return listeners;
    }
}
