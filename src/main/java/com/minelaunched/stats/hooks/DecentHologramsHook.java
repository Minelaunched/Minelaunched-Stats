package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class DecentHologramsHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "decentholograms";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("viewing_holograms");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("DecentHolograms") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.decentholograms.export_viewing_holograms", true)) {
            po.addProperty("viewing_holograms", true);
        }
    }
}
