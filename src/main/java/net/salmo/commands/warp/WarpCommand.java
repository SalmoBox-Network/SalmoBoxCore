package net.salmo.commands.warp;

import net.salmo.SalmoBoxPlugin;
import net.salmo.database.WarpData;
import net.salmo.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WarpCommand extends Command {

    private final SalmoBoxPlugin plugin;

    public WarpCommand() {
        super("warp", "Teletransporta a un warp", "/warp <nombre>", List.of("warps", "goto"));
        this.plugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageManager.getMessage("messages.only-players"));
            return true;
        }

        if (args.length < 1) {
            List<WarpData> warps = plugin.getDatabaseManager().getWarpRepository().findAll();
            List<String> availableWarps = warps.stream()
                    .filter(warp -> warp.getPermission() == null ||
                            player.hasPermission(warp.getPermission()))
                    .map(WarpData::getName)
                    .collect(Collectors.toList());

            if (availableWarps.isEmpty()) {
                player.sendMessage(MessageManager.getMessage("messages.warp.no-warps-available"));
            } else {
                player.sendMessage(MessageManager.getMessage("messages.warp.list-available",
                        java.util.Map.of("%warps%", String.join("§7, §e", availableWarps))));
            }
            return true;
        }

        String warpName = args[0].toLowerCase();

       Optional<WarpData> warpOpt = plugin.getDatabaseManager().getWarpRepository().findByName(warpName);

        if (warpOpt.isPresent()) {
            WarpData warp = warpOpt.get();

            if (warp.getPermission() != null && !warp.getPermission().isEmpty() &&
                    !player.hasPermission(warp.getPermission())) {
                player.sendMessage(MessageManager.getMessage("messages.no-permission"));
                return true;
            }

            player.teleport(warp.getLocation());
            player.sendMessage(MessageManager.getMessage("messages.warp.teleport-success",
                    java.util.Map.of("%warp%", warpName)));
        } else {
            player.sendMessage(MessageManager.getMessage("messages.warp.not-found",
                    java.util.Map.of("%warp%", warpName)));
        }

        return true;
    }
}