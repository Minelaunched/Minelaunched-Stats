package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class AuthMeHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "authme";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_logged_in");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("AuthMe") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.authme.export_is_logged_in", true)) {
            po.addProperty("is_logged_in", true);
        }
    }
}
