package com.minelaunched.stats.hooks;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonObject;

public class EssentialsHook {
    private static Essentials ess = null;
    private static boolean initialized = false;

    public static void init() {
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

    public static boolean isEnabled() {
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
}
