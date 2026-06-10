package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class HuskyCratesHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "huskycrates";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("crate_keys");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("HuskyCrates") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.huskycrates.export_crate_keys", true)) {
            po.addProperty("crate_keys", true);
        }
    }
}
