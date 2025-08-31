package net.salmo.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.salmo.Logger;
import net.salmo.SalmoBoxPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageManager {

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static FileConfiguration messagesConfig;
    private static File messagesFile;
    private static SalmoBoxPlugin plugin;

    public static void initialize(SalmoBoxPlugin pluginInstance) {
        plugin = pluginInstance;
        setupMessages();
    }

    private static void setupMessages() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        // Crear archivo si no existe
        if (!messagesFile.exists()) {
            try (InputStream inputStream = plugin.getResource("messages.yml")) {
                if (inputStream != null) {
                    java.nio.file.Files.copy(inputStream, messagesFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    Logger.info("messages.yml creado desde recursos");
                } else {
                    messagesFile.createNewFile();
                    Logger.warning("No se encontró messages.yml en recursos, archivo vacío creado");
                }
            } catch (IOException e) {
                Logger.severe("Error creando messages.yml: " + e.getMessage());
                return;
            }
        }

        reloadMessages();
    }

    public static void reloadMessages() {
        try {
            messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

            // Cargar configuración por defecto desde el JAR
            try (InputStream defaultStream = plugin.getResource("messages.yml")) {
                if (defaultStream != null) {
                    YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
                    messagesConfig.setDefaults(defaultConfig);
                    messagesConfig.options().copyDefaults(true);

                    // Guardar para incluir cualquier nueva clave por defecto
                    saveMessages();
                }
            }

            Logger.info("Messages recargados correctamente");
        } catch (Exception e) {
            Logger.severe("Error recargando mensajes: " + e.getMessage());
        }
    }

    public static void saveMessages() {
        if (messagesConfig != null && messagesFile != null) {
            try {
                messagesConfig.save(messagesFile);
            } catch (IOException e) {
                Logger.severe("Error guardando messages.yml: " + e.getMessage());
            }
        }
    }

    public static Component getMessage(String path) {
        if (messagesConfig == null) {
            return miniMessage.deserialize("<red>MessageManager no inicializado</red>");
        }

        String message = messagesConfig.getString(path);
        if (message == null) {
            return miniMessage.deserialize("<red>Mensaje no encontrado: " + path + "</red>");
        }

        return miniMessage.deserialize(message);
    }

    public static Component getMessage(String path, java.util.Map<String, String> placeholders) {
        Component message = getMessage(path);
        String text = miniMessage.serialize(message);

        for (java.util.Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }

        return miniMessage.deserialize(text);
    }

    public static String getMessageRaw(String path) {
        if (messagesConfig == null) return "Mensaje no encontrado";
        return messagesConfig.getString(path, "Mensaje no encontrado");
    }
}