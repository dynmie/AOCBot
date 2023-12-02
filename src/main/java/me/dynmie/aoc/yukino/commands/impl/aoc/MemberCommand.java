package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.aoc.AOCMember;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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
import java.util.UUID;
import java.util.concurrent.Executors;
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
                            new ArrayList<>()
                    );

                    database.saveAOCMember(member).thenAccept(v -> {
                        EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                .setDescription(Lang.MEMBER_SAVED.format(MarkdownSanitizer.escape(member.getFullName())));
                        hook.editOriginalEmbeds(builder.build()).queue();

                        Member targetGMember = guild.getMember(event.getUser());
                        if (targetGMember == null) {
                            return;
                        }

                        guild.modifyNickname(targetGMember, member.getFullName()).queue();
                    });
                }).exceptionally(t -> {
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

                            hook.editOriginalEmbeds(builder.build()).setActionRow(deleteButton).queue();

                            Yukino.getInstance().getScheduler().schedule(() ->
                                    hook.editOriginalComponents(ActionRow.of(deleteButton.asDisabled())).queue(),
                                    15,
                                    TimeUnit.SECONDS
                            );
                        }, () -> {
                            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                            hook.editOriginalEmbeds(builder.build()).queue();
                        }))
                );
            }
        }
    }
}
