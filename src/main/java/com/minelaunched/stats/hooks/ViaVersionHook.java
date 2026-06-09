package com.minelaunched.stats.hooks;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

public class ViaVersionHook {
    private static boolean initialized = false;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            try {
                Class.forName("com.viaversion.viaversion.api.Via");
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: ViaVersion successfully linked!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link ViaVersion.");
            }
        }
    }

    public static boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized) return null;
        try {
            int version = Via.getAPI().getPlayerVersion(player.getUniqueId());
            JsonObject json = new JsonObject();
            json.addProperty("protocol_version", version);
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
