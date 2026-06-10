package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Collection;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class LandsHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object landsIntegration;
    private static Method getLandsPlayer;
    private static Method getLands;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Lands") != null) {
            try {
                Class<?> landsIntegrationClass = Class.forName("me.angeschossen.lands.api.LandsIntegration");
                Method ofMethod = landsIntegrationClass.getMethod("of", org.bukkit.plugin.Plugin.class);
                landsIntegration = ofMethod.invoke(null, Bukkit.getPluginManager().getPlugin("MinelaunchedStats"));
                
                getLandsPlayer = landsIntegrationClass.getMethod("getLandPlayer", java.util.UUID.class);
                
                Class<?> landPlayerClass = Class.forName("me.angeschossen.lands.api.player.LandPlayer");
                getLands = landPlayerClass.getMethod("getLands");
                
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into Lands!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into Lands.");
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static int getLandsCount(Player player) {
        if (!enabled) return 0;
        try {
            Object landPlayer = getLandsPlayer.invoke(landsIntegration, player.getUniqueId());
            if (landPlayer == null) return 0;
            Collection<?> lands = (Collection<?>) getLands.invoke(landPlayer);
            return lands.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getPluginName() {
        return "lands";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("lands_count");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("lands_count", getLandsCount(p));
    }

}
