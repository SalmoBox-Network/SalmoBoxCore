package net.salmo.commands;

import net.salmo.SalmoBoxPlugin;
import net.salmo.models.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import net.salmo.managers.MessageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class SalmoBoxCommand extends Command {

    public SalmoBoxCommand() {
        super("salmoboxcore", "Recarga la configuración del plugin", "/salmoboxcore reload", List.of("sbc"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(Permissions.COMMAND_RELOAD.getPermission())) {
                sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
                return true;
            }

            MessageManager.reloadMessages();
            SalmoBoxPlugin.instance.reloadConfig();
            sender.sendMessage(Component.text("Configuración recargada correctamente.", NamedTextColor.GREEN));
            return true;
        }

        sender.sendMessage(Component.text("Uso: /salmoboxcore reload", NamedTextColor.YELLOW));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return List.of();
    }
}