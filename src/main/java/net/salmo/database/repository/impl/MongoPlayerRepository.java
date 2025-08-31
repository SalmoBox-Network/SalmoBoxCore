package net.salmo.database.repository.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.salmo.database.PlayerData;
import net.salmo.database.repository.PlayerRepository;
import net.salmo.managers.InstantTypeAdapter;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonString;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MongoPlayerRepository implements PlayerRepository {

    private final ExecutorService executor;
    private final MongoClient mongoClient;
    private final MongoCollection<BsonDocument> collection;
    private final Gson gson;

    public MongoPlayerRepository(ExecutorService executor, MongoClient mongoClient,
                                 MongoCollection<BsonDocument> collection) {
        this.executor = executor;
        this.mongoClient = mongoClient;
        this.collection = collection;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
    }

    @Override
    public CompletableFuture<Optional<PlayerData>> findByIdAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> findById(uuid), executor);
    }

    @Override
    public CompletableFuture<Void> saveAsync(PlayerData playerData) {
        return CompletableFuture.runAsync(() -> save(playerData), executor);
    }

    @Override
    public Optional<PlayerData> findById(UUID uuid) {
        BsonDocument doc = collection.find(Filters.eq("_id", uuid)).first();
        if (doc != null) {
            if (doc.containsKey("_id") && doc.get("_id").isBinary()) {
                UUID docUuid = doc.getBinary("_id").asUuid();
                doc.put("uuid", new BsonString(docUuid.toString()));
            }
            PlayerData data = gson.fromJson(doc.toJson(), PlayerData.class);
            return Optional.of(data);
        }
        return Optional.empty();
    }

    @Override
    public Optional<PlayerData> findByName(String playerName) {
        BsonDocument doc = collection.find(Filters.eq("name", playerName)).first();
        if (doc != null) {
            PlayerData data = gson.fromJson(doc.toJson(), PlayerData.class);
            return Optional.of(data);
        }
        return Optional.empty();
    }

    @Override
    public void save(PlayerData playerData) {
        String json = gson.toJson(playerData);
        BsonDocument doc = BsonDocument.parse(json);

        doc.put("_id", new BsonBinary(playerData.getUuid()));

        doc.put("name", new BsonString(playerData.getName()));

        collection.replaceOne(
                Filters.eq("_id", playerData.getUuid()),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }

    @Override
    public void delete(UUID uuid) {
        collection.deleteOne(Filters.eq("_id", uuid));
    }

    public void close() {
        mongoClient.close();
        executor.shutdown();
    }
}