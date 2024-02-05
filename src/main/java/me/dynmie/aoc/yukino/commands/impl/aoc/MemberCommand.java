package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.aoc.AOCMember;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.locale.Lang;
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
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author dynmie
 */
public class MemberCommand implements YukinoCommand {

    private final Database database = Yukino.getInstance().getDatabaseManager().getDatabase();

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("member", "Edit members")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .setGuildOnly(true)
                .addSubcommands(
                        new SubcommandData("add", "Add a member")
                                .addOption(OptionType.USER, "member", "The discord of the member", true)
                                .addOption(OptionType.STRING, "first_name", "The first name of the member", true)
                                .addOption(OptionType.STRING, "last_name", "The last name of the member", true),
                        new SubcommandData("rename", "Rename a member")
                                .addOption(OptionType.USER, "member", "The discord of the member", true)
                                .addOption(OptionType.STRING, "first_name", "The first name of the member", true)
                                .addOption(OptionType.STRING, "last_name", "The last name of the member", true),
                        new SubcommandData("remove", "Remove a member from AOC")
                                .addOption(OptionType.USER, "member", "The discord of the member", true)
                );
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
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

        String guildId = guild.getId();

        switch (subcommandName) {
            case "add" -> {

                OptionMapping memberMapping = event.getOption("member");
                if (memberMapping == null) {
                    EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    return;
                }
                User user = memberMapping.getAsUser();

                OptionMapping firstNameMapping = event.getOption("first_name");
                if (firstNameMapping == null) {
                    EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    return;
                }
                String firstName = firstNameMapping.getAsString();

                OptionMapping lastNameMapping = event.getOption("last_name");
                if (lastNameMapping == null) {
                    EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    return;
                }
                String lastName = lastNameMapping.getAsString();

                event.deferReply().queue(hook -> database.getAOCMemberByDiscordId(user.getId()).thenAccept(lookup -> {
                    if (lookup.isPresent()) {
                        EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_ALREADY_EXIST.get());
                        hook.editOriginalEmbeds(builder.build()).queue();
                        return;
                    }

                    AOCMember member = new AOCMember(
                            UUID.randomUUID(),
                            user.getId(),
                            System.currentTimeMillis(),
                            firstName.trim(),
                            lastName.trim(),
                            new ArrayList<>(),
                            0
                    );

                    database.saveAOCMember(member).thenAccept(v -> {
                        EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                .setDescription(Lang.MEMBER_SAVED.format(MarkdownSanitizer.escape(member.getFullName())));
                        hook.editOriginalEmbeds(builder.build()).queue();

                        Guild g = event.getJDA().getGuildById(guildId);
                        if (g == null) return;
                        Member targetGMember = g.getMember(user);
                        if (targetGMember == null) return;
                        g.modifyNickname(targetGMember, member.getFullName()).queue();
                    });
                }).exceptionally(t -> {
                    throw new RuntimeException(t);
                }));

            }

            case "rename" -> {
                OptionMapping memberMapping = event.getOption("member");
                if (memberMapping == null) {
                    EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    return;
                }
                User user = memberMapping.getAsUser();

                OptionMapping firstNameMapping = event.getOption("first_name");
                if (firstNameMapping == null) {
                    EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    return;
                }
                String firstName = firstNameMapping.getAsString();

                OptionMapping lastNameMapping = event.getOption("last_name");
                if (lastNameMapping == null) {
                    EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    return;
                }
                String lastName = lastNameMapping.getAsString();

                event.deferReply().queue(hook -> database.getAOCMemberByDiscordId(user.getId()).thenAccept(
                        lookup -> lookup.ifPresentOrElse(member -> {
                            member.setFirstName(firstName);
                            member.setLastName(lastName);
                            database.saveAOCMember(member).thenAccept(v -> {
                                EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                        .setDescription(Lang.MEMBER_SAVED.format(MarkdownSanitizer.escape(member.getFullName())));
                                hook.editOriginalEmbeds(builder.build()).queue();

                                Guild g = event.getJDA().getGuildById(guildId);
                                if (g == null) return;
                                Member targetGMember = g.getMember(user);
                                if (targetGMember == null) return;
                                g.modifyNickname(targetGMember, member.getFullName()).queue();
                            }).exceptionally(t -> {
                                throw new RuntimeException(t);
                            });
                        }, () -> {
                            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                            hook.editOriginalEmbeds(builder.build()).queue();
                        })).exceptionally(t -> {
                    throw new RuntimeException(t);
                }));
            }

            case "remove" -> {
                OptionMapping memberMapping = event.getOption("member");
                if (memberMapping == null) {
                    EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
                    event.replyEmbeds(builder.build()).setEphemeral(true).queue();
                    return;
                }
                User user = memberMapping.getAsUser();

                event.deferReply().queue(hook -> database.getAOCMemberByDiscordId(user.getId()).thenAccept(lookup ->
                        lookup.ifPresentOrElse(member -> {
                            EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                    .setTitle("Member Remove")
                                    .setDescription(Lang.MEMBER_REMOVE_CONFIRM.format(MarkdownSanitizer.escape(member.getFullName())));

                            String userId = user.getId();
                            long expiry = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
                            String targetId = member.getDiscordId();

                            Button deleteButton = Button.danger("aoc_member_delete:%s:%s:%s".formatted(
                                            userId,
                                            expiry,
                                            targetId
                            ), "Remove");

                            Button cancelButton = Button.secondary("aoc_member_delete_cancel:%s:%s:%s".formatted(
                                    userId,
                                    expiry,
                                    targetId
                            ), "Cancel");

                            hook.editOriginalEmbeds(builder.build()).setActionRow(deleteButton, cancelButton)
                                    .delay(15, TimeUnit.SECONDS)
                                    .flatMap(h -> h.editMessageComponents(ActionRow.of(deleteButton.asDisabled(), cancelButton.asDisabled())))
                                    .queue();
                        }, () -> {
                            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                            hook.editOriginalEmbeds(builder.build()).queue();
                        }))
                );
            }
        }
    }

    @SubscribeEvent
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!GuildUtils.isActiveGuild(event.getGuild())) {
            return;
        }

        if (event.getComponentId().startsWith("aoc_member_delete")) {

            String[] split = event.getComponentId().split(":");

            String id = split[0];
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

            List<Button> buttons = event.getMessage().getButtons().stream().map(Button::asDisabled).toList();
            event.getMessage().editMessageComponents(ActionRow.of(buttons)).queue();

            if (System.currentTimeMillis() > expiry) {
                event.replyEmbeds(EmbedUtils.getClearEmbed().setDescription(Lang.MODAL_EXPIRED.get()).build()).queue();
                return;
            }

            if (id.equals("aoc_member_delete_cancel")) {
                event.replyEmbeds(EmbedUtils.getClearEmbed().setDescription(Lang.INTERACTION_CANCELLED.get()).build())
                        .setEphemeral(true)
                        .queue();
                return;
            }

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
