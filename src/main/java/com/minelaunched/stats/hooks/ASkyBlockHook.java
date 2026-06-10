package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class ASkyBlockHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "askyblock";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("island_level");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("ASkyBlock") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.askyblock.export_island_level", true)) {
            po.addProperty("island_level", true);
        }
    }
}
