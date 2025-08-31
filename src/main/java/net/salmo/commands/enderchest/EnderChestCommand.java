package net.salmo.commands.enderchest;

import net.salmo.SalmoBoxPlugin;
import net.salmo.managers.MessageManager;
import net.salmo.models.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderChestCommand extends Command {

    private final SalmoBoxPlugin plugin;

    public EnderChestCommand() {
        super("enderchest", "Abre tu EnderChest virtual", "/enderchest", List.of("ec"));
        this.plugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageManager.getMessage("messages.player-only"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_ENDERCHEST.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        player.openInventory(player.getEnderChest());
        player.sendMessage(MessageManager.getMessage("messages.enderchest.opened"));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        return List.of();
    }
}
