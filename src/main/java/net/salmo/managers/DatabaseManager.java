package net.salmo.managers;

import lombok.Getter;
import net.salmo.database.repository.PlayerRepository;
import net.salmo.database.repository.impl.MongoPlayerRepository;

import java.util.concurrent.ExecutorService;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.BsonDocument;

public class DatabaseManager {

    @Getter
    private final PlayerRepository playerRepository;
    private final ExecutorService executor;
    private final MongoClient mongoClient;

    public DatabaseManager(ExecutorService executor, MongoClient mongoClient,
                           MongoCollection<BsonDocument> collection) {
        this.executor = executor;
        this.mongoClient = mongoClient;
        this.playerRepository = new MongoPlayerRepository(executor, mongoClient, collection);
    }

    public void shutdown() {
        mongoClient.close();
        executor.shutdown();
    }
}