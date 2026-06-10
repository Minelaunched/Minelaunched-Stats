package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class SuperiorSkyblockHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Method getPlayerMethod;
    private static Method getIslandMethod;
    private static Method getIslandLevelMethod;
    private static Method getNameMethod;

    @Override
    public void init() {
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
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "superiorskyblock2";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("island_name", "island_level");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject ssData = getPlayerData(p);
                    if (ssData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.superiorskyblock2.export_island_level", true) && ssData.has("island_level")) filtered.add("island_level", ssData.get("island_level"));
                        if (config.getBoolean("hooks.superiorskyblock2.export_island_name", true) && ssData.has("island_name")) filtered.add("island_name", ssData.get("island_name"));
                        if (filtered.size() > 0) po.add("superiorskyblock2", filtered);
                    }
    }

}
