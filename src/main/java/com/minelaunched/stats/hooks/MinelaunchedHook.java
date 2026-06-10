package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class MinelaunchedHook {

    /**
     * Returns the exact name of the plugin this hook is for (must match config.yml key)
     */
    public abstract String getPluginName();

    /**
     * The list of export properties for the config (e.g., "player_balance" -> hooks.vault.export_player_balance)
     */
    public abstract List<String> getExportKeys();

    /**
     * Initialize the hook via reflection or direct API calls.
     */
    public abstract void init();

    /**
     * Return true if the hook successfully found the plugin and is ready to be used.
     */
    public abstract boolean isEnabled();

    /**
     * Automatically registers the default configuration paths for this hook.
     */
    public void registerDefaults(FileConfiguration config) {
        String base = "hooks." + getPluginName().toLowerCase();
        config.addDefault(base + ".enabled", true);
        for (String key : getExportKeys()) {
            config.addDefault(base + ".export_" + key, true);
        }
    }

    /**
     * Append player-specific statistics to the JSON object.
     */
    public void appendPlayerStats(JsonObject playerData, Player p, FileConfiguration config) {
        // Default empty implementation
    }

    /**
     * Append server-wide statistics to the JSON object.
     */
    public void appendServerStats(JsonObject serverData, FileConfiguration config) {
        // Default empty implementation
    }
}
