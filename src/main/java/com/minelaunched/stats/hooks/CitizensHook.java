package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class CitizensHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Object registry;
    private static Method isNPCMethod;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
            try {
                Class<?> apiClass = Class.forName("net.citizensnpcs.api.CitizensAPI");
                Method getNPCRegistry = apiClass.getMethod("getNPCRegistry");
                registry = getNPCRegistry.invoke(null);
                
                isNPCMethod = registry.getClass().getMethod("isNPC", org.bukkit.entity.Entity.class);
                
                enabled = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static boolean isNPC(Player player) {
        if (!enabled) return false;
        try {
            return (boolean) isNPCMethod.invoke(registry, player);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPluginName() {
        return "citizens";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("is_npc");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("is_npc", isNPC(p));
    }

}
