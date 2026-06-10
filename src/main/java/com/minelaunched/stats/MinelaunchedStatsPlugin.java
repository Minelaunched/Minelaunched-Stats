package com.minelaunched.stats;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

import com.minelaunched.stats.network.WebServer;
import com.minelaunched.stats.network.RedisManager;
import com.minelaunched.stats.utils.TpsTracker;
import com.minelaunched.stats.utils.AutoUpdater;

public class MinelaunchedStatsPlugin extends JavaPlugin {

    private WebServer webServer;
    private TpsTracker tpsTracker;
    private RedisManager redisManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        tpsTracker = new TpsTracker();
        tpsTracker.runTaskTimer(this, 0L, 1L);

        // Initialize Plugin Hooks safely
        // Initialize Hooks after 1 tick to ensure all other plugins are loaded (eliminates the need for softdepend)
        Bukkit.getScheduler().runTask(this, () -> {
            com.minelaunched.stats.hooks.HookManager.initAll();
            com.minelaunched.stats.hooks.HookManager.registerAllDefaults(getConfig());
            getConfig().options().copyDefaults(true);
            saveConfig();
        });
        


        // Initialize Web Server
        int port = getConfig().getInt("web.port", 8080);
        webServer = new WebServer(this, port);
        try {
            webServer.start();
        } catch (Exception e) {
            getLogger().severe("Failed to start web server on port " + port);
            e.printStackTrace();
        }

        // Auto Updater
        new AutoUpdater(this).checkForUpdates();

        // Redis
        if (getConfig().getBoolean("redis.enabled", false)) {
            try {
                redisManager = new RedisManager(this);
            } catch (Exception e) {
                getLogger().severe("Failed to connect to Redis: " + e.getMessage());
            }
        }

        getLogger().info("MinelaunchedStats enabled and listening on port " + port + "!");
    }

    @Override
    public void onDisable() {
        if (webServer != null) {
            webServer.stop();
        }
        if (tpsTracker != null) {
            tpsTracker.cancel();
        }
        if (redisManager != null) {
            redisManager.stop();
        }
        getLogger().info("MinelaunchedStats disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mlstats")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("mlstats.admin")) {
                    reloadPlugin();
                    sender.sendMessage("§a[MinelaunchedStats] Configuration and WebServer reloaded successfully!");
                    return true;
                } else {
                    sender.sendMessage("§cYou don't have permission.");
                    return true;
                }
            }
        }
        Bukkit.getScheduler().cancelTasks(this);
        return false;
    }

    public void reloadPlugin() {
        // Stop Web Server
        if (webServer != null) {
            webServer.stop();
        }
        // Stop TPS tracker
        if (tpsTracker != null) {
            tpsTracker.cancel();
        }
        // Stop Redis
        if (redisManager != null) {
            redisManager.stop();
            redisManager = null;
        }
        
        // Reload config
        reloadConfig();
        com.minelaunched.stats.hooks.HookManager.registerAllDefaults(getConfig());
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        // Restart TPS tracker
        tpsTracker = new TpsTracker();
        tpsTracker.runTaskTimer(this, 0L, 1L);

        // Restart Web Server
        int port = getConfig().getInt("web.port", 8080);
        webServer = new WebServer(this, port);
        try {
            webServer.start();
        } catch (Exception e) {
            getLogger().severe("Failed to restart web server on port " + port);
        }
        
        // Restart Redis
        if (getConfig().getBoolean("redis.enabled", false)) {
            try {
                redisManager = new RedisManager(this);
            } catch (Exception e) {
                getLogger().severe("Failed to reconnect to Redis: " + e.getMessage());
            }
        }
        
        getLogger().info("MinelaunchedStats config and server reloaded.");
    }
}
