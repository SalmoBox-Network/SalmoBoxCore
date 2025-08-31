package net.salmo.commands.spawn;

import net.salmo.SalmoBoxPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class SpawnManager {

    private static final SalmoBoxPlugin plugin = SalmoBoxPlugin.instance;

    public static void setSpawn(Location location) {
        FileConfiguration config = plugin.getConfig();
        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());
        plugin.saveConfig();
    }

    public static Location getSpawn() {
        FileConfiguration config = plugin.getConfig();
        if (!config.contains("spawn.world")) return null;

        return new Location(
                Bukkit.getWorld(Objects.requireNonNull(config.getString("spawn.world"))),
                config.getDouble("spawn.x"),
                config.getDouble("spawn.y"),
                config.getDouble("spawn.z"),
                (float) config.getDouble("spawn.yaw"),
                (float) config.getDouble("spawn.pitch")
        );
    }

    public static void teleportToSpawn(Player player) {
        Location spawn = getSpawn();
        if (spawn != null) {
            player.teleport(spawn);
        }
    }

    public static @NotNull Location getSpawnLocation() {
        Location spawn = getSpawn();
        if (spawn != null) return spawn;
        return Bukkit.getWorlds().getFirst().getSpawnLocation();
    }
}
