package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class TradeSystemHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "tradesystem";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_trading");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("TradeSystem") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.tradesystem.export_is_trading", true)) {
            po.addProperty("is_trading", true);
        }
    }
}
