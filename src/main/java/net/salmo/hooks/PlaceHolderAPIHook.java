package net.salmo.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlaceHolderAPIHook extends PlaceholderExpansion {
    private final Map<String, Function<Player, String>> placeholderHandlers = new HashMap<>();

    public PlaceHolderAPIHook() {
    }

    public void addPlaceholder(String identifier, Function<Player, String> handler) {
        placeholderHandlers.put(identifier.toLowerCase(), handler);
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        Function<Player, String> handler = placeholderHandlers.get(identifier.toLowerCase());
        return handler != null ? handler.apply(player) : null;
    }


    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "salmoboxcore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SalmoBox";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
}