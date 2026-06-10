package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class EcoEnchantsHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "ecoenchants";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("custom_enchants");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("EcoEnchants") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.ecoenchants.export_custom_enchants", true)) {
            po.addProperty("custom_enchants", true);
        }
    }
}
