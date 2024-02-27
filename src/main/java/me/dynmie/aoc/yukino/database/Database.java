package me.dynmie.aoc.yukino.database;

import me.dynmie.aoc.yukino.aoc.AOCMember;
import me.dynmie.aoc.yukino.database.impl.YMongoDatabase;
import me.dynmie.aoc.yukino.utils.BotConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Database {

    /**
     * Initialize and connect the database.
     */
    void connect();

    /**
     * Checks if the database connection is closed.
     * @return True if closed, false if open.
     */
    boolean isClosed();

    @NotNull
    CompletableFuture<Optional<AOCMember>> getAOCMemberByDiscordId(@NotNull String discordId);

    @NotNull
    CompletableFuture<Optional<AOCMember>> getAOCMemberByUniqueId(@NotNull UUID uniqueId);

    @NotNull
    CompletableFuture<Void> saveAOCMember(@NotNull AOCMember member);

    @NotNull
    CompletableFuture<Void> deleteAOCMember(@NotNull AOCMember member);

    @NotNull
    CompletableFuture<Void> resetStrikes();

    @NotNull
    CompletableFuture<List<AOCMember>> getTopAOCMembersByHours(int page, int limit);

    @NotNull
    CompletableFuture<Long> getAOCMemberCount();

    static Database of(String type, BotConfig config) {
        if (type == null) return null;

        if ("mongo".equalsIgnoreCase(type)) {
            return new YMongoDatabase(config);
        }

        return null;
    }

}
