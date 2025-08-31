package net.salmo.database.repository;

import net.salmo.database.PlayerData;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PlayerRepository {
    CompletableFuture<Optional<PlayerData>> findByIdAsync(UUID uuid);
    CompletableFuture<Void> saveAsync(PlayerData playerData);
    Optional<PlayerData> findById(UUID uuid);
    Optional<PlayerData> findByName(String playerName);
    void save(PlayerData playerData);
    void delete(UUID uuid);
}