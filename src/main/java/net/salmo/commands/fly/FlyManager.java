package net.salmo.commands.fly;

import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import net.kyori.adventure.text.Component;
import net.salmo.managers.MessageManager;

public final class FlyManager {

    public static void toggleFly(Player player) {
        boolean newState = !player.getAllowFlight();
        player.setAllowFlight(newState);

        if (!newState) {
            player.setFlying(false);
        }
    }

    public static void setFlySpeed(Player player, float speed) {
        speed = Math.max(0.1f, Math.min(speed, 10.0f)) / 10.0f;
        player.setFlySpeed(speed);
    }

    public static Component getFlySpeedMessage(String speedValue) {
        return MessageManager.getMessage("messages.fly.speed-set")
                .replaceText(builder -> builder.match("%speed%").replacement(speedValue));
    }

    public static Component getFlyToggleMessage(CommandSender sender, Player target, boolean newState) {
        if (sender.equals(target)) {
            return newState ?
                    MessageManager.getMessage("messages.fly.enabled-self") :
                    MessageManager.getMessage("messages.fly.disabled-self");
        } else {
            Component message = newState ?
                    MessageManager.getMessage("messages.fly.enabled-other") :
                    MessageManager.getMessage("messages.fly.disabled-other");
            return message.replaceText(builder -> builder.match("%player%").replacement(target.getName()));
        }
    }

    public static Component getPlayerNotFoundMessage() {
        return MessageManager.getMessage("messages.player-not-found");
    }

    public static Component getInvalidNumberMessage() {
        return MessageManager.getMessage("messages.invalid-number");
    }

    public static Component getSpeedRangeMessage() {
        return MessageManager.getMessage("messages.fly.invalid-speed");
    }

    public static Component getSpeedUsageMessage() {
        return MessageManager.getMessage("messages.fly.speed-usage");
    }
}