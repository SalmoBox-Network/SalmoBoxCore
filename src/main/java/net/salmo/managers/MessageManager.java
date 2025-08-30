package net.salmo.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.salmo.SalmoBoxPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MessageManager {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static FileConfiguration messagesConfig;
    private static File messagesFile;

    public static void initialize(SalmoBoxPlugin plugin) {
        setupMessages(plugin);
    }

    private static void setupMessages(SalmoBoxPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            try {
                InputStream inputStream = plugin.getResource("messages.yml");
                if (inputStream != null) {
                    Files.copy(inputStream, messagesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getLogger().info("messages.yml copiado desde recursos");
                } else {
                    plugin.getLogger().warning("No se encontró messages.yml en los recursos del plugin");
                    messagesFile.createNewFile();
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Error creando messages.yml: " + e.getMessage());
            }
        }

        reloadMessages();
    }

    public static void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static Component getMessage(String path) {
        String message = messagesConfig.getString(path);
        if (message == null) {
            return miniMessage.deserialize("<red>Mensaje no encontrado: " + path + "</red>");
        }
        return miniMessage.deserialize(message);
    }
}