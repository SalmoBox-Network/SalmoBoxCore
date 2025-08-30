package net.salmo;

import lombok.Getter;
import net.salmo.commands.DisposalCommand;
import net.salmo.hooks.PlaceHolderAPIHook;
import net.salmo.managers.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

@Getter
public class SalmoBoxPlugin extends JavaPlugin {
    @Getter
    public static SalmoBoxPlugin instance;
    public MessageManager getMessageManager;
    @Getter
    private PlaceHolderAPIHook placeholderHook;
    @Getter
    private MessageManager messageManager;

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();
        printStartupHeader();

        setupPlaceholderAPI();
        registerCommands();

        long loadTime = System.currentTimeMillis() - startTime;
        printLoadedMessage(loadTime);
    }

    @Override
    public void onDisable() {
        printDisableMessage();
    }

    private void setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                if (this.placeholderHook != null) {
                    this.placeholderHook.unregister();
                }
                this.placeholderHook = new PlaceHolderAPIHook();

                placeholderHook.register();
                getLogger().info("PlaceholderAPI integrado correctamente.");
            } catch (Exception e) {
                getLogger().warning("Error al configurar PlaceholderAPI: " + e.getMessage());
            }
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
            Bukkit.getCommandMap().register("salmobox", new DisposalCommand());
        } catch (Exception e) {
            getLogger().warning("Error al registrar comandos: " + e.getMessage());
        }
    }
}