package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class FactionsHook {
    private static boolean enabled = false;
    private static Method getFPlayer;
    private static Method getFaction;
    private static Method getTag;
    private static Method getRole;

    public static void init() {
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
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into FactionsUUID!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into FactionsUUID.");
            }
        }
    }

    public static boolean isEnabled() {
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
}
