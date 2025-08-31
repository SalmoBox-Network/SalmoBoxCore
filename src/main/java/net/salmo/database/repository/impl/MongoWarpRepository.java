package net.salmo.database.repository.impl;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.salmo.database.WarpData;
import net.salmo.database.repository.WarpRepository;
import org.bson.BsonDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MongoWarpRepository implements WarpRepository {

    private final ExecutorService executor;
    private final MongoCollection<BsonDocument> collection;
    private final Gson gson;

    public MongoWarpRepository(ExecutorService executor, MongoCollection<BsonDocument> collection) {
        this.executor = executor;
        this.collection = collection;
        this.gson = new Gson();
    }

    @Override
    public CompletableFuture<Optional<WarpData>> findByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> findByName(name), executor);
    }

    @Override
    public CompletableFuture<Void> saveAsync(WarpData warpData) {
        return CompletableFuture.runAsync(() -> save(warpData), executor);
    }

    @Override
    public CompletableFuture<Void> deleteAsync(String name) {
        return CompletableFuture.runAsync(() -> delete(name), executor);
    }

    @Override
    public CompletableFuture<List<WarpData>> findAllAsync() {
        return CompletableFuture.supplyAsync(this::findAll, executor);
    }

    @Override
    public Optional<WarpData> findByName(String name) {
        BsonDocument doc = collection.find(Filters.eq("name", name.toLowerCase())).first();
        if (doc != null) {
            WarpData data = gson.fromJson(doc.toJson(), WarpData.class);
            return Optional.of(data);
        }
        return Optional.empty();
    }

    @Override
    public void save(WarpData warpData) {
        String json = gson.toJson(warpData);
        BsonDocument doc = BsonDocument.parse(json);

        collection.replaceOne(
                Filters.eq("name", warpData.getName().toLowerCase()),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }

    @Override
    public void delete(String name) {
        collection.deleteOne(Filters.eq("name", name.toLowerCase()));
    }

    @Override
    public List<WarpData> findAll() {
        List<WarpData> warps = new ArrayList<>();
        collection.find().forEach(doc -> {
            WarpData data = gson.fromJson(doc.toJson(), WarpData.class);
            warps.add(data);
        });
        return warps;
    }
}