package me.dynmie.aoc.yukino.listeners;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.commands.CommandListener;
import me.dynmie.aoc.yukino.listeners.impl.AOCApplicationListener;
import me.dynmie.aoc.yukino.listeners.impl.MemberListener;
import me.dynmie.aoc.yukino.listeners.impl.StatusListener;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {

    private final Yukino yukino = Yukino.getInstance();

    public static final List<Object> LISTENERS = new ArrayList<>(List.of(
            // COMMANDS
            new CommandListener(),

            // AOC
            new AOCApplicationListener(),
            new MemberListener(),

            // STATUS
            new StatusListener()
    ));

    public void register() {
        for (Object listener : LISTENERS) {
            yukino.getJDA().addEventListener(listener);
        }
    }

}
