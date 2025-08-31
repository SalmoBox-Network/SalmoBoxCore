package net.salmo.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {

    private static final Map<String, Map<Player, Long>> cooldowns = new HashMap<>();

    public static boolean isOnCooldown(Player player, String command, int cooldown) {
        cooldowns.putIfAbsent(command, new HashMap<>());
        Map<Player, Long> commandCooldowns = cooldowns.get(command);

        if (!commandCooldowns.containsKey(player)) return false;

        long lastUsed = commandCooldowns.get(player);
        long timePassed = (System.currentTimeMillis() - lastUsed) / 1000;

        return timePassed < cooldown;
    }

    public static int getRemaining(Player player, String command, int cooldown) {
        long lastUsed = cooldowns.getOrDefault(command, new HashMap<>()).getOrDefault(player, 0L);
        long timePassed = (System.currentTimeMillis() - lastUsed) / 1000;
        return (int) Math.max(0, cooldown - timePassed);
    }

    public static void startCooldown(Player player, String command) {
        cooldowns.putIfAbsent(command, new HashMap<>());
        cooldowns.get(command).put(player, System.currentTimeMillis());
    }
}
