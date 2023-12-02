package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.aoc.strikes.Strike;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.DateUtils;
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
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

/**
 * @author dynmie
 */
public class StrikeCommand implements YukinoCommand {

    private final Database database = Yukino.getInstance().getDatabaseManager().getDatabase();

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("strike", "Strike a member")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .setGuildOnly(true)
                .addSubcommands(
                        new SubcommandData("remove", "Remove a strike")
                                .addOption(OptionType.USER, "member", "The discord of the member", true)
                                .addOption(OptionType.INTEGER, "id", "The ID of the strike", true),
                        new SubcommandData("add", "Add a strike")
                                .addOption(OptionType.USER, "member", "The discord of the member", true)
                                .addOption(OptionType.STRING, "reason", "Reason for the strike", true),
                        new SubcommandData("list", "List all strikes")
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

        OptionMapping memberMapping = event.getOption("member");
        if (memberMapping == null) {
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
            return;
        }
        User user = memberMapping.getAsUser();

        switch (subcommandName) {
            case "add" -> {
                OptionMapping reasonMapping = event.getOption("reason");
                if (reasonMapping == null) {
                    event.reply(Lang.ERROR.get()).setEphemeral(true).queue();
                    return;
                }
                String reason = reasonMapping.getAsString();

                event.deferReply().queue(h -> database.getAOCMemberByDiscordId(user.getId()).thenAccept(optional -> optional.ifPresentOrElse(
                        member -> {
                            Strike strike = new Strike(
                                    System.currentTimeMillis(),
                                    reason
                            );
                            member.getStrikes().add(strike);

                            database.saveAOCMember(member).thenAccept(v -> {
                                EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                        .setDescription(Lang.STRIKE_CREATED.format(
                                                member.getFullName(),
                                                MarkdownSanitizer.escape(strike.getReason()),
                                                member.getStrikes().size()
                                        ));
                                h.editOriginalEmbeds(builder.build()).queue();
                            });
                        },
                        () -> {
                            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                            h.editOriginalEmbeds(builder.build()).queue();
                        }
                )));
            }
            case "remove" -> {
                OptionMapping idMapping = event.getOption("id");
                if (idMapping == null) {
                    event.reply(Lang.ERROR.get()).setEphemeral(true).queue();
                    return;
                }
                int id = idMapping.getAsInt();

                event.deferReply().queue(h -> database.getAOCMemberByDiscordId(user.getId()).thenAccept(optional -> optional.ifPresentOrElse(
                        member -> {
                            Strike strike;
                            try {
                                strike = member.getStrikes().get(id - 1);
                            } catch (IndexOutOfBoundsException ignored) {
                                strike = null;
                            }
                            if (strike == null) {
                                h.editOriginalEmbeds(EmbedUtils.getClearEmbed()
                                        .setDescription(Lang.STRIKE_NOT_EXIST.get())
                                        .build()).queue();
                                return;
                            }
                            member.getStrikes().remove(strike);
                            database.saveAOCMember(member).thenAccept(v ->
                                    h.editOriginal(Lang.MEMBER_SAVED.format(MarkdownSanitizer.escape(member.getFullName()))).queue()
                            );
                        },
                        () -> {
                            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                            h.editOriginalEmbeds(builder.build()).queue();
                        }
                )));
            }
            case "list" ->
                    event.deferReply().queue(h -> database.getAOCMemberByDiscordId(user.getId()).thenAccept(optional -> optional.ifPresentOrElse(
                            member -> {
                                EmbedBuilder builder = EmbedUtils.getDefaultEmbed()
                                        .setTitle("%s's Strikes (%s)".formatted(
                                                MarkdownSanitizer.escape(member.getFullName()),
                                                member.getStrikes().size()
                                        ));


                                StringJoiner joiner = new StringJoiner("\n");
                                for (int i = 0; i < member.getStrikes().size(); i++) {
                                    Strike strike = member.getStrikes().get(i);
                                    joiner.add("#%s - %s - %s".formatted(
                                            i + 1,
                                            DateUtils.formatMillis(strike.getWhen()),
                                            strike.getReason().trim().replace("```", "")
                                    ));
                                }
                                if (member.getStrikes().isEmpty()) {
                                    builder.setDescription("This member has no strikes.");
                                } else {
                                    builder.setDescription("```" + joiner + "```");
                                }

                                h.editOriginalEmbeds(builder.build()).queue();
                            },
                            () -> {
                                EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                                h.editOriginalEmbeds(builder.build()).queue();
                            }
                    )));
        }
    }

}
