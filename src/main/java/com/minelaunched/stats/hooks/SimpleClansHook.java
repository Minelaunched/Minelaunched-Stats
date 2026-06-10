package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class SimpleClansHook {
    private static boolean enabled = false;
    private static Object clanManager;
    private static Method getClanPlayerMethod;

    public static void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleClans");
        if (plugin != null) {
            try {
                Method getClanManager = plugin.getClass().getMethod("getClanManager");
                clanManager = getClanManager.invoke(plugin);
                getClanPlayerMethod = clanManager.getClass().getMethod("getClanPlayer", Player.class);
                enabled = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooked into SimpleClans!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Failed to hook into SimpleClans.");
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static JsonObject getClanData(Player player) {
        if (!enabled) return null;
        try {
            Object clanPlayer = getClanPlayerMethod.invoke(clanManager, player);
            if (clanPlayer != null) {
                Method getClan = clanPlayer.getClass().getMethod("getClan");
                Object clan = getClan.invoke(clanPlayer);
                if (clan != null) {
                    JsonObject data = new JsonObject();
                    Method getName = clan.getClass().getMethod("getName");
                    Method getTag = clan.getClass().getMethod("getTag");
                    data.addProperty("name", (String) getName.invoke(clan));
                    data.addProperty("tag", (String) getTag.invoke(clan));
                    return data;
                }
            }
        } catch (Exception e) {}
        return null;
    }
}
