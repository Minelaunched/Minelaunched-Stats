package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class AACHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "aac";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("aac_violations");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("AAC") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.aac.export_aac_violations", true)) {
            po.addProperty("aac_violations", true);
        }
    }
}
