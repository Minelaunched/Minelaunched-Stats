package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class CitizensHook {
    private static boolean enabled = false;
    private static Object registry;
    private static Method isNPCMethod;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            try {
                Class<?> apiClass = Class.forName("net.citizensnpcs.api.CitizensAPI");
                Method getNPCRegistry = apiClass.getMethod("getNPCRegistry");
                registry = getNPCRegistry.invoke(null);
                
                isNPCMethod = registry.getClass().getMethod("isNPC", org.bukkit.entity.Entity.class);
                
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into Citizens!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into Citizens.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isNPC(Player player) {
        if (!enabled) return false;
        try {
            return (boolean) isNPCMethod.invoke(registry, player);
        } catch (Exception e) {
            return false;
        }
    }
}
