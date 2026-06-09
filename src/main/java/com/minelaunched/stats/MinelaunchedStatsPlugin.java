package com.minelaunched.stats;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class MinelaunchedStatsPlugin extends JavaPlugin {

    private WebServer webServer;
    private TpsTracker tpsTracker;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        tpsTracker = new TpsTracker();
        tpsTracker.runTaskTimer(this, 0L, 1L);

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
        getLogger().info("MinelaunchedStats disabled!");
    }
}
