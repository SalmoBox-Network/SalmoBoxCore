package net.salmo.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.salmo.database.PlayerData;
import org.bson.Document;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private final MongoCollection<Document> collection;
    private final Gson gson;

    public DatabaseManager(JavaPlugin plugin, String connectionString, String databaseName) {
        this.plugin = plugin;

        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection("player_data");

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .create();
    }

    public CompletableFuture<Optional<PlayerData>> getPlayerAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Document doc = collection.find(Filters.eq("uuid", uuid.toString())).first();
            if (doc != null) {
                PlayerData data = gson.fromJson(doc.toJson(), PlayerData.class);
                return Optional.of(data);
            }
            return Optional.empty();
        });
    }

    public CompletableFuture<Void> savePlayerAsync(PlayerData data) {
        return CompletableFuture.runAsync(() -> {
            String json = gson.toJson(data);
            Document doc = Document.parse(json);
            collection.replaceOne(
                    Filters.eq("uuid", data.getUuid().toString()),
                    doc,
                    new ReplaceOptions().upsert(true)
            );
        });
    }

    public Optional<PlayerData> getPlayer(UUID uuid) {
        Document doc = collection.find(Filters.eq("uuid", uuid.toString())).first();
        if (doc != null) {
            PlayerData data = gson.fromJson(doc.toJson(), PlayerData.class);
            return Optional.of(data);
        }
        return Optional.empty();
    }

    public void savePlayer(PlayerData data) {
        String json = gson.toJson(data);
        Document doc = Document.parse(json);
        collection.replaceOne(
                Filters.eq("uuid", data.getUuid().toString()),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }
}