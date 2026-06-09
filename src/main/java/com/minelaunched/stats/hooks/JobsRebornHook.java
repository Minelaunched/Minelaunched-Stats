package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.reflect.Method;
import java.util.List;

public class JobsRebornHook {
    private static boolean initialized = false;
    private static Class<?> jobsClass = null;

    public static void init() {
        if (Bukkit.getPluginManager().getPlugin("Jobs") != null) {
            try {
                jobsClass = Class.forName("com.gamingmesh.jobs.Jobs");
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: Jobs Reborn successfully linked via Reflection!");
            } catch (Exception e) {
                Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link Jobs Reborn.");
            }
        }
    }

    public static boolean isEnabled() {
        return initialized;
    }

    public static JsonObject getPlayerData(Player player) {
        if (!initialized) return null;
        try {
            // Jobs.getPlayerManager().getJobsPlayer(player)
            Method getPlayerManager = jobsClass.getMethod("getPlayerManager");
            Object playerManager = getPlayerManager.invoke(null);
            
            Method getJobsPlayer = playerManager.getClass().getMethod("getJobsPlayer", Player.class);
            Object jobsPlayer = getJobsPlayer.invoke(playerManager, player);
            
            if (jobsPlayer == null) return null;
            
            Method getJobProgression = jobsPlayer.getClass().getMethod("getJobProgression");
            List<?> progressionList = (List<?>) getJobProgression.invoke(jobsPlayer);
            
            JsonObject json = new JsonObject();
            JsonArray jobsArray = new JsonArray();
            
            for (Object prog : progressionList) {
                Method getJob = prog.getClass().getMethod("getJob");
                Object job = getJob.invoke(prog);
                
                Method getName = job.getClass().getMethod("getName");
                String name = (String) getName.invoke(job);
                
                Method getLevel = prog.getClass().getMethod("getLevel");
                int level = (int) getLevel.invoke(prog);
                
                JsonObject j = new JsonObject();
                j.addProperty("name", name);
                j.addProperty("level", level);
                jobsArray.add(j);
            }
            
            json.add("list", jobsArray);
            return json;
            
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
