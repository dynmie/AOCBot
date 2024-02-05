package me.dynmie.aoc.yukino.commands.impl.aoc;

import me.dynmie.aoc.yukino.Yukino;
import me.dynmie.aoc.yukino.aoc.AOCMember;
import me.dynmie.aoc.yukino.commands.YukinoCommand;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author dynmie
 */
public class LeaderboardCommand implements YukinoCommand {

    private static final int MEMBERS_PER_PAGE = 10;
    private final Database database = Yukino.getInstance().getDatabaseManager().getDatabase();

    @Override
    public @NotNull SlashCommandData getSlashCommandData() {
        return Commands.slash("leaderboard", "Show the top members");
    }

    @Override
    public void executeSlashCommand(@NotNull SlashCommandInteractionEvent event) {
        int page = 1;
        event.deferReply().queue(hook -> database.getTopAOCMembersByHours(page, MEMBERS_PER_PAGE).whenComplete((members, throwable) -> {
            if (throwable != null) throw new RuntimeException(throwable);
            database.getAOCMemberCount().whenComplete((count, t) -> {
                if (t != null) throw new RuntimeException(t);
                int maxPages = (int) Math.ceil((double) count / MEMBERS_PER_PAGE);
                hook.editOriginalEmbeds(generateEmbed(page, members, event.getUser().getId()))
                        .setComponents(generateActionRow(page, maxPages))
                        .queue();
            });
        }));
    }

    @SubscribeEvent
    public void onButton(@NotNull ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("aoc_leaderboard_")) return;
        String[] split = event.getComponentId().replaceFirst("aoc_leaderboard_", "").split(":");

        String id = split[0];
        int currentPage = Integer.parseInt(split[1]);

        event.deferEdit().queue(hook -> {
            switch (id) {
                case "previous" ->
                        database.getTopAOCMembersByHours(Math.max(currentPage - 1, 1), MEMBERS_PER_PAGE).whenComplete((members, throwable) -> {
                            if (throwable != null) throw new RuntimeException(throwable);
                            database.getAOCMemberCount().whenComplete((count, t) -> {
                                if (t != null) throw new RuntimeException(t);
                                int maxPages = (int) Math.ceil((double) count / MEMBERS_PER_PAGE);
                                hook.editOriginalEmbeds(generateEmbed(Math.max(currentPage - 1, 1), members, event.getUser().getId()))
                                        .setComponents(generateActionRow(Math.max(currentPage - 1, 1), maxPages))
                                        .queue();
                            });
                        });
                case "first_page" ->
                        database.getTopAOCMembersByHours(1, MEMBERS_PER_PAGE).whenComplete((members, throwable) -> {
                            if (throwable != null) throw new RuntimeException(throwable);
                            database.getAOCMemberCount().whenComplete((count, t) -> {
                                if (t != null) throw new RuntimeException(t);
                                int maxPages = (int) Math.ceil((double) count / MEMBERS_PER_PAGE);
                                hook.editOriginalEmbeds(generateEmbed(1, members, event.getUser().getId()))
                                        .setComponents(generateActionRow(1, maxPages))
                                        .queue();
                            });
                        });
//                case "you" ->
//                        database.getTopAOCMembersByHours(Math.max(currentPage + 1, 1), MEMBERS_PER_PAGE).whenComplete((members, throwable) -> {
//                            if (throwable != null) throw new RuntimeException(throwable);
//                            database.getAOCMemberCount().whenComplete((count, t) -> {
//                                if (t != null) throw new RuntimeException(t);
//                                int maxPages = (int) Math.ceil((double) (count) / MEMBERS_PER_PAGE);
//                                hook.editOriginalEmbeds(generateEmbed(Math.max(currentPage - 1, 1), members, event.getUser().getId()))
//                                        .setComponents(generateActionRow(Math.max(currentPage + 1, 1), maxPages))
//                                        .queue();
//                            });
//                        });
                case "refresh" ->
                        database.getTopAOCMembersByHours(Math.max(currentPage, 1), MEMBERS_PER_PAGE).whenComplete((members, throwable) -> {
                            if (throwable != null) throw new RuntimeException(throwable);
                            database.getAOCMemberCount().whenComplete((count, t) -> {
                                if (t != null) throw new RuntimeException(t);
                                int maxPages = (int) Math.ceil((double) count / MEMBERS_PER_PAGE);
                                hook.editOriginalEmbeds(generateEmbed(Math.max(currentPage, 1), members, event.getUser().getId()))
                                        .setComponents(generateActionRow(Math.max(currentPage, 1), maxPages))
                                        .queue();
                            });
                        });
                case "next" ->
                        database.getTopAOCMembersByHours(Math.max(currentPage + 1, 1), MEMBERS_PER_PAGE).whenComplete((members, throwable) -> {
                            if (throwable != null) throw new RuntimeException(throwable);
                            database.getAOCMemberCount().whenComplete((count, t) -> {
                                if (t != null) throw new RuntimeException(t);
                                int maxPages = (int) Math.ceil((double) count / MEMBERS_PER_PAGE);
                                hook.editOriginalEmbeds(generateEmbed(Math.max(currentPage + 1, 1), members, event.getUser().getId()))
                                        .setComponents(generateActionRow(Math.max(currentPage + 1, 1), maxPages))
                                        .queue();
                            });
                        });
            }
        });
    }

    private static ActionRow generateActionRow(int page, int maxPages) {
        Button previousButton = Button.primary("aoc_leaderboard_previous:%s".formatted(page), Emoji.fromUnicode("U+25C0"));
        Button firstPageButton = Button.secondary("aoc_leaderboard_first_page:%s".formatted(page), Emoji.fromUnicode("U+23EA"));
//        Button youButton = Button.secondary("aoc_leaderboard_you:%s".formatted(page), Emoji.fromUnicode("U+1F4CD"));
        Button refreshButton = Button.secondary("aoc_leaderboard_refresh:%s".formatted(page), Emoji.fromUnicode("U+1F504"));
        Button nextButton = Button.primary("aoc_leaderboard_next:%s".formatted(page), Emoji.fromUnicode("U+25B6"));
        if (page + 1 > maxPages) {
            nextButton = nextButton.asDisabled();
        }

        if (page - 1 < 1) {
            previousButton = previousButton.asDisabled();
        }

        return ActionRow.of(previousButton, firstPageButton, refreshButton, nextButton);
    }

    private static MessageEmbed generateEmbed(int page, List<AOCMember> members, String highlightId) {
        StringJoiner joiner = new StringJoiner("\n");
        for (int i = 0; i < members.size(); i++) {
            int rank = ((page - 1) * MEMBERS_PER_PAGE) + i + 1;

            AOCMember member = members.get(i);
            boolean bold = member.getDiscordId().equals(highlightId);

            StringBuilder builder = new StringBuilder();
            if (rank <= 3) {
                switch (rank) {
                    case 1 -> builder.append(":first_place:   - ");
                    case 2 -> builder.append(":second_place:   - ");
                    case 3 -> builder.append(":third_place:   - ");
                }
            } else if (rank < 10) {
                builder.append("`#%s ` - ".formatted(rank));
            } else {
                builder.append("`#%s` - ".formatted(rank));
            }

            builder.append(member.getHours());
            builder.append(" hrs ");
            builder.append("<@").append(member.getDiscordId()).append(">");

            String ret = builder.toString();
            if (bold) {
                ret = "**" + ret + " :round_pushpin:**";
            }

            joiner.add(ret);
        }

        EmbedBuilder builder = EmbedUtils.getDefaultEmbed()
                .setTitle(":trophy:  Leaderboard")
                .setDescription(joiner.toString());

        return builder.build();
    }

}
