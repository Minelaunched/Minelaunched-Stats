package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class ProtectionStonesHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "protectionstones";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("regions_count");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("ProtectionStones") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.protectionstones.export_regions_count", true)) {
            po.addProperty("regions_count", true);
        }
    }
}
