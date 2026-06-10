package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class SuperVanishHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Method isVanishedMethod;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("SuperVanish") != null || Bukkit.getPluginManager().getPlugin("PremiumVanish") != null) {
            try {
                Class<?> vanishApiClass = Class.forName("de.myzelyam.api.vanish.VanishAPI");
                isVanishedMethod = vanishApiClass.getMethod("isInvisible", Player.class);
                enabled = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static boolean isVanished(Player player) {
        if (!enabled) return false;
        try {
            return (boolean) isVanishedMethod.invoke(null, player);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPluginName() {
        return "supervanish";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_vanished");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("is_supervanished", isVanished(p));
    }

}
