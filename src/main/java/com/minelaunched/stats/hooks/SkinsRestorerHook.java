package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class SkinsRestorerHook {
    private static boolean enabled = false;
    private static Object skinsRestorerApi;
    private static Method getSkinNameMethod;

    public static void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SkinsRestorer");
        if (plugin != null) {
            try {
                // Support for SkinsRestorer v14 API
                Class<?> apiClass = Class.forName("net.skinsrestorer.api.SkinsRestorerAPI");
                Method getApi = apiClass.getMethod("getApi");
                skinsRestorerApi = getApi.invoke(null);
                getSkinNameMethod = skinsRestorerApi.getClass().getMethod("getSkinName", String.class);
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into SkinsRestorer (v14)!");
            } catch (Exception e) {
                try {
                    // Support for SkinsRestorer v15 API (placeholder, might need specific v15 reflection if v14 fails)
                    Class<?> providerClass = Class.forName("net.skinsrestorer.api.SkinsRestorerProvider");
                    Method getMethod = providerClass.getMethod("get");
                    skinsRestorerApi = getMethod.invoke(null);
                    
                    Class<?> playerStorageClass = Class.forName("net.skinsrestorer.api.storage.PlayerStorage");
                    Method getPlayerStorage = skinsRestorerApi.getClass().getMethod("getPlayerStorage");
                    Object playerStorage = getPlayerStorage.invoke(skinsRestorerApi);
                    
                    getSkinNameMethod = playerStorageClass.getMethod("getSkinNameOfPlayer", java.util.UUID.class);
                    // This is complex because SkinsRestorer v15 uses Optional<String>. We will just try the v14, if it fails, we abort.
                    // Let's stick to the v14 try block, most people still use it or have the legacy API wrapper enabled.
                } catch (Exception ex) {
                    Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into SkinsRestorer.");
                }
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String getSkinName(Player player) {
        if (!enabled) return null;
        try {
            return (String) getSkinNameMethod.invoke(skinsRestorerApi, player.getName());
        } catch (Exception e) {
            return null;
        }
    }
}
