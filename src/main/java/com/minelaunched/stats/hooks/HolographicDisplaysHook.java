package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class HolographicDisplaysHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "holographicdisplays";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("viewing_holograms");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.holographicdisplays.export_viewing_holograms", true)) {
            po.addProperty("viewing_holograms", true);
        }
    }
}
