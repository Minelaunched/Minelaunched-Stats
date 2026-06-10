package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class CombatLogXHook {
    private static boolean enabled = false;
    private static Object combatManager;
    private static Method isInCombatMethod;

    public static void init() {
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

    public static boolean isEnabled() {
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
}
