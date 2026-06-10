package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class McMMOHook extends MinelaunchedHook {
    private static boolean initialized = false;
    private static Method getPowerLevelMethod = null;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
            try {
                Class<?> experienceAPI = Class.forName("com.gmail.nossr50.api.ExperienceAPI");
                getPowerLevelMethod = experienceAPI.getMethod("getPowerLevel", Player.class);
                initialized = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized || getPowerLevelMethod == null) return null;
        try {
            JsonObject json = new JsonObject();
            int powerLevel = (int) getPowerLevelMethod.invoke(null, player);
            json.addProperty("power_level", powerLevel);
            return json;
        } catch (Exception e) {
            // Ignore if player data isn't loaded yet
        }
        return null;
    }

    @Override
    public String getPluginName() {
        return "mcmmo";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("power_level");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject mcmmoData = getPlayerData(p);
                    if (mcmmoData != null) po.add("mcmmo", mcmmoData);
    }

}
