package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class PvPManagerHook {
    private static boolean enabled = false;
    private static Object playerHandler;
    private static Method getPvPPlayerMethod;

    public static void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PvPManager");
        if (plugin != null) {
            try {
                Method getPlayerHandler = plugin.getClass().getMethod("getPlayerHandler");
                playerHandler = getPlayerHandler.invoke(plugin);
                getPvPPlayerMethod = playerHandler.getClass().getMethod("get", Player.class);
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into PvPManager!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into PvPManager.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isInCombat(Player player) {
        if (!enabled) return false;
        try {
            Object pvpPlayer = getPvPPlayerMethod.invoke(playerHandler, player);
            if (pvpPlayer != null) {
                Method isInCombat = pvpPlayer.getClass().getMethod("isInCombat");
                return (boolean) isInCombat.invoke(pvpPlayer);
            }
        } catch (Exception e) {}
        return false;
    }

    public static boolean hasPvPEnabled(Player player) {
        if (!enabled) return false;
        try {
            Object pvpPlayer = getPvPPlayerMethod.invoke(playerHandler, player);
            if (pvpPlayer != null) {
                Method hasPvPEnabled = pvpPlayer.getClass().getMethod("hasPvPEnabled");
                return (boolean) hasPvPEnabled.invoke(pvpPlayer);
            }
        } catch (Exception e) {}
        return false;
    }
}
