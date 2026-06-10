package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class VotifierHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "votifier";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("has_voted");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Votifier") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.votifier.export_has_voted", true)) {
            po.addProperty("has_voted", true);
        }
    }
}
