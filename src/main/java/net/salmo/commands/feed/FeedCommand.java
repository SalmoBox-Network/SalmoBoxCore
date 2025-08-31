package net.salmo.commands.feed;

import net.salmo.managers.CooldownManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.salmo.SalmoBoxPlugin;
import net.salmo.models.Permissions;
import net.salmo.managers.MessageManager;
import net.kyori.adventure.text.Component;

import java.util.List;

public class FeedCommand extends Command {

    private final SalmoBoxPlugin salmoPlugin;

    public FeedCommand() {
        super("feed", "Llena tu barra de comida", "/feed [jugador]", List.of("comida", "alimentar"));
        this.salmoPlugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(MessageManager.getMessage("messages.player-only"));
                return true;
            }

            if (!player.hasPermission(Permissions.COMMAND_FEED.getPermission())) {
                sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
                return true;
            }

            if (!player.hasPermission(Permissions.FEED_COOLDOWN_BYPASS.getPermission())) {
                int cooldown = salmoPlugin.getConfig().getInt("cooldowns.feed-cooldown");
                if (CooldownManager.isOnCooldown(player, "feed", cooldown)) {
                    int remaining = CooldownManager.getRemaining(player, "feed", cooldown);
                    Component msgComponent = MessageManager.getMessage("messages.cooldown-message")
                            .replaceText(builder -> builder.match("%time%").replacement(String.valueOf(remaining)));
                    sender.sendMessage(msgComponent);
                    return true;
                }
            }

            feedPlayer(player);

            if (!player.hasPermission(Permissions.FEED_COOLDOWN_BYPASS.getPermission())) {
                CooldownManager.startCooldown(player, "feed");
            }

            sender.sendMessage(MessageManager.getMessage("messages.feed.self"));
            return true;
        }


        if (!sender.hasPermission(Permissions.COMMAND_FEED_OTHERS.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        Player target = salmoPlugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageManager.getMessage("messages.player-not-found"));
            return true;
        }

        feedPlayer(target);

        Component message = MessageManager.getMessage("messages.feed.other")
                .replaceText(builder -> builder.match("%player%").replacement(target.getName()));
        sender.sendMessage(message);

        if (!sender.equals(target)) {
            target.sendMessage(MessageManager.getMessage("messages.feed.fed-by")
                    .replaceText(builder -> builder.match("%sender%").replacement(sender.getName())));
        }

        return true;
    }

    private void feedPlayer(Player player) {
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        player.setExhaustion(0.0f);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission(Permissions.COMMAND_FEED_OTHERS.getPermission())) {
            return salmoPlugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        return List.of();
    }
}