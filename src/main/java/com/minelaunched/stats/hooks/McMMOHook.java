package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;

public class McMMOHook {
    private static boolean initialized = false;
    private static Method getPowerLevelMethod = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
            try {
                Class<?> experienceAPI = Class.forName("com.gmail.nossr50.api.ExperienceAPI");
                getPowerLevelMethod = experienceAPI.getMethod("getPowerLevel", Player.class);
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: mcMMO successfully linked via Reflection!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link mcMMO.");
            }
        }
    }

    public static boolean isEnabled() {
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
}
