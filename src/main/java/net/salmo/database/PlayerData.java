package net.salmo.database;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class PlayerData {

    private final UUID uuid;
    @Setter
    private Instant lastSeen;
    @Setter
    private String ip;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.lastSeen = Instant.now();
        this.ip = "Desconocida";
    }
}
