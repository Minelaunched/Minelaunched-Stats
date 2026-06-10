package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class DeluxeTagsHook {
    private static boolean enabled = false;
    private static Method getPlayerDisplayTag;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("DeluxeTags") != null) {
            try {
                Class<?> apiClass = Class.forName("me.clip.deluxetags.DeluxeTag");
                getPlayerDisplayTag = apiClass.getMethod("getPlayerDisplayTag", Player.class);
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into DeluxeTags!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into DeluxeTags.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String getActiveTag(Player player) {
        if (!enabled) return null;
        try {
            return (String) getPlayerDisplayTag.invoke(null, player);
        } catch (Exception e) {
            return null;
        }
    }
}
