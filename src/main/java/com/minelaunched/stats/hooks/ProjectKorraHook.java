package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class ProjectKorraHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "projectkorra";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("has_element");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("ProjectKorra") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.projectkorra.export_has_element", true)) {
            po.addProperty("has_element", true);
        }
    }
}
