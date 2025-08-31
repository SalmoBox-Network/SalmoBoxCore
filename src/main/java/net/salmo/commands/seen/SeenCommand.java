package net.salmo.commands.seen;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.salmo.SalmoBoxPlugin;
import net.salmo.database.PlayerData;
import net.salmo.managers.DatabaseManager;
import net.salmo.managers.MessageManager;
import net.salmo.models.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SeenCommand extends Command {

    private final SalmoBoxPlugin plugin;

    public SeenCommand() {
        super("seen", "Muestra información de un jugador", "/seen <jugador>", List.of("ver", "estado"));
        this.plugin = SalmoBoxPlugin.instance;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, String @NotNull [] args) {
        if (args.length != 1) {
            sender.sendMessage(MessageManager.getMessage("messages.seen.usage"));
            return true;
        }

        if (!sender.hasPermission(Permissions.COMMAND_SEEN.getPermission())) {
            sender.sendMessage(MessageManager.getMessage("messages.no-permission"));
            return true;
        }

        String targetName = args[0];
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        DatabaseManager db = plugin.getDatabaseManager();
        UUID uuid = target.getUniqueId();

        PlayerData data = db.getPlayer(uuid).orElse(new PlayerData(uuid));

        boolean online = target.isOnline();
        String lastSeen;
        String connectionTime = "";

        if (online) {
            Player onlinePlayer = target.getPlayer();
            if (onlinePlayer != null) {
                Duration connectedDuration = Duration.between(data.getLastSeen(), Instant.now());
                connectionTime = formatDuration(connectedDuration);
                lastSeen = "Conectado ahora (hace " + connectionTime + ")";
            } else {
                lastSeen = "Conectado ahora";
            }
        } else {
            if (data.getLastSeen() != null) {
                Duration offlineDuration = Duration.between(data.getLastSeen(), Instant.now());
                lastSeen = formatDuration(offlineDuration) + " desconectado";
            } else {
                lastSeen = "Tiempo desconocido";
            }
        }

        String ip = online && target instanceof Player p ?
                Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress() :
                data.getIp();

        Component message = MiniMessage.miniMessage().deserialize(
                MessageManager.getMessageRaw("messages.seen.info")
                        .replace("%player%", targetName)
                        .replace("%uuid%", uuid.toString())
                        .replace("%status%", online ? "Online" : "Offline")
                        .replace("%lastseen%", lastSeen)
                        .replace("%ip%", ip)
        );

        sender.sendMessage(message);
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

    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        sb.append(seconds).append("s");
        return sb.toString();
    }
}