package net.salmo.commands.spawn.commands;

import net.salmo.SalmoBoxPlugin;
import net.salmo.managers.MessageManager;
import net.salmo.commands.spawn.SpawnManager;
import net.salmo.models.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpawnCommand extends Command {

    private final SalmoBoxPlugin plugin;

    public SpawnCommand() {
        super("spawn", "Te teletransporta al spawn", "/spawn", List.of());
        this.plugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageManager.getMessage("messages.player-only"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_SPAWN.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        if (SpawnManager.getSpawn() == null) {
            sender.sendMessage(MessageManager.getMessage("messages.spawn.not-set"));
            return true;
        }

        SpawnManager.teleportToSpawn(player);
        sender.sendMessage(MessageManager.getMessage("messages.spawn.teleported"));
        return true;
    }
}
