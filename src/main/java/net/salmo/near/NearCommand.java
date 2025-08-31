package net.salmo.near;

import net.salmo.SalmoBoxPlugin;
import net.salmo.managers.CooldownManager;
import net.salmo.managers.MessageManager;
import net.salmo.models.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NearCommand extends Command {

    private final SalmoBoxPlugin salmoPlugin;

    public NearCommand() {
        super("near", "Muestra jugadores cercanos", "/near", List.of("cerca", "playersnear"));
        this.salmoPlugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageManager.getMessage("messages.player-only"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_NEAR.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        if (!player.hasPermission(Permissions.NEAR_COOLDOWN_BYPASS.getPermission())) {
            int cooldown = salmoPlugin.getConfig().getInt("cooldowns.near-cooldown");
            if (CooldownManager.isOnCooldown(player, "near", cooldown)) {
                int remaining = CooldownManager.getRemaining(player, "near", cooldown);
                Component msgComponent = MessageManager.getMessage("messages.cooldown-message")
                        .replaceText(builder -> builder.match("%time%").replacement(String.valueOf(remaining)));
                player.sendMessage(msgComponent);
                return true;
            }
        }

        double maxDistance = salmoPlugin.getConfig().getDouble("near.distance");

        boolean found = false;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.equals(player)) continue;

            double distance = player.getLocation().distance(target.getLocation());
            if (distance <= maxDistance) {
                Component message = MessageManager.getMessage("messages.near.player")
                        .replaceText(builder -> builder.match("%player%").replacement(target.getName()))
                        .replaceText(builder -> builder.match("%distance%").replacement(String.format("%.1f", distance)));
                player.sendMessage(message);
                found = true;
            }
        }

        if (!found) {
            player.sendMessage(MessageManager.getMessage("messages.near.none"));
        }

        if (!player.hasPermission(Permissions.NEAR_COOLDOWN_BYPASS.getPermission())) {
            CooldownManager.startCooldown(player, "near");
        }

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return salmoPlugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        return List.of();
    }
}
