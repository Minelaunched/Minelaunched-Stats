package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class SuperVanishHook {
    private static boolean enabled = false;
    private static Method isVanishedMethod;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("SuperVanish") != null || Bukkit.getPluginManager().getPlugin("PremiumVanish") != null) {
            try {
                Class<?> vanishApiClass = Class.forName("de.myzelyam.api.vanish.VanishAPI");
                isVanishedMethod = vanishApiClass.getMethod("isInvisible", Player.class);
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into SuperVanish/PremiumVanish!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into SuperVanish/PremiumVanish.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isVanished(Player player) {
        if (!enabled) return false;
        try {
            return (boolean) isVanishedMethod.invoke(null, player);
        } catch (Exception e) {
            return false;
        }
    }
}
