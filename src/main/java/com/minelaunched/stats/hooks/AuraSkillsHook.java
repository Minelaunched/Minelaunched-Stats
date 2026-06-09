package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;
import java.util.UUID;

public class AuraSkillsHook {
    private static boolean initialized = false;
    private static Object apiInstance = null;
    private static Method getUserMethod = null;
    private static Method getManaMethod = null;
    private static Method getPowerLevelMethod = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("AuraSkills") != null || Bukkit.getPluginManager().getPlugin("AureliumSkills") != null) {
            try {
                Class<?> apiClass;
                try {
                    apiClass = Class.forName("dev.aurelium.auraskills.api.AuraSkillsApi"); // AuraSkills (New)
                } catch (ClassNotFoundException e) {
                    apiClass = Class.forName("com.archyx.aureliumskills.api.AureliumAPI"); // AureliumSkills (Old)
                }

                Method getMethod = apiClass.getMethod("get");
                apiInstance = getMethod.invoke(null);
                
                getUserMethod = apiInstance.getClass().getMethod("getUser", UUID.class);
                
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: AuraSkills successfully linked via Reflection!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link AuraSkills.");
            }
        }
    }

    public static boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized || getUserMethod == null || apiInstance == null) return null;
        try {
            Object user = getUserMethod.invoke(apiInstance, player.getUniqueId());
            if (user == null) return null;

            if (getManaMethod == null) {
                try {
                    getManaMethod = user.getClass().getMethod("getMana");
                } catch (Exception ignored) {}
            }
            if (getPowerLevelMethod == null) {
                try {
                    getPowerLevelMethod = user.getClass().getMethod("getPowerLevel");
                } catch (Exception ignored) {}
            }

            JsonObject json = new JsonObject();
            if (getManaMethod != null) {
                json.addProperty("mana", (double) getManaMethod.invoke(user));
            }
            if (getPowerLevelMethod != null) {
                json.addProperty("power_level", (int) getPowerLevelMethod.invoke(user));
            }

            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
