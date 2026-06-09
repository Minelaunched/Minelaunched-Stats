package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;
import java.util.UUID;

public class PlayerPointsHook {
    private static boolean initialized = false;
    private static Object apiInstance = null;
    private static Method lookMethod = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            try {
                Class<?> ppClass = Class.forName("org.black_ixx.playerpoints.PlayerPoints");
                Method getInstance = ppClass.getMethod("getInstance");
                Object ppInstance = getInstance.invoke(null);
                
                Method getAPI = ppClass.getMethod("getAPI");
                apiInstance = getAPI.invoke(ppInstance);
                
                lookMethod = apiInstance.getClass().getMethod("look", UUID.class);
                
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: PlayerPoints successfully linked via Reflection!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link PlayerPoints.");
            }
        }
    }

    public static boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized || lookMethod == null || apiInstance == null) return null;
        try {
            JsonObject json = new JsonObject();
            int points = (int) lookMethod.invoke(apiInstance, player.getUniqueId());
            json.addProperty("balance", points);
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
