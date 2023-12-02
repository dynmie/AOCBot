package me.dynmie.aoc.yukino.listeners.impl;

import me.dynmie.aoc.yukino.Yukino;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class StatusListener {

    @SubscribeEvent
    public void onReady(@NotNull ReadyEvent event) {
        Yukino.LOGGER.info(event.getJDA().getSelfUser().getAsTag() + " is ready.");
    }

}
