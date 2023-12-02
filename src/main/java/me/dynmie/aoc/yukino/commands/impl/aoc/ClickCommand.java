package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.locale.Lang;
import me.dynmie.aoc.yukino.utils.Colors;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

/**
 * @author dynmie
 */
public class ClickCommand implements YukinoCommand {
    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("click", "Set the click channel")
                .setGuildOnly(true)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
                .addOption(OptionType.CHANNEL, "channel", "The channel for the click modal", true);
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) return;

        Member guildMember = guild.getMember(event.getUser());
        if (guildMember == null) return;

        if (!guildMember.hasPermission(Permission.ADMINISTRATOR)) {
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.NO_PERMISSION.get());
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
            return;
        }

        OptionMapping channelMapping = event.getOption("channel");
        if (channelMapping == null) {
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
            return;
        }

        GuildChannelUnion channel = channelMapping.getAsChannel();
        if (channel.getType() != ChannelType.TEXT) {
            EmbedBuilder builder = EmbedUtils.getClearEmbed().setDescription(Lang.ERROR.get());
            event.replyEmbeds(builder.build()).setEphemeral(true).queue();
            return;
        }


        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Colors.DEFAULT)
                .setTitle("Apply to the Adopt our Campus Club")
                .setDescription("Click below to start your application.");

        event.deferReply(true).queue(h -> channel.asGuildMessageChannel().sendMessageEmbeds(builder.build())
                .addActionRow(Button.primary("aoc_apply", "Apply"))
                .queue(v ->
                        h.editOriginal("Done.").queue()
                ));

    }
}
