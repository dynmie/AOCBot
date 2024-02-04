package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.DateUtils;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.jetbrains.annotations.NotNull;

/**
 * @author dynmie
 */
public class LookupCommand implements YukinoCommand {

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("lookup", "Look up a member")
                .setGuildOnly(true)
                .addOption(OptionType.USER, "member", "The discord of the member", true);
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        OptionMapping mapping = event.getOption("member");
        if (mapping == null) {
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
            return;
        }

        event.deferReply().queue(hook -> {
            User user = mapping.getAsUser();
            Yukino.getInstance().getDatabaseManager().getDatabase().getAOCMemberByDiscordId(user.getId()).thenAccept(optional ->
                    optional.ifPresentOrElse(member -> {
                        MessageEmbed embed = EmbedUtils.getDefaultEmbed()
                                .setTitle("%s".formatted(MarkdownSanitizer.escape(member.getFullName())))
                                .addField("Strikes", "%s strikes".formatted(member.getStrikes().size()), true)
                                .addField("Joined", DateUtils.formatMillis(member.getJoined()), true)
                                .build();

                        hook.editOriginalEmbeds(embed).queue();
                    }, () ->{
                        EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.MEMBER_NOT_EXIST.get());
                        hook.editOriginalEmbeds(builder.build()).queue();
                    }));
        });
    }

}
