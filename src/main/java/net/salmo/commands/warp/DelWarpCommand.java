package net.salmo.commands.warp;

import net.salmo.SalmoBoxPlugin;
import net.salmo.managers.MessageManager;
import net.salmo.models.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DelWarpCommand extends Command {

    private final SalmoBoxPlugin plugin;

    public DelWarpCommand() {
        super("delwarp", "Elimina un warp", "/delwarp <nombre>", List.of("deletewarp", "removewarp"));
        this.plugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cEste comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(MessageManager.getMessage("messages.warp.delwarp-usage"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_DELWARP.getPermission())) {
            player.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        String warpName = args[0].toLowerCase();

        plugin.getDatabaseManager().getWarpRepository().findByNameAsync(warpName).thenAccept(warpOpt -> {
            if (warpOpt.isPresent()) {
                plugin.getDatabaseManager().getWarpRepository().deleteAsync(warpName).thenAccept(v -> {
                    player.sendMessage(MessageManager.getMessage("messages.warp.delwarp-success",
                            java.util.Map.of("%warp%", warpName)));
                });
            } else {
                player.sendMessage(MessageManager.getMessage("messages.warp.not-found",
                        java.util.Map.of("%warp%", warpName)));
            }
        });

        return true;
    }
}