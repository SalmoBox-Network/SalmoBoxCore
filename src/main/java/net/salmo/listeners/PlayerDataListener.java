package net.salmo.listeners;

import net.salmo.SalmoBoxPlugin;
import net.salmo.database.PlayerData;
import net.salmo.database.repository.PlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Instant;
import java.util.Objects;

public class PlayerDataListener implements Listener {

    private final PlayerRepository playerRepository;

    public PlayerDataListener(SalmoBoxPlugin plugin) {
        this.playerRepository = plugin.getDatabaseManager().getPlayerRepository();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var uuid = player.getUniqueId();
        var name = player.getName();

        PlayerData data = playerRepository.findById(uuid)
                .orElse(new PlayerData(uuid, name));

        data.setName(name);
        data.setIp(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
        data.setLastSeen(Instant.now());

        playerRepository.saveAsync(data);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var uuid = player.getUniqueId();
        var name = player.getName();

        PlayerData data = playerRepository.findById(uuid)
                .orElse(new PlayerData(uuid, name));

        data.setName(name);
        data.setLastSeen(Instant.now());

        playerRepository.saveAsync(data);
    }
}