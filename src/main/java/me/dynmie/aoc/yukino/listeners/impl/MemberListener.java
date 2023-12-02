package me.dynmie.aoc.yukino.listeners.impl;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import me.dynmie.aoc.yukino.utils.GuildUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.jetbrains.annotations.NotNull;

/**
 * @author dynmie
 */
public class MemberListener {

    private final Database database = Yukino.getInstance().getDatabaseManager().getDatabase();

    @SubscribeEvent
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!GuildUtils.isActiveGuild(event.getGuild())) {
            return;
        }

        if (event.getComponentId().startsWith("aoc_member_delete")) {

            String[] split = event.getComponentId().split(":");

            String userId = split[1];
            long expiry = Long.parseLong(split[2]);
            String targetId = split[3];

            if (System.currentTimeMillis() > expiry) {
                event.editButton(event.getComponent().asDisabled()).queue();
                return;
            }

            if (!event.getUser().getId().equals(userId)) {
                EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.BUTTON_NOT_YOURS.get());
                event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                return;
            }

            Guild guild = event.getGuild();
            if (guild == null) return;

            Member guildMember = guild.getMember(event.getUser());
            if (guildMember == null) return;

            if (!guildMember.hasPermission(Permission.ADMINISTRATOR)) {
                EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.NO_PERMISSION.get());
                event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                return;
            }

            event.getMessage().editMessageComponents(ActionRow.of(event.getButton().asDisabled())).queue();

            event.deferReply().queue(hook -> database.getAOCMemberByDiscordId(targetId).thenAccept(
                    lookup -> lookup.ifPresentOrElse(member -> database.deleteAOCMember(member).thenAccept(v -> {
                        EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                .setDescription(Lang.MEMBER_REMOVED.format(MarkdownSanitizer.escape(member.getFullName())));
                        hook.editOriginalEmbeds(builder.build()).queue();
                    }), () -> {
                        EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                        hook.editOriginalEmbeds(builder.build()).queue();
                    }))
            );

        }



    }

}
