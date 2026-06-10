package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class SuperiorSkyblockHook {
    private static boolean enabled = false;
    private static Method getPlayerMethod;
    private static Method getIslandMethod;
    private static Method getIslandLevelMethod;
    private static Method getNameMethod;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("SuperiorSkyblock2") != null) {
            try {
                Class<?> apiClass = Class.forName("com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI");
                getPlayerMethod = apiClass.getMethod("getPlayer", java.util.UUID.class);
                
                Class<?> superiorPlayerClass = Class.forName("com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer");
                getIslandMethod = superiorPlayerClass.getMethod("getIsland");
                
                Class<?> islandClass = Class.forName("com.bgsoftware.superiorskyblock.api.island.Island");
                getIslandLevelMethod = islandClass.getMethod("getIslandLevel");
                getNameMethod = islandClass.getMethod("getName");
                
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into SuperiorSkyblock2!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into SuperiorSkyblock2.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!enabled) return null;
        try {
            Object superiorPlayer = getPlayerMethod.invoke(null, player.getUniqueId());
            if (superiorPlayer == null) return null;
            
            Object island = getIslandMethod.invoke(superiorPlayer);
            if (island == null) return null;
            
            JsonObject data = new JsonObject();
            
            Object level = getIslandLevelMethod.invoke(island);
            if (level != null && level instanceof Number) {
                data.addProperty("island_level", (Number) level);
            }
            
            Object name = getNameMethod.invoke(island);
            if (name != null) {
                data.addProperty("island_name", name.toString());
            }
            
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
