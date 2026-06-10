package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class MMOCoreHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "mmocore";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("level", "mana");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("MMOCore") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.mmocore.export_level", true)) {
            po.addProperty("level", true);
        }
        if (config.getBoolean("hooks.mmocore.export_mana", true)) {
            po.addProperty("mana", true);
        }
    }
}
