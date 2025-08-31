package net.salmo.commands.warp;

import net.salmo.SalmoBoxPlugin;
import net.salmo.database.WarpData;
import net.salmo.managers.MessageManager;
import net.salmo.models.Permissions;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetWarpCommand extends Command {

    private final SalmoBoxPlugin plugin;

    public SetWarpCommand() {
        super("setwarp", "Establece un warp", "/setwarp <nombre> [permiso]", List.of());
        this.plugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageManager.getMessage("messages.warp.setwarp-usage"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_SETWARP.getPermission())) {
            player.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        String warpName = args[0].toLowerCase();
        String permission = args.length > 1 ? args[1] : null;

        Location location = player.getLocation();
        WarpData warpData = new WarpData(warpName, location, permission);

        plugin.getDatabaseManager().getWarpRepository().saveAsync(warpData).thenAccept(v -> {
            if (permission != null && !permission.isEmpty()) {
                player.sendMessage(MessageManager.getMessage("messages.warp.setwarp-success-permission",
                        java.util.Map.of("%warp%", warpName, "%permission%", permission)));
            } else {
                player.sendMessage(MessageManager.getMessage("messages.warp.setwarp-success",
                        java.util.Map.of("%warp%", warpName)));
            }
        });
        return true;
    }
}