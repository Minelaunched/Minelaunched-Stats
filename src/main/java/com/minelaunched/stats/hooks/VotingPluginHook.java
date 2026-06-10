package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class VotingPluginHook {
    private static boolean enabled = false;
    private static Object userManager;
    private static Method getVotingPluginUserMethod;

    public static void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("VotingPlugin");
        if (plugin != null) {
            try {
                // Actually it's simpler to just use reflection on com.bencodez.votingplugin.user.UserManager
                Class<?> userManagerClass = Class.forName("com.bencodez.votingplugin.user.UserManager");
                Method getInstance = userManagerClass.getMethod("getInstance");
                userManager = getInstance.invoke(null);
                
                getVotingPluginUserMethod = userManagerClass.getMethod("getVotingPluginUser", java.util.UUID.class);
                
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into VotingPlugin!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into VotingPlugin.");
            }
        }
    }

    public static boolean isEnabled() {
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
}
