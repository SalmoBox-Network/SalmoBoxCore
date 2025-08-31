package net.salmo.commands.fly.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.salmo.SalmoBoxPlugin;
import net.salmo.models.Permissions;
import net.salmo.managers.MessageManager;
import net.salmo.commands.fly.FlyManager;

import java.util.List;

public class FlyCommand extends Command {

    private final SalmoBoxPlugin salmoPlugin;

    public FlyCommand() {
        super("fly", "Activa/desactiva el modo vuelo", "/fly [jugador|velocidad] [valor]", List.of("velocidad"));
        this.salmoPlugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("speed")) {
            return handleSpeed(sender, args);
        }
        return handleFly(sender, args);
    }

    private boolean handleSpeed(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageManager.getMessage("messages.player-only"));
            return true;
        }

        if (!player.hasPermission(Permissions.COMMAND_FLY_SPEED.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(FlyManager.getSpeedUsageMessage());
            return true;
        }

        try {
            float speed = Float.parseFloat(args[1]);
            if (speed < 0.1f || speed > 10.0f) {
                sender.sendMessage(FlyManager.getSpeedRangeMessage());
                return true;
            }

            FlyManager.setFlySpeed(player, speed);
            sender.sendMessage(FlyManager.getFlySpeedMessage(args[1]));

        } catch (NumberFormatException e) {
            sender.sendMessage(FlyManager.getInvalidNumberMessage());
        }
        return true;
    }

    private boolean handleFly(CommandSender sender, String[] args) {
        Player targetPlayer;

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageManager.getMessage("messages.player-only"));
                return true;
            }

            if (!player.hasPermission(Permissions.COMMAND_FLY.getPermission())) {
                sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
                return true;
            }

            boolean newState = !player.getAllowFlight();
            FlyManager.toggleFly(player);
            sender.sendMessage(FlyManager.getFlyToggleMessage(sender, player, newState));
            return true;
        }

        if (!sender.hasPermission(Permissions.COMMAND_FLY_OTHERS.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        targetPlayer = salmoPlugin.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(FlyManager.getPlayerNotFoundMessage());
            return true;
        }

        boolean newState = !targetPlayer.getAllowFlight();
        FlyManager.toggleFly(targetPlayer);
        sender.sendMessage(FlyManager.getFlyToggleMessage(sender, targetPlayer, newState));

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return List.of("speed");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("speed")) {
            return List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        }

        return List.of();
    }
}