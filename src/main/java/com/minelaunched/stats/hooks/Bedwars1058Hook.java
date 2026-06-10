package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class Bedwars1058Hook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "bedwars1058";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("bedwars_level");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Bedwars1058") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.bedwars1058.export_bedwars_level", true)) {
            po.addProperty("bedwars_level", true);
        }
    }
}
