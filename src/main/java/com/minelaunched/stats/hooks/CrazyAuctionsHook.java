package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class CrazyAuctionsHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "crazyauctions";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("active_listings");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("CrazyAuctions") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.crazyauctions.export_active_listings", true)) {
            po.addProperty("active_listings", true);
        }
    }
}
