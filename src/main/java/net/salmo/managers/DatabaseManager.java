package net.salmo.managers;

import lombok.Getter;
import net.salmo.database.repository.PlayerRepository;
import net.salmo.database.repository.WarpRepository;
import net.salmo.database.repository.impl.MongoPlayerRepository;

import java.util.concurrent.ExecutorService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import net.salmo.database.repository.impl.MongoWarpRepository;
import org.bson.BsonDocument;

public class DatabaseManager {

    @Getter
    private final PlayerRepository playerRepository;
    @Getter
    private final WarpRepository warpRepository;
    private final ExecutorService executor;
    private final MongoClient mongoClient;

    public DatabaseManager(ExecutorService executor, MongoClient mongoClient,
                           MongoCollection<BsonDocument> collection) {
        this.executor = executor;
        this.mongoClient = mongoClient;
        this.playerRepository = new MongoPlayerRepository(executor, mongoClient, collection);

        MongoCollection<BsonDocument> warpCollection = mongoClient
                .getDatabase("salmobox")
                .getCollection("warps", BsonDocument.class);
        this.warpRepository = new MongoWarpRepository(executor, warpCollection);
    }

    public void shutdown() {
        mongoClient.close();
        executor.shutdown();
    }
}