package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import me.dynmie.jeorge.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author dynmie
 */
public class HoursCommand implements YukinoCommand {

    private final Database database;

    @Inject
    public HoursCommand(Database database) {
        this.database = database;
    }

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("hours", "Edit a member's volunteer hours")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .setGuildOnly(true)
                .addSubcommands(
                        new SubcommandData("add", "Add volunteer hours for a member")
                                 .addOption(OptionType.USER, "member", "The discord of the member", true)
                                 .addOption(OptionType.INTEGER, "hours", "The amount of hours to add", true),
                        new SubcommandData("set", "Set volunteer hours for a member")
                                .addOption(OptionType.USER, "member", "The discord of the member", true)
                                .addOption(OptionType.INTEGER, "hours", "The amount of hours to set", true),
                        new SubcommandData("remove", "Remove volunteer hours for a member")
                                .addOption(OptionType.USER, "member", "The discord of the member", true)
                                .addOption(OptionType.INTEGER, "hours", "The amount of hours to remove", true)
                );
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        String subcommandName = event.getSubcommandName();
        if (subcommandName == null) {
            event.replyEmbeds(EmbedUtils.getErrorEmbed(event.getJDA()).build()).setEphemeral(true).queue();
            return;
        }

        User targetUser = Optional.ofNullable(event.getOption("member"))
                .map(OptionMapping::getAsUser).orElseThrow();

        long hours = Optional.ofNullable(event.getOption("hours"))
                .map(OptionMapping::getAsLong).orElseThrow();

        event.deferReply().queue(hook -> database.getAOCMemberByDiscordId(targetUser.getId()).whenComplete((memberOptional, throwable) -> {
            if (throwable != null) throw new RuntimeException(throwable);
            memberOptional.ifPresentOrElse(
                    member -> {
                        switch (subcommandName) {
                            case "add" -> {
                                member.setHours(member.getHours() + Math.max(hours, 0));
                                database.saveAOCMember(member).thenAccept((v) -> {
                                    EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                            .setDescription(Lang.HOURS_SET.format(
                                                    member.getFirstName(),
                                                    member.getHours()
                                            ));
                                    hook.editOriginalEmbeds(builder.build()).queue();
                                }).exceptionally((t) -> {
                                    throw new RuntimeException(t);
                                });
                            }
                            case "set" -> {
                                member.setHours(Math.max(hours, 0));
                                database.saveAOCMember(member).thenAccept((v) -> {
                                    EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                            .setDescription(Lang.HOURS_SET.format(
                                                    member.getFirstName(),
                                                    member.getHours()
                                            ));
                                    hook.editOriginalEmbeds(builder.build()).queue();
                                }).exceptionally((t) -> {
                                    throw new RuntimeException(t);
                                });
                            }
                            case "remove" -> {
                                long actualHours = member.getHours() - Math.max(hours, 0);
                                if (actualHours < 0) actualHours = 0;

                                member.setHours(actualHours);
                                database.saveAOCMember(member).thenAccept((v) -> {
                                    EmbedBuilder builder = EmbedUtils.getClearEmbed()
                                            .setDescription(Lang.HOURS_SET.format(
                                                    member.getFirstName(),
                                                    member.getHours()
                                            ));
                                    hook.editOriginalEmbeds(builder.build()).queue();
                                }).exceptionally((t) -> {
                                    throw new RuntimeException(t);
                                });
                            }
                        }
                    },
                    () -> {
                        EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                        hook.editOriginalEmbeds(builder.build()).queue();
                    }
            );
        }));


    }

}
