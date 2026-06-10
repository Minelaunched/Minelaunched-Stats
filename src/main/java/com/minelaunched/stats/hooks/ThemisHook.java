package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class ThemisHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "themis";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("themis_violations");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Themis") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.themis.export_themis_violations", true)) {
            po.addProperty("themis_violations", true);
        }
    }
}
