package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class GriefPreventionHook extends MinelaunchedHook {
    private static boolean initialized = false;
    private static Object dataStoreInstance = null;
    private static Method getPlayerDataMethod = null;
    private static Method getAccruedMethod = null;
    private static Method getBonusMethod = null;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("GriefPrevention") != null) {
            try {
                Class<?> gpClass = Class.forName("me.ryanhamshire.GriefPrevention.GriefPrevention");
                Field instanceField = gpClass.getField("instance");
                Object gpInstance = instanceField.get(null);
                
                Field dataStoreField = gpClass.getField("dataStore");
                dataStoreInstance = dataStoreField.get(gpInstance);
                
                getPlayerDataMethod = dataStoreInstance.getClass().getMethod("getPlayerData", UUID.class);
                
                // We resolve the other methods when fetching since PlayerData class is package-private or from the return type
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: GriefPrevention successfully linked via Reflection!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link GriefPrevention.");
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized || getPlayerDataMethod == null || dataStoreInstance == null) return null;
        try {
            Object playerData = getPlayerDataMethod.invoke(dataStoreInstance, player.getUniqueId());
            if (playerData == null) return null;
            
            if (getAccruedMethod == null) {
                getAccruedMethod = playerData.getClass().getMethod("getAccruedClaimBlocks");
            }
            if (getBonusMethod == null) {
                getBonusMethod = playerData.getClass().getMethod("getBonusClaimBlocks");
            }
            
            int accrued = (int) getAccruedMethod.invoke(playerData);
            int bonus = (int) getBonusMethod.invoke(playerData);
            
            JsonObject json = new JsonObject();
            json.addProperty("claim_blocks", accrued);
            json.addProperty("bonus_blocks", bonus);
            json.addProperty("total_blocks", accrued + bonus);
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @Override
    public String getPluginName() {
        return "griefprevention";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("claim_blocks", "bonus_blocks");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject gpData = getPlayerData(p);
                    if (gpData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.griefprevention.export_claim_blocks", true) && gpData.has("claim_blocks")) filtered.add("claim_blocks", gpData.get("claim_blocks"));
                        if (config.getBoolean("hooks.griefprevention.export_bonus_blocks", true) && gpData.has("bonus_blocks")) filtered.add("bonus_blocks", gpData.get("bonus_blocks"));
                        if (gpData.has("total_blocks")) filtered.add("total_blocks", gpData.get("total_blocks"));
                        if (filtered.size() > 0) po.add("griefprevention", filtered);
                    }
    }

}
