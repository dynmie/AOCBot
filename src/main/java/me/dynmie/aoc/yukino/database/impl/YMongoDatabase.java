package me.dynmie.aoc.yukino.database.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.client.model.ReplaceOptions;
import me.dynmie.aoc.yukino.aoc.AOCMember;
import me.dynmie.aoc.yukino.database.DBSerializable;
import me.dynmie.aoc.yukino.database.Database;
import me.dynmie.aoc.yukino.utils.BotConfig;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dynmie
 */
public class YMongoDatabase implements Database {

    private final BotConfig config;

    public YMongoDatabase(BotConfig config) {
        this.config = config;
    }

    private MongoClient client;

    private MongoCollection<Document> aocMembersCollection;

    @Override
    public void connect() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.cluster").setLevel(Level.WARNING);
        Logger.getLogger("org.mongodb.driver.connection").setLevel(Level.WARNING);

        String mongoUri = config.getMongoURI();

        MongoClientURI mongoClientURI = new MongoClientURI(mongoUri);

        client = new MongoClient(mongoClientURI);

        MongoDatabase database = client.getDatabase(config.getMongoDatabase());

        aocMembersCollection = database.getCollection(config.getDatabaseNameMembers());
    }

    @Override
    public boolean isClosed() {
        return client == null;
    }

    @Override
    public @NotNull CompletableFuture<Optional<AOCMember>> getAOCMemberByDiscordId(@NotNull String discordId) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = aocMembersCollection.find(Filters.eq("discordId", discordId)).first();
            if (document == null) return Optional.empty();

            return Optional.of(DBSerializable.deserialize(document, AOCMember.class));
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<AOCMember>> getAOCMemberByUniqueId(@NotNull UUID uniqueId) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = aocMembersCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first();
            if (document == null) return Optional.empty();

            return Optional.of(DBSerializable.deserialize(document, AOCMember.class));
        });
    }


    @Override
    public @NotNull CompletableFuture<Void> saveAOCMember(@NotNull AOCMember member) {
        return CompletableFuture.runAsync(() -> aocMembersCollection.replaceOne(
                Filters.eq("uniqueId", member.getUniqueId().toString()),
                new Document(member.serialize()),
                new ReplaceOptions().upsert(true)
        ));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteAOCMember(@NotNull AOCMember member) {
        return CompletableFuture.runAsync(() -> aocMembersCollection.deleteOne(
                Filters.eq("uniqueId", member.getUniqueId().toString())
        ));
    }

    @Override
    public @NotNull CompletableFuture<Void> resetStrikes() {
        return CompletableFuture.runAsync(() -> {
            Set<AOCMember> members = new HashSet<>();
            for (Document document : aocMembersCollection.find()) {
                members.add(DBSerializable.deserialize(document, AOCMember.class));
            }
            List<ReplaceOneModel<Document>> documents = new ArrayList<>();
            for (AOCMember member : members) {
                member.getStrikes().clear();
                Document document = new Document(member.serialize());
                documents.add(new ReplaceOneModel<>(
                        Filters.eq("uniqueId", member.getUniqueId().toString()),
                        document,
                        new ReplaceOptions().upsert(true)
                ));
            }
            aocMembersCollection.bulkWrite(documents);
        });
    }

    @Override
    public @NotNull CompletableFuture<List<AOCMember>> getTopAOCMembersByHours(int page, int limit) {
        if (page < 1) {
            throw new IllegalArgumentException("page cannot be lower than 1");
        }

        return CompletableFuture.supplyAsync(() -> {
            Document filter = new Document();
            filter.put("hours", -1);
            filter.put("firstName", 1);
            filter.put("lastName", 1);

            int skip = (page - 1) * limit;
            if (skip != 0) skip++;

            FindIterable<Document> iterable = aocMembersCollection.find()
                    .sort(filter)
                    .skip(skip)
                    .limit(limit);

            List<AOCMember> members = new ArrayList<>();
            for (Document document : iterable) {
                members.add(DBSerializable.deserialize(document, AOCMember.class));
            }

            return members;
        });
    }

    @Override
    public @NotNull CompletableFuture<Long> getAOCMemberCount() {
        return CompletableFuture.supplyAsync(() -> aocMembersCollection.countDocuments());
    }

}
