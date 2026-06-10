package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class SimpleClansHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object clanManager;
    private static Method getClanPlayerMethod;

    @Override
    public void init() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleClans");
        if (plugin != null) {
            try {
                Method getClanManager = plugin.getClass().getMethod("getClanManager");
                clanManager = getClanManager.invoke(plugin);
                getClanPlayerMethod = clanManager.getClass().getMethod("getClanPlayer", Player.class);
                enabled = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
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

    @Override
    public String getPluginName() {
        return "simpleclans";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("clan_name", "clan_tag");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject clanData = getClanData(p);
                    if (clanData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.simpleclans.export_clan_name", true) && clanData.has("name")) filtered.add("clan_name", clanData.get("name"));
                        if (config.getBoolean("hooks.simpleclans.export_clan_tag", true) && clanData.has("tag")) filtered.add("clan_tag", clanData.get("tag"));
                        if (filtered.size() > 0) po.add("simpleclans", filtered);
                    }
    }

}
