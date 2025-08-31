package net.salmo.database.repository;

import net.salmo.database.WarpData;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface WarpRepository {
    CompletableFuture<Optional<WarpData>> findByNameAsync(String name);
    CompletableFuture<Void> saveAsync(WarpData warpData);
    CompletableFuture<Void> deleteAsync(String name);
    CompletableFuture<List<WarpData>> findAllAsync();

    Optional<WarpData> findByName(String name);
    void save(WarpData warpData);
    void delete(String name);
    List<WarpData> findAll();
}