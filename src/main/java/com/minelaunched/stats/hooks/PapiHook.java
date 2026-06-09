package com.minelaunched.stats.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PapiHook {
    private static boolean initialized = false;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            initialized = true;
            Bukkit.getLogger().info("[MinelaunchedStats] Hooks: PlaceholderAPI successfully linked!");
        }
    }

    public static boolean isEnabled() {
        return initialized;
    }

    public static String setPlaceholders(OfflinePlayer player, String text) {
        if (!initialized) return text;
        try {
            return PlaceholderAPI.setPlaceholders(player, text);
        } catch (Exception e) {
            return text;
        }
    }
}
