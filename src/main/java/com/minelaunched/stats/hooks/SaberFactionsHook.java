package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class SaberFactionsHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "saberfactions";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("faction_power");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("SaberFactions") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.saberfactions.export_faction_power", true)) {
            po.addProperty("faction_power", true);
        }
    }
}
