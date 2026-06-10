package com.minelaunched.stats;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;

import java.io.IOException;

public class MinelaunchedStatsPlugin extends JavaPlugin {

    private WebServer webServer;
    private TpsTracker tpsTracker;
    private RedisManager redisManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Dynamically inject missing keys from the internal config.yml into the user's file
        getConfig().options().copyDefaults(true);
        saveConfig();

        tpsTracker = new TpsTracker();
        tpsTracker.runTaskTimer(this, 0L, 1L);

        // Initialize Plugin Hooks safely
        com.minelaunched.stats.hooks.VaultHook.init();
        com.minelaunched.stats.hooks.PapiHook.init();
        com.minelaunched.stats.hooks.LuckPermsHook.init();
        com.minelaunched.stats.hooks.EssentialsHook.init();
        com.minelaunched.stats.hooks.ViaVersionHook.init();
        com.minelaunched.stats.hooks.FloodgateHook.init();
        com.minelaunched.stats.hooks.McMMOHook.init();
        com.minelaunched.stats.hooks.PlayerPointsHook.init();
        com.minelaunched.stats.hooks.GriefPreventionHook.init();
        com.minelaunched.stats.hooks.AuraSkillsHook.init();
        com.minelaunched.stats.hooks.TownyHook.init();
        com.minelaunched.stats.hooks.JobsRebornHook.init();
        com.minelaunched.stats.hooks.CMIHook.init();
        com.minelaunched.stats.hooks.DiscordSRVHook.init();
        com.minelaunched.stats.hooks.VulcanHook.init();
        com.minelaunched.stats.hooks.SuperVanishHook.init();
        com.minelaunched.stats.hooks.LandsHook.init();
        com.minelaunched.stats.hooks.FactionsHook.init();

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

    private void migrateConfig() {
        if (getConfig().isBoolean("hooks.vault")) {
            Bukkit.getLogger().info("[MinelaunchedStats] Migrating old config format to new granular format...");
            
            boolean oldVault = getConfig().getBoolean("hooks.vault", true);
            getConfig().set("hooks.vault", null);
            getConfig().set("hooks.vault.enabled", oldVault);
            getConfig().set("hooks.vault.export_player_balance", true);
            getConfig().set("hooks.vault.export_server_economy", true);

            boolean oldLuckPerms = getConfig().getBoolean("hooks.luckperms", true);
            getConfig().set("hooks.luckperms", null);
            getConfig().set("hooks.luckperms.enabled", oldLuckPerms);
            getConfig().set("hooks.luckperms.export_primary_group", true);
            getConfig().set("hooks.luckperms.export_prefix", true);
            getConfig().set("hooks.luckperms.export_suffix", true);

            boolean oldEssentials = getConfig().getBoolean("hooks.essentials", true);
            getConfig().set("hooks.essentials", null);
            getConfig().set("hooks.essentials.enabled", oldEssentials);
            getConfig().set("hooks.essentials.export_afk", true);
            getConfig().set("hooks.essentials.export_vanish", true);
            getConfig().set("hooks.essentials.export_god_mode", true);
            getConfig().set("hooks.essentials.export_muted", true);
            getConfig().set("hooks.essentials.export_nickname", true);

            boolean oldViaVersion = getConfig().getBoolean("hooks.viaversion", true);
            getConfig().set("hooks.viaversion", null);
            getConfig().set("hooks.viaversion.enabled", oldViaVersion);
            getConfig().set("hooks.viaversion.export_protocol_version", true);

            boolean oldFloodgate = getConfig().getBoolean("hooks.floodgate", true);
            getConfig().set("hooks.floodgate", null);
            getConfig().set("hooks.floodgate.enabled", oldFloodgate);
            getConfig().set("hooks.floodgate.export_is_bedrock", true);
            getConfig().set("hooks.floodgate.export_device_os", true);

            boolean oldMcmmo = getConfig().getBoolean("hooks.mcmmo", true);
            getConfig().set("hooks.mcmmo", null);
            getConfig().set("hooks.mcmmo.enabled", oldMcmmo);
            getConfig().set("hooks.mcmmo.export_power_level", true);

            boolean oldPlayerPoints = getConfig().getBoolean("hooks.playerpoints", true);
            getConfig().set("hooks.playerpoints", null);
            getConfig().set("hooks.playerpoints.enabled", oldPlayerPoints);
            getConfig().set("hooks.playerpoints.export_balance", true);

            boolean oldGriefPrevention = getConfig().getBoolean("hooks.griefprevention", true);
            getConfig().set("hooks.griefprevention", null);
            getConfig().set("hooks.griefprevention.enabled", oldGriefPrevention);
            getConfig().set("hooks.griefprevention.export_claim_blocks", true);
            getConfig().set("hooks.griefprevention.export_bonus_blocks", true);

            boolean oldAuraSkills = getConfig().getBoolean("hooks.auraskills", true);
            getConfig().set("hooks.auraskills", null);
            getConfig().set("hooks.auraskills.enabled", oldAuraSkills);
            getConfig().set("hooks.auraskills.export_mana", true);
            getConfig().set("hooks.auraskills.export_power_level", true);
            
            saveConfig();
            Bukkit.getLogger().info("[MinelaunchedStats] Config migration complete!");
        }
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
        migrateConfig();
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
