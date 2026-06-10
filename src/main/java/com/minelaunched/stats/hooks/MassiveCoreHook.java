package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class MassiveCoreHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "massivecore";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("faction_power");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("MassiveCore") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.massivecore.export_faction_power", true)) {
            po.addProperty("faction_power", true);
        }
    }
}
