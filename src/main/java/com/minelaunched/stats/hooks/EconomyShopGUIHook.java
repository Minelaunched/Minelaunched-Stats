package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class EconomyShopGUIHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "economyshopgui";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_in_shop");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("EconomyShopGUI") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.economyshopgui.export_is_in_shop", true)) {
            po.addProperty("is_in_shop", true);
        }
    }
}
