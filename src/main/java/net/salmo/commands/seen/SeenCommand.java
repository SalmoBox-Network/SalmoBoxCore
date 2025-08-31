package net.salmo.commands.seen;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.salmo.SalmoBoxPlugin;
import net.salmo.database.PlayerData;
import net.salmo.database.repository.PlayerRepository;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SeenCommand extends Command {

    private final SalmoBoxPlugin plugin;
    private final PlayerRepository playerRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public SeenCommand() {
        super("seen", "Muestra información de un jugador", "/seen <jugador>", List.of("ver", "estado"));
        this.plugin = SalmoBoxPlugin.instance;
        this.playerRepository = plugin.getDatabaseManager().getPlayerRepository();
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

        PlayerData data = playerRepository.findByName(targetName).orElse(null);

        UUID uuid;
        boolean online;
        String actualPlayerName = targetName;

        if (data != null) {

            uuid = data.getUuid();
            actualPlayerName = data.getName();
            online = Bukkit.getPlayer(uuid) != null;
        } else {

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
            uuid = offlinePlayer.getUniqueId();
            online = offlinePlayer.isOnline();

            if (online) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    actualPlayerName = player.getName();
                }
            }
        }

        String lastSeenInfo;
        String connectionTime = "";

        if (online) {
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                if (data != null && data.getLastSeen() != null) {
                    Duration connectedDuration = Duration.between(data.getLastSeen(), Instant.now());
                    connectionTime = formatDuration(connectedDuration);
                    lastSeenInfo = "Conectado ahora (hace " + connectionTime + ")";
                } else {
                    lastSeenInfo = "Conectado ahora";
                }
            } else {
                lastSeenInfo = "Conectado ahora";
            }
        } else {
            if (data != null && data.getLastSeen() != null) {
                Duration offlineDuration = Duration.between(data.getLastSeen(), Instant.now());
                String timeAgo = formatDuration(offlineDuration);

                LocalDateTime lastSeenDate = LocalDateTime.ofInstant(data.getLastSeen(), ZoneId.systemDefault());
                String formattedDate = lastSeenDate.format(dateFormatter);

                lastSeenInfo = timeAgo + " desconectado (" + formattedDate + ")";
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                if (offlinePlayer.hasPlayedBefore()) {
                    lastSeenInfo = "Ha entrado antes pero sin registro de última conexión";
                } else {
                    lastSeenInfo = "Nunca ha entrado al servidor";
                }
            }
        }

        String ip;
        if (online) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                ip = Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress();
            } else if (data != null && data.getIp() != null) {
                ip = data.getIp();
            } else {
                ip = "Desconocida";
            }
        } else if (data != null && data.getIp() != null) {
            ip = data.getIp();
        } else {
            ip = "Desconocida";
        }

        String rawMessage = MessageManager.getMessageRaw("messages.seen.info");
        if (rawMessage == null) {
            rawMessage = "<green>Información de %player%</green>\n" +
                    "<gray>UUID: %uuid%</gray>\n" +
                    "<gray>Estado: %status%</gray>\n" +
                    "<gray>Última vez: %lastseen%</gray>\n" +
                    "<gray>IP: %ip%</gray>";
        }

        Component message = MiniMessage.miniMessage().deserialize(
                rawMessage
                        .replace("%player%", actualPlayerName)
                        .replace("%uuid%", uuid.toString())
                        .replace("%status%", online ? "Online" : "Offline")
                        .replace("%lastseen%", lastSeenInfo)
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