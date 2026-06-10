package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class SkillAPIHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "skillapi";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("class_level");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("SkillAPI") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.skillapi.export_class_level", true)) {
            po.addProperty("class_level", true);
        }
    }
}
