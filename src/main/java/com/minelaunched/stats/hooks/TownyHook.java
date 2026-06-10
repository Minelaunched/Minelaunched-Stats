package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class TownyHook extends MinelaunchedHook {
    private static boolean initialized = false;
    private static Class<?> townyUniverseClass = null;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Towny") != null) {
            try {
                townyUniverseClass = Class.forName("com.palmergames.bukkit.towny.TownyUniverse");
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: Towny successfully linked via Reflection!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link Towny.");
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
            // TownyUniverse.getInstance().getResident(player.getUniqueId())
            Method getInstance = townyUniverseClass.getMethod("getInstance");
            Object universe = getInstance.invoke(null);
            
            Method getResident = universe.getClass().getMethod("getResident", java.util.UUID.class);
            Object resident = getResident.invoke(universe, player.getUniqueId());
            
            if (resident == null) return null;
            
            JsonObject json = new JsonObject();
            
            Method hasTown = resident.getClass().getMethod("hasTown");
            if ((boolean) hasTown.invoke(resident)) {
                Method getTown = resident.getClass().getMethod("getTown");
                Object town = getTown.invoke(resident);
                Method getName = town.getClass().getMethod("getName");
                json.addProperty("town", (String) getName.invoke(town));
                
                Method getTitle = resident.getClass().getMethod("getTitle");
                String title = (String) getTitle.invoke(resident);
                if (title != null && !title.isEmpty()) json.addProperty("title", title);
                
                Method hasNation = town.getClass().getMethod("hasNation");
                if ((boolean) hasNation.invoke(town)) {
                    Method getNation = town.getClass().getMethod("getNation");
                    Object nation = getNation.invoke(town);
                    Method getNationName = nation.getClass().getMethod("getName");
                    json.addProperty("nation", (String) getNationName.invoke(nation));
                }
            }
            
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @Override
    public String getPluginName() {
        return "towny";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("title", "town", "nation");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject townyData = getPlayerData(p);
                    if (townyData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.towny.export_town", true) && townyData.has("town")) filtered.add("town", townyData.get("town"));
                        if (config.getBoolean("hooks.towny.export_nation", true) && townyData.has("nation")) filtered.add("nation", townyData.get("nation"));
                        if (config.getBoolean("hooks.towny.export_title", true) && townyData.has("title")) filtered.add("title", townyData.get("title"));
                        if (filtered.size() > 0) po.add("towny", filtered);
                    }
    }

}
