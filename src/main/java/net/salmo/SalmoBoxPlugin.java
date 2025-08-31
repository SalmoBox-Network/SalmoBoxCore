package net.salmo;

import lombok.Getter;
import net.salmo.commands.disposal.DisposalCommand;
import net.salmo.commands.SalmoBoxCommand;
import net.salmo.commands.enderchest.EnderChestCommand;
import net.salmo.commands.feed.FeedCommand;
import net.salmo.commands.fly.commands.FlyCommand;
import net.salmo.commands.playtime.commands.PlaytimeCommand;
import net.salmo.commands.seen.SeenCommand;
import net.salmo.commands.spawn.commands.SetSpawnCommand;
import net.salmo.commands.spawn.commands.SpawnCommand;
import net.salmo.commands.spawn.listeners.SpawnListener;
import net.salmo.hooks.PlaceHolderAPIHook;
import net.salmo.listeners.PlayerDataListener;
import net.salmo.managers.DatabaseManager;
import net.salmo.database.MongoDBInitializer;
import net.salmo.managers.MessageManager;
import net.salmo.near.NearCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

@Getter
public class SalmoBoxPlugin extends JavaPlugin {
    @Getter
    public static SalmoBoxPlugin instance;
    public MessageManager getMessageManager;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private PlaceHolderAPIHook placeholderHook;

    @Override
    public void onEnable() {
        instance = this;
        MessageManager.initialize(this);

        long startTime = System.currentTimeMillis();
        printStartupHeader();

        saveDefaultConfig();

        try {
            this.databaseManager = MongoDBInitializer.initialize(this);
            getServer().getPluginManager().registerEvents(new PlayerDataListener(this), this);
            getLogger().info("Database connection established successfully");
        } catch (Exception e) {
            getLogger().severe("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }

        setupPlaceholderAPI();
        registerListeners();
        registerCommands();

        long loadTime = System.currentTimeMillis() - startTime;
        printLoadedMessage(loadTime);
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
        MessageManager.saveMessages();
        printDisableMessage();
    }

    private void setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                if (this.placeholderHook != null) {
                    this.placeholderHook.unregister();
                }
                this.placeholderHook = new PlaceHolderAPIHook();

                placeholderHook.register();
                Logger.info("PlaceholderAPI detectado e integrado correctamente.");
            } catch (Exception e) {
                Logger.warning("Error al configurar PlaceholderAPI: " + e.getMessage());
            }
        } else {
            Logger.info("PlaceholderAPI no detectado. El plugin funcionará sin placeholders.");
        }
    }

    private void printStartupHeader() {
        Bukkit.getConsoleSender().sendMessage("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("  ██████╗ █████╗ ██╗     ███╗   ███╗ █████╗   ");
        Bukkit.getConsoleSender().sendMessage(" ██╔════╝██╔══██╗██║     ████╗ ████║██╔══██╗  ");
        Bukkit.getConsoleSender().sendMessage(" ╚█████╗ ███████║██║     ██╔████╔██║██║  ██║  ");
        Bukkit.getConsoleSender().sendMessage("  ╚═══██╗██╔══██║██║     ██║╚██╔╝██║██║  ██║  ");
        Bukkit.getConsoleSender().sendMessage(" ██████╔╝██║  ██║███████╗██║ ╚═╝ ██║╚█████╔╝  ");
        Bukkit.getConsoleSender().sendMessage(" ╚═════╝ ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝ ╚════╝   ");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(" SalmoBoxCore v" + this.getPluginMeta().getVersion());
        Bukkit.getConsoleSender().sendMessage(" Desarrollado por Rud ♥️");
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
    }

    private void printLoadedMessage(long loadTime) {
        Bukkit.getConsoleSender().sendMessage("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
        Bukkit.getConsoleSender().sendMessage(" Plugin activado en " + loadTime + "ms");
        Bukkit.getConsoleSender().sendMessage("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
    }

    private void printDisableMessage() {
        Bukkit.getConsoleSender().sendMessage("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
        Bukkit.getConsoleSender().sendMessage(" SalmoBoxCore ha sido desactivado");
        Bukkit.getConsoleSender().sendMessage("▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰");
    }

    private void registerCommands() {
        try {
            Bukkit.getCommandMap().register("salmoboxcore", new SalmoBoxCommand());
            Bukkit.getCommandMap().register("fly", new FlyCommand());
            Bukkit.getCommandMap().register("disposal", new DisposalCommand());
            Bukkit.getCommandMap().register("playtime", new PlaytimeCommand());
            Bukkit.getCommandMap().register("spawn", new SpawnCommand());
            Bukkit.getCommandMap().register("setspawn", new SetSpawnCommand());
            Bukkit.getCommandMap().register("enderchest", new EnderChestCommand());
            Bukkit.getCommandMap().register("feed", new FeedCommand());
            Bukkit.getCommandMap().register("near", new NearCommand());
            Bukkit.getCommandMap().register("seen", new SeenCommand());
        } catch (Exception e) {
            Logger.warning("Error al registrar comandos: " + e.getMessage());
        }
    }

    private void registerListeners() {
        try {
            getServer().getPluginManager().registerEvents(new SpawnListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerDataListener(this), this);
        } catch (Exception e) {
            Logger.warning("Error al registrar eventos: " + e.getMessage());
        }
    }
}