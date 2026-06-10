package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class BountyHuntersHook {
    private static boolean enabled = false;
    private static Object bountyManager;
    private static Method getBountyMethod;

    public static void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BountyHunters");
        if (plugin != null) {
            try {
                Class<?> pluginClass = Class.forName("net.Indyuce.bountyhunters.BountyHunters");
                Method getInstance = pluginClass.getMethod("getInstance");
                Object instance = getInstance.invoke(null);
                
                Method getBountyManagerMethod = instance.getClass().getMethod("getBountyManager");
                bountyManager = getBountyManagerMethod.invoke(instance);
                
                getBountyMethod = bountyManager.getClass().getMethod("getBounty", org.bukkit.OfflinePlayer.class);
                
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into BountyHunters!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into BountyHunters.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static double getBountyReward(Player player) {
        if (!enabled) return 0;
        try {
            Object bounty = getBountyMethod.invoke(bountyManager, player);
            if (bounty != null) {
                Method getReward = bounty.getClass().getMethod("getReward");
                return (double) getReward.invoke(bounty);
            }
        } catch (Exception e) {}
        return 0;
    }
}
