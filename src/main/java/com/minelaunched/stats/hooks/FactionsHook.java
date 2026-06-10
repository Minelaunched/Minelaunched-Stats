package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class FactionsHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Method getFPlayer;
    private static Method getFaction;
    private static Method getTag;
    private static Method getRole;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Factions") != null) {
            try {
                Class<?> fPlayersClass = Class.forName("com.massivecraft.factions.FPlayers");
                Method getInstance = fPlayersClass.getMethod("getInstance");
                Object fPlayers = getInstance.invoke(null);
                
                getFPlayer = fPlayersClass.getMethod("getByPlayer", Player.class);
                
                Class<?> fPlayerClass = Class.forName("com.massivecraft.factions.FPlayer");
                getFaction = fPlayerClass.getMethod("getFaction");
                getRole = fPlayerClass.getMethod("getRole");
                
                Class<?> factionClass = Class.forName("com.massivecraft.factions.Faction");
                getTag = factionClass.getMethod("getTag");
                
                enabled = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static String getFactionName(Player player) {
        if (!enabled) return null;
        try {
            Object fPlayer = getFPlayer.invoke(null, player);
            if (fPlayer == null) return null;
            Object faction = getFaction.invoke(fPlayer);
            if (faction == null) return null;
            String tag = (String) getTag.invoke(faction);
            // Some Factions return wilderness or stateless if no faction
            if (tag == null || tag.contains("Wilderness")) return null;
            return tag;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getFactionRole(Player player) {
        if (!enabled) return null;
        try {
            Object fPlayer = getFPlayer.invoke(null, player);
            if (fPlayer == null) return null;
            Object role = getRole.invoke(fPlayer);
            return role != null ? role.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getPluginName() {
        return "factionsuuid";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("faction_role", "faction_name");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject factionsData = new JsonObject();
                    if (config.getBoolean("hooks.factionsuuid.export_faction_name", true)) {
                        String fName = getFactionName(p);
                        if (fName != null) factionsData.addProperty("name", fName);
                    }
                    if (config.getBoolean("hooks.factionsuuid.export_faction_role", true)) {
                        String fRole = getFactionRole(p);
                        if (fRole != null) factionsData.addProperty("role", fRole);
                    }
                    if (factionsData.size() > 0) po.add("factions", factionsData);
    }

}
