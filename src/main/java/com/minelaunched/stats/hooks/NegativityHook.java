package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class NegativityHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "negativity";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("negativity_violations");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Negativity") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.negativity.export_negativity_violations", true)) {
            po.addProperty("negativity_violations", true);
        }
    }
}
