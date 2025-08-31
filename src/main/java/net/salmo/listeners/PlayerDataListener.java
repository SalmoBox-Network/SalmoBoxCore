package net.salmo.listeners;

import net.salmo.SalmoBoxPlugin;
import net.salmo.database.PlayerData;
import net.salmo.managers.DatabaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.Objects;

public class PlayerDataListener implements Listener {

    private final DatabaseManager db;

    public PlayerDataListener(SalmoBoxPlugin plugin) {
        this.db = plugin.getDatabaseManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var uuid = player.getUniqueId();

        PlayerData data = db.getPlayer(uuid).orElse(new PlayerData(uuid));

        data.setIp(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
        data.setLastSeen(Instant.now());

        db.savePlayer(data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var uuid = player.getUniqueId();

        PlayerData data = db.getPlayer(uuid).orElse(new PlayerData(uuid));

        data.setLastSeen(Instant.now());

        db.savePlayer(data);
    }
}