package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class TokenManagerHook {
    private static boolean enabled = false;
    private static Plugin tokenManagerPlugin;
    private static Method getTokensMethod;

    public static void init() {
        tokenManagerPlugin = Bukkit.getPluginManager().getPlugin("TokenManager");
        if (tokenManagerPlugin != null) {
            try {
                getTokensMethod = tokenManagerPlugin.getClass().getMethod("getTokens", Player.class);
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into TokenManager!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into TokenManager.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static long getTokens(Player player) {
        if (!enabled) return 0;
        try {
            Object optionalLong = getTokensMethod.invoke(tokenManagerPlugin, player);
            if (optionalLong != null) {
                // It's a java.util.OptionalLong
                Method isPresent = optionalLong.getClass().getMethod("isPresent");
                boolean present = (boolean) isPresent.invoke(optionalLong);
                if (present) {
                    Method getAsLong = optionalLong.getClass().getMethod("getAsLong");
                    return (long) getAsLong.invoke(optionalLong);
                }
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
