package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class TebexHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "tebex";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_donator");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Tebex") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.tebex.export_is_donator", true)) {
            po.addProperty("is_donator", true);
        }
    }
}
