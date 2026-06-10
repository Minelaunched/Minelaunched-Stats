package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class IntaveHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "intave";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("intave_violations");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Intave") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.intave.export_intave_violations", true)) {
            po.addProperty("intave_violations", true);
        }
    }
}
