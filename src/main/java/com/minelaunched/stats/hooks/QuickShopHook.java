package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class QuickShopHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "quickshop";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("shop_count");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("QuickShop") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.quickshop.export_shop_count", true)) {
            po.addProperty("shop_count", true);
        }
    }
}
