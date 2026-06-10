package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class PvPManagerHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object playerHandler;
    private static Method getPvPPlayerMethod;

    @Override
    public void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PvPManager");
        if (plugin != null) {
            try {
                Method getPlayerHandler = plugin.getClass().getMethod("getPlayerHandler");
                playerHandler = getPlayerHandler.invoke(plugin);
                getPvPPlayerMethod = playerHandler.getClass().getMethod("get", Player.class);
                enabled = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "pvpmanager";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("has_pvp_enabled", "is_in_combat");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.pvpmanager.export_is_in_combat", true)) po.addProperty("is_in_pvpmanager_combat", isInCombat(p));
                    if (config.getBoolean("hooks.pvpmanager.export_has_pvp_enabled", true)) po.addProperty("has_pvp_enabled", hasPvPEnabled(p));
    }

}
