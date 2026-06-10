package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class CombatLogXHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object combatManager;
    private static Method isInCombatMethod;

    @Override
    public void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CombatLogX");
        if (plugin != null) {
            try {
                Method getCombatManager = plugin.getClass().getMethod("getCombatManager");
                combatManager = getCombatManager.invoke(plugin);
                isInCombatMethod = combatManager.getClass().getMethod("isInCombat", Player.class);
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into CombatLogX!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into CombatLogX.");
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
            return (boolean) isInCombatMethod.invoke(combatManager, player);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPluginName() {
        return "combatlogx";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_in_combat");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("is_in_combat", isInCombat(p));
    }

}
