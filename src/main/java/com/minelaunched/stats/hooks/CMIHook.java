package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class CMIHook extends MinelaunchedHook {
    private static boolean initialized = false;
    private static Class<?> cmiClass = null;

    @Override
    public void init() {
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

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "cmi";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("afk", "vanish", "god_mode");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject cmiData = getPlayerData(p);
                    if (cmiData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.cmi.export_afk", true) && cmiData.has("is_afk")) filtered.add("is_afk", cmiData.get("is_afk"));
                        if (config.getBoolean("hooks.cmi.export_vanish", true) && cmiData.has("is_vanished")) filtered.add("is_vanished", cmiData.get("is_vanished"));
                        if (config.getBoolean("hooks.cmi.export_god_mode", true) && cmiData.has("is_god_mode")) filtered.add("is_god_mode", cmiData.get("is_god_mode"));
                        if (filtered.size() > 0) po.add("cmi", filtered);
                    }
    }

}
