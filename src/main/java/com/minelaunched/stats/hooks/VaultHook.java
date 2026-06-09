package com.minelaunched.stats.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {
    private static Economy econ = null;
    private static boolean initialized = false;

    public static void init() {
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

    public static boolean isEnabled() {
        return initialized;
    }

    public static double getBalance(OfflinePlayer player) {
        if (!initialized || econ == null) return 0.0;
        return econ.getBalance(player);
    }
}
