package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;

public class CMIHook {
    private static boolean initialized = false;
    private static Class<?> cmiClass = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("CMI") != null) {
            try {
                cmiClass = Class.forName("com.Zrips.CMI.CMI");
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: CMI successfully linked via Reflection!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link CMI.");
            }
        }
    }

    public static boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized) return null;
        try {
            // CMI.getInstance().getPlayerManager().getUser(player)
            Method getInstance = cmiClass.getMethod("getInstance");
            Object cmiInstance = getInstance.invoke(null);
            
            Method getPlayerManager = cmiInstance.getClass().getMethod("getPlayerManager");
            Object playerManager = getPlayerManager.invoke(cmiInstance);
            
            Method getUser = playerManager.getClass().getMethod("getUser", Player.class);
            Object cmiUser = getUser.invoke(playerManager, player);
            
            if (cmiUser == null) return null;
            
            JsonObject json = new JsonObject();
            
            Method isAfk = cmiUser.getClass().getMethod("isAfk");
            json.addProperty("is_afk", (boolean) isAfk.invoke(cmiUser));
            
            Method isVanished = cmiUser.getClass().getMethod("isVanished");
            json.addProperty("is_vanished", (boolean) isVanished.invoke(cmiUser));
            
            Method isGod = cmiUser.getClass().getMethod("isGod");
            json.addProperty("is_god_mode", (boolean) isGod.invoke(cmiUser));
            
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
