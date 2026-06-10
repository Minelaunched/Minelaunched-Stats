package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class BountyHuntersHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object bountyManager;
    private static Method getBountyMethod;

    @Override
    public void init() {
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
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "bountyhunters";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("bounty_reward");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("bounty_reward", getBountyReward(p));
    }

}
