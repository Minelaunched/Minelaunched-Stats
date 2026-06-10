package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public class DiscordSRVHook {
    private static boolean enabled = false;
    private static Object discordSrvApi;
    private static Method getAccountLinkManager;
    private static Method getDiscordId;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("DiscordSRV") != null) {
            try {
                Class<?> discordSrvClass = Class.forName("github.scarsz.discordsrv.DiscordSRV");
                Method getPluginMethod = discordSrvClass.getMethod("getPlugin");
                Object pluginInstance = getPluginMethod.invoke(null);
                
                getAccountLinkManager = discordSrvClass.getMethod("getAccountLinkManager");
                Object accountLinkManager = getAccountLinkManager.invoke(pluginInstance);
                
                Class<?> accountLinkManagerClass = accountLinkManager.getClass();
                getDiscordId = accountLinkManagerClass.getMethod("getDiscordId", java.util.UUID.class);
                
                discordSrvApi = accountLinkManager;
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into DiscordSRV!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into DiscordSRV.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String getDiscordId(Player player) {
        if (!enabled) return null;
        try {
            return (String) getDiscordId.invoke(discordSrvApi, player.getUniqueId());
        } catch (Exception e) {
            return null;
        }
    }
}
