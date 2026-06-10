package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class TABHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "tab";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("tab_prefix");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("TAB") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.tab.export_tab_prefix", true)) {
            po.addProperty("tab_prefix", true);
        }
    }
}
