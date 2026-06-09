package com.minelaunched.stats.hooks;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

public class FloodgateHook {
    private static boolean initialized = false;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            try {
                Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: Floodgate (Geyser) successfully linked!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link Floodgate.");
            }
        }
    }

    public static boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized) return null;
        try {
            FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
            JsonObject json = new JsonObject();
            if (fPlayer != null) {
                json.addProperty("is_bedrock", true);
                json.addProperty("device_os", fPlayer.getDeviceOs().name());
                json.addProperty("language_code", fPlayer.getLanguageCode());
            } else {
                json.addProperty("is_bedrock", false);
            }
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
