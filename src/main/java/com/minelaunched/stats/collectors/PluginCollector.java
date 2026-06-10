package com.minelaunched.stats.collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.lang.reflect.Method;

public class PluginCollector {
    public static JsonObject collect(FileConfiguration config) {
        JsonObject root = new JsonObject(); // Fake root to hold the collected data, wait actually we should return the collected array/object directly.
        // Actually, the blocks add directly to `root`. So let's provide a `root` and return the element it added.
        // Let's just wrap the block as it is, since it adds to `root`.
        JsonObject result = new JsonObject();
        JsonObject rootSubstitute = new JsonObject();
        // 5. Plugins
        if (config.getBoolean("stats.plugins.enabled", true)) {
            JsonArray pluginsArray = new JsonArray();
            String cp = "stats.plugins";
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                JsonObject pl = new JsonObject();
                CollectorUtils.addSafe(config, pl, cp, "name", () -> plugin.getName());
                CollectorUtils.addSafe(config, pl, cp, "version", () -> plugin.getDescription().getVersion());
                CollectorUtils.addSafe(config, pl, cp, "plugin_enabled", "enabled", () -> plugin.isEnabled());
                
                if (config.getBoolean(cp + ".author", true) && plugin.getDescription().getAuthors() != null && !plugin.getDescription().getAuthors().isEmpty()) {
                    pl.addProperty("author", String.join(", ", plugin.getDescription().getAuthors()));
                }
                if (config.getBoolean(cp + ".description", true) && plugin.getDescription().getDescription() != null) {
                    pl.addProperty("description", plugin.getDescription().getDescription());
                }
                if (config.getBoolean(cp + ".website", true) && plugin.getDescription().getWebsite() != null) {
                    pl.addProperty("website", plugin.getDescription().getWebsite());
                }
                pluginsArray.add(pl);
            }
            rootSubstitute.add("plugins", pluginsArray);
        }

        
        // The block puts its result into rootSubstitute under a key (e.g. "system"). 
        // We just return that element.
        if (rootSubstitute.has("plugin")) return rootSubstitute.getAsJsonObject("plugin");
        if (rootSubstitute.has("online_players")) return rootSubstitute; // Quick hack for players since playersData is what we want... wait, "players" is the key.
        if (rootSubstitute.has("plugins")) return rootSubstitute.getAsJsonArray("plugins") != null ? new JsonObject() : new JsonObject();
        
        // Actually, let's just return the rootSubstitute, and let StatsCollector combine them via addAll.
        return rootSubstitute;
    }
}
