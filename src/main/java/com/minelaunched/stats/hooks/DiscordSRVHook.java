package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class DiscordSRVHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object discordSrvApi;
    private static Method getAccountLinkManager;
    private static Method getDiscordId;

    @Override
    public void init() {
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

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "discordsrv";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("discord_id");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        String discordId = getDiscordId(p);
                    if (discordId != null) po.addProperty("discord_id", discordId);
    }

}
