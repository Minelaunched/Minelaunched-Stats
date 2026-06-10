package com.minelaunched.stats.hooks;

import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class ViaVersionHook extends MinelaunchedHook {
    private static boolean initialized = false;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
            try {
                Class.forName("com.viaversion.viaversion.api.Via");
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
            int version = Via.getAPI().getPlayerVersion(player.getUniqueId());
            JsonObject json = new JsonObject();
            json.addProperty("protocol_version", version);
            return json;
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @Override
    public String getPluginName() {
        return "viaversion";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("protocol_version");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject viaData = getPlayerData(p);
                    if (viaData != null) po.add("viaversion", viaData);
    }

}
