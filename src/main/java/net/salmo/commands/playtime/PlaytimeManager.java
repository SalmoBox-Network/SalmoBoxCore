package net.salmo.commands.playtime;

import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.salmo.managers.MessageManager;

public final class PlaytimeManager {

    private static String formatPlaytime(Player player) {
        int ticks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        minutes %= 60;
        seconds %= 60;

        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }

    public static Component getSelfPlaytimeMessage(Player player) {
        return MessageManager.getMessage("messages.playtime.self")
                .replaceText(builder -> builder.match("%time%").replacement(formatPlaytime(player)));
    }

    public static Component getOtherPlaytimeMessage(CommandSender sender, Player target) {
        return MessageManager.getMessage("messages.playtime.other")
                .replaceText(builder -> builder.match("%player%").replacement(target.getName()))
                .replaceText(builder -> builder.match("%time%").replacement(formatPlaytime(target)));
    }

    public static Component getPlayerNotFoundMessage() {
        return MessageManager.getMessage("messages.player-not-found");
    }
}
