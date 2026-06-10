package com.minelaunched.stats.hooks;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class EssentialsHook extends MinelaunchedHook {
    private static Essentials ess = null;
    private static boolean initialized = false;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            try {
                ess = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: EssentialsX successfully linked!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link EssentialsX.");
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized || ess == null) return null;
        try {
            User user = ess.getUser(player);
            if (user != null) {
                JsonObject json = new JsonObject();
                json.addProperty("is_afk", user.isAfk());
                json.addProperty("is_vanished", user.isVanished());
                json.addProperty("is_god_mode", user.isGodModeEnabled());
                json.addProperty("is_muted", user.isMuted());
                
                String nickname = user.getNickname();
                if (nickname != null) {
                    json.addProperty("nickname", nickname);
                }
                
                return json;
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @Override
    public String getPluginName() {
        return "essentials";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("afk", "nickname", "god_mode", "muted", "vanish");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject essData = getPlayerData(p);
                    if (essData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.essentials.export_afk", true) && essData.has("is_afk")) filtered.add("is_afk", essData.get("is_afk"));
                        if (config.getBoolean("hooks.essentials.export_vanish", true) && essData.has("is_vanished")) filtered.add("is_vanished", essData.get("is_vanished"));
                        if (config.getBoolean("hooks.essentials.export_god_mode", true) && essData.has("is_god_mode")) filtered.add("is_god_mode", essData.get("is_god_mode"));
                        if (config.getBoolean("hooks.essentials.export_muted", true) && essData.has("is_muted")) filtered.add("is_muted", essData.get("is_muted"));
                        if (config.getBoolean("hooks.essentials.export_nickname", true) && essData.has("nickname")) filtered.add("nickname", essData.get("nickname"));
                        if (filtered.size() > 0) po.add("essentials", filtered);
                    }
    }

}
