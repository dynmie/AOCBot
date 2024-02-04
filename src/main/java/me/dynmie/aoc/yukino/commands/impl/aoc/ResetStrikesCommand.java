package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.EmbedLevel;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import me.dynmie.aoc.yukino.utils.GuildUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dynmie
 */
public class ResetStrikesCommand implements YukinoCommand {

    private final Database database = Yukino.getInstance().getDatabaseManager().getDatabase();

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("resetstrikes", "Reset all strikes from the database")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        User user = event.getUser();

        EmbedBuilder builder = EmbedUtils.getClearEmbed(EmbedLevel.DANGER)
                .setTitle(":octagonal_sign:  Danger")
                .setDescription(Lang.STRIKES_RESET_CONFIRM.get());

        String userId = user.getId();
        long expiry = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);

        Button deleteButton = Button.danger("aoc_strikes_reset:%s:%s".formatted(
                userId,
                expiry
        ), "Continue");

        Button cancelButton = Button.secondary("aoc_strikes_reset_cancel:%s:%s".formatted(
                userId,
                expiry
        ), "Cancel");

        event.replyEmbeds(builder.build()).setActionRow(deleteButton, cancelButton)
                .delay(15, TimeUnit.SECONDS)
                .flatMap(h -> h.editOriginalComponents(ActionRow.of(deleteButton.asDisabled(), cancelButton.asDisabled())))
                .queue();
    }

    @SubscribeEvent
    public void onButton(@NotNull ButtonInteractionEvent event) {
        if (!GuildUtils.isActiveGuild(event.getGuild())) return;
        if (!event.getComponentId().startsWith("aoc_strikes_reset")) return;


        String[] split = event.getComponentId().split(":");

        String userId = split[1];
        long expiry = Long.parseLong(split[2]);

        User user = event.getUser();
        if (!user.getId().equals(userId)) return;

        Guild guild = event.getGuild();
        if (guild == null) return;

        Member guildMember = guild.getMember(event.getUser());
        if (guildMember == null) return;

        if (!guildMember.hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.NO_PERMISSION.get());
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
            return;
        }

        List<Button> buttons = event.getMessage().getButtons().stream().map(Button::asDisabled).toList();
        event.getMessage().editMessageComponents(ActionRow.of(buttons)).queue();

        if (System.currentTimeMillis() > expiry) {
            event.replyEmbeds(EmbedUtils.getClearEmbed().setDescription(Lang.MODAL_EXPIRED.get()).build()).queue();
            return;
        }

        if (event.getComponentId().startsWith("aoc_strikes_reset_cancel")) {
            event.replyEmbeds(EmbedUtils.getClearEmbed().setDescription(Lang.INTERACTION_CANCELLED.get()).build())
                    .queue();
            return;
        }

        event.deferReply().queue(h -> database.resetStrikes().whenComplete((v, t) -> {
            if (t != null) {
                EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                h.editOriginalEmbeds(builder.build()).queue();
                throw new RuntimeException(t);
            }
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.STRIKES_RESET.get());
            h.editOriginalEmbeds(builder.build()).queue();
        }));
    }

}
