package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class Pl3xMapHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "pl3xmap";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_hidden");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Pl3xMap") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.pl3xmap.export_is_hidden", true)) {
            po.addProperty("is_hidden", true);
        }
    }
}
