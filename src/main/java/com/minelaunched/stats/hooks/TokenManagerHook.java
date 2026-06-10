package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class TokenManagerHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Plugin tokenManagerPlugin;
    private static Method getTokensMethod;

    @Override
    public void init() {
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

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "tokenmanager";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("tokens");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("tokens", getTokens(p));
    }

}
