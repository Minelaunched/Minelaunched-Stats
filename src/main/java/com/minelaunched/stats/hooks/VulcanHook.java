package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class VulcanHook {
    private static boolean enabled = false;
    private static Object vulcanApi;
    private static Method getPlayerData;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("Vulcan") != null) {
            try {
                Class<?> vulcanClass = Class.forName("me.frep.vulcan.api.VulcanAPI");
                Class<?> factoryClass = Class.forName("me.frep.vulcan.api.VulcanAPI$Factory");
                Method getApi = factoryClass.getMethod("getApi");
                vulcanApi = getApi.invoke(null);
                
                getPlayerData = vulcanClass.getMethod("getPlayerData", Player.class);
                
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into Vulcan!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into Vulcan.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static int getViolations(Player player) {
        if (!enabled) return 0;
        try {
            Object playerData = getPlayerData.invoke(vulcanApi, player);
            if (playerData == null) return 0;
            
            int total = 0;
            for (Method method : playerData.getClass().getMethods()) {
                if (method.getName().endsWith("Violations") && method.getReturnType() == int.class) {
                    total += (int) method.invoke(playerData);
                }
            }
            return total;
        } catch (Exception e) {
            return 0;
        }
    }
}
