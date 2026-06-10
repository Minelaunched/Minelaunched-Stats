package com.minelaunched.stats.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;
import java.util.Arrays;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;

public class VaultHook extends MinelaunchedHook {
    private static Economy econ = null;
    private static boolean initialized = false;

    @Override
    public void init() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return;
        try {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
                initialized = true;
                Bukkit.getLogger().info("[MinelaunchedStats] Hooks: Vault Economy successfully linked!");
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[MinelaunchedStats] Hooks: Failed to link Vault Economy.");
        }
    }

    @Override
    public boolean isEnabled() {
        return initialized;
    }

    public static double getBalance(OfflinePlayer player) {
        if (!initialized || econ == null) return 0.0;
        return econ.getBalance(player);
    }

    @Override
    public String getPluginName() {
        return "vault";
    }

    @Override
    public List<String> getExportKeys() {
        return Arrays.asList("server_economy", "player_balance");
    }

    @Override
    public void appendPlayerStats(JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        po.addProperty("vault_balance", getBalance(p));
    }

    @Override
    public void appendServerStats(JsonObject server, FileConfiguration config) {
        double totalEconomy = 0;
                for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                    totalEconomy += getBalance(p);
                }
                server.addProperty("vault_total_economy", totalEconomy);
    }

}
