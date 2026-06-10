package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class DeluxeTagsHook extends MinelaunchedHook {
    private static boolean enabled = false;
    private static Method getPlayerDisplayTag;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("DeluxeTags") != null) {
            try {
                Class<?> apiClass = Class.forName("me.clip.deluxetags.DeluxeTag");
                getPlayerDisplayTag = apiClass.getMethod("getPlayerDisplayTag", Player.class);
                enabled = true;
                            } catch (Exception e) {
                            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public static String getActiveTag(Player player) {
        if (!enabled) return null;
        try {
            return (String) getPlayerDisplayTag.invoke(null, player);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getPluginName() {
        return "deluxetags";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("active_tag");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        String activeTag = getActiveTag(p);
                    if (activeTag != null) po.addProperty("active_tag", activeTag);
    }

}
