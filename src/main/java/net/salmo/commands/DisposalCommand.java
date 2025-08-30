package net.salmo.commands;

import net.salmo.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import net.salmo.SalmoBoxPlugin;
import net.salmo.models.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public final class DisposalCommand extends Command {

    private final SalmoBoxPlugin salmoPlugin;

    public DisposalCommand() {
        super("disposal", "Tira tu basura.", "/disposal", List.of("trash", "desechar"));
        this.salmoPlugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageManager.getMessage("messages.player-only"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_DISPOSAL.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        Inventory disposalInventory = salmoPlugin.getServer().createInventory(
                player,
                36,
                Component.text("Tira tu Basura").color(NamedTextColor.DARK_GRAY)
        );

        player.openInventory(disposalInventory);
        sender.sendMessage(MessageManager.getMessage("messages.disposal.opened"));

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String @NotNull [] args) {
        return List.of();
    }
}