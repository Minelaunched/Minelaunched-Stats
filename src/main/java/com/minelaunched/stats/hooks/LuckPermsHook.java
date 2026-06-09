package com.minelaunched.stats.hooks;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

public class LuckPermsHook {
    private static LuckPerms api = null;
    private static boolean initialized = false;

    public static void init() {
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

    public static boolean isEnabled() {
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
}
