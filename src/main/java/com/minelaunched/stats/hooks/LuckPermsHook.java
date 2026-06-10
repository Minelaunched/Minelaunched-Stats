package com.minelaunched.stats.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class LuckPermsHook extends MinelaunchedHook {
    private static LuckPerms api = null;
    private static boolean initialized = false;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                api = LuckPermsProvider.get();
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: LuckPerms successfully linked!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link LuckPerms.");
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized || api == null) return null;
        try {
            User user = api.getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                JsonObject json = new JsonObject();
                json.addProperty("primary_group", user.getPrimaryGroup());
                
                String prefix = user.getCachedData().getMetaData().getPrefix();
                if (prefix != null) json.addProperty("prefix", prefix);
                
                String suffix = user.getCachedData().getMetaData().getSuffix();
                if (suffix != null) json.addProperty("suffix", suffix);
                
                return json;
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }

    @Override
    public String getPluginName() {
        return "luckperms";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("suffix", "prefix", "primary_group");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        JsonObject lpData = getPlayerData(p);
                    if (lpData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.luckperms.export_primary_group", true) && lpData.has("primary_group")) filtered.add("primary_group", lpData.get("primary_group"));
                        if (config.getBoolean("hooks.luckperms.export_prefix", true) && lpData.has("prefix")) filtered.add("prefix", lpData.get("prefix"));
                        if (config.getBoolean("hooks.luckperms.export_suffix", true) && lpData.has("suffix")) filtered.add("suffix", lpData.get("suffix"));
                        if (filtered.size() > 0) po.add("luckperms", filtered);
                    }
    }

}
