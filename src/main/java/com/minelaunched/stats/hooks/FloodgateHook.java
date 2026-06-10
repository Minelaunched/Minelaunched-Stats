package com.minelaunched.stats.hooks;

import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class FloodgateHook extends MinelaunchedHook {
    private static boolean initialized = false;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
            try {
                Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                initialized = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized) return null;
        try {
            FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
            JsonObject json = new JsonObject();
            if (fPlayer != null) {
                json.addProperty("is_bedrock", true);
                json.addProperty("device_os", fPlayer.getDeviceOs().name());
                json.addProperty("language_code", fPlayer.getLanguageCode());
            } else {
                json.addProperty("is_bedrock", false);
            }
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @Override
    public String getPluginName() {
        return "floodgate";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_bedrock", "device_os");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject floodgateData = getPlayerData(p);
                    if (floodgateData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.floodgate.export_is_bedrock", true) && floodgateData.has("is_bedrock")) filtered.add("is_bedrock", floodgateData.get("is_bedrock"));
                        if (config.getBoolean("hooks.floodgate.export_device_os", true) && floodgateData.has("device_os")) filtered.add("device_os", floodgateData.get("device_os"));
                        if (floodgateData.has("language_code")) filtered.add("language_code", floodgateData.get("language_code"));
                        if (filtered.size() > 0) po.add("floodgate", filtered);
                    }
    }

}
