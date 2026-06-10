package com.minelaunched.stats.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class PapiHook extends MinelaunchedHook {
    private static boolean initialized = false;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            initialized = true;
                    }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    public static String setPlaceholders(OfflinePlayer player, String text) {
        if (!initialized) return text;
        try {
            return PlaceholderAPI.setPlaceholders(player, text);
        } catch (Exception e) {
            return text;
        }
    }

    @Override
    public String getPluginName() {
        return "placeholderapi";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList();
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject papi = new JsonObject();
                    for (String ph : config.getStringList("hooks.placeholderapi.player_placeholders")) {
                        papi.addProperty(ph, setPlaceholders(p, ph));
                    }
                    po.add("placeholders", papi);
    }

    @Override
    public void appendServerStats(JsonObject server, FileConfiguration config) {
        JsonObject papi = new JsonObject();
                for (String ph : config.getStringList("hooks.placeholderapi.server_placeholders")) {
                    papi.addProperty(ph, setPlaceholders(null, ph));
                }
                server.add("placeholders", papi);
    }

}
