package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class VotingPluginHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object userManager;
    private static Method getVotingPluginUserMethod;

    @Override
    public void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("VotingPlugin");
        if (plugin != null) {
            try {
                // Actually it's simpler to just use reflection on com.bencodez.votingplugin.user.UserManager
                Class<?> userManagerClass = Class.forName("com.bencodez.votingplugin.user.UserManager");
                Method getInstance = userManagerClass.getMethod("getInstance");
                userManager = getInstance.invoke(null);
                
                getVotingPluginUserMethod = userManagerClass.getMethod("getVotingPluginUser", java.util.UUID.class);
                
                enabled = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static int getTotalVotes(Player player) {
        if (!enabled) return 0;
        try {
            Object user = getVotingPluginUserMethod.invoke(userManager, player.getUniqueId());
            if (user != null) {
                Method getTotal = user.getClass().getMethod("getTotal");
                return (int) getTotal.invoke(user);
            }
        } catch (Exception e) {}
        return 0;
    }

    @Override
    public String getPluginName() {
        return "votingplugin";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("total_votes");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("total_votes", getTotalVotes(p));
    }

}
