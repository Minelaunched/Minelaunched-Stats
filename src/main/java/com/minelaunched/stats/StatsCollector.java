package com.minelaunched.stats;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import com.minelaunched.stats.collectors.*;
import java.util.concurrent.Callable;

public class StatsCollector implements Callable<JsonObject> {

    private final FileConfiguration config;

    public StatsCollector(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public JsonObject call() {
        JsonObject root = new JsonObject();

        JsonObject systemData = SystemCollector.collect(config);
        if (systemData.has("system")) root.add("system", systemData.get("system"));

        JsonObject serverData = ServerCollector.collect(config);
        if (serverData.has("server")) root.add("server", serverData.get("server"));

        JsonObject worldData = WorldCollector.collect(config);
        if (worldData.has("worlds")) root.add("worlds", worldData.get("worlds"));

        JsonObject playerData = PlayerCollector.collect(config);
        if (playerData.has("players")) root.add("players", playerData.get("players"));

        JsonObject pluginData = PluginCollector.collect(config);
        if (pluginData.has("plugins")) root.add("plugins", pluginData.get("plugins"));

        return root;
    }
}
