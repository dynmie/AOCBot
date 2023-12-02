package me.dynmie.aoc.yukino.listeners.impl;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.BotConfig;
import me.dynmie.aoc.yukino.utils.EmbedLevel;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import me.dynmie.aoc.yukino.utils.GuildUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author dynmie
 */
public class AOCApplicationListener {

    private final Database database = Yukino.getInstance().getDatabaseManager().getDatabase();
    private final BotConfig config = Yukino.getInstance().getConfig();

    @SubscribeEvent
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!GuildUtils.isActiveGuild(event.getGuild())) {
            return;
        }

        if (!event.getComponentId().equals("aoc_apply")) {
            return;
        }

        User user = event.getUser();

        event.deferReply(true).queue(h -> database.getAOCMemberByDiscordId(user.getId()).thenAccept(member -> {
            if (member.isPresent()) {
                EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.YOU_ALREADY_MEMBER.get());
                h.editOriginalEmbeds(builder.build()).queue();
                return;
            }
            h.editOriginal("Apply here: " + config.getAOCApplicationLink()).queue();
        }));
    }

}
