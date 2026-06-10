package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;

public class HeroesHook extends MinelaunchedHook {

    private boolean initialized = false;

    @Override
    public String getPluginName() {
        return "heroes";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("hero_level");
    }

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Heroes") != null) {
            initialized = true;
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    @Override
    public void appendPlayerStats(JsonObject po, Player p, FileConfiguration config) {
        if (config.getBoolean("hooks.heroes.export_hero_level", true)) {
            po.addProperty("hero_level", true);
        }
    }
}
