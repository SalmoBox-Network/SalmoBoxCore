package net.salmo.commands.playtime.commands;

import net.salmo.models.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.salmo.SalmoBoxPlugin;
import net.salmo.managers.MessageManager;
import net.salmo.commands.playtime.PlaytimeManager;

import java.util.List;

public class PlaytimeCommand extends Command {

    private final SalmoBoxPlugin plugin;

    public PlaytimeCommand() {
        super("playtime", "Muestra el tiempo jugado", "/playtime [jugador]", List.of("tiempo"));
        this.plugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageManager.getMessage("messages.player-only"));
                return true;
            }

            if (!player.hasPermission(Permissions.COMMAND_PLAYTIME.getPermission())) {
                sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
                return true;
            }

            sender.sendMessage(PlaytimeManager.getSelfPlaytimeMessage(player));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(PlaytimeManager.getPlayerNotFoundMessage());
            return true;
        }

        sender.sendMessage(PlaytimeManager.getOtherPlaytimeMessage(sender, target));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        return List.of();
    }
}
