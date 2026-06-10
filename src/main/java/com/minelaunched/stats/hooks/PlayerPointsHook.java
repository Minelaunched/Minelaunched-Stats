package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;
import java.util.UUID;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerPointsHook extends MinelaunchedHook {
    private static boolean initialized = false;
    private static Object apiInstance = null;
    private static Method lookMethod = null;

    @Override
    public void init() {
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

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "playerpoints";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("balance");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject ppData = getPlayerData(p);
                    if (ppData != null) po.add("playerpoints", ppData);
    }

}
