package com.minelaunched.stats.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    public static void migrateConfig(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        if (config.isBoolean("hooks.vault")) {
            Bukkit.getLogger().info("[MinelaunchedStats] Migrating old config format to new granular format...");
            
            boolean oldVault = config.getBoolean("hooks.vault", true);
            config.set("hooks.vault", null);
            config.set("hooks.vault.enabled", oldVault);
            config.set("hooks.vault.export_player_balance", true);
            config.set("hooks.vault.export_server_economy", true);

            boolean oldLuckPerms = config.getBoolean("hooks.luckperms", true);
            config.set("hooks.luckperms", null);
            config.set("hooks.luckperms.enabled", oldLuckPerms);
            config.set("hooks.luckperms.export_primary_group", true);
            config.set("hooks.luckperms.export_prefix", true);
            config.set("hooks.luckperms.export_suffix", true);

            boolean oldEssentials = config.getBoolean("hooks.essentials", true);
            config.set("hooks.essentials", null);
            config.set("hooks.essentials.enabled", oldEssentials);
            config.set("hooks.essentials.export_afk", true);
            config.set("hooks.essentials.export_vanish", true);
            config.set("hooks.essentials.export_god_mode", true);
            config.set("hooks.essentials.export_muted", true);
            config.set("hooks.essentials.export_nickname", true);

            boolean oldViaVersion = config.getBoolean("hooks.viaversion", true);
            config.set("hooks.viaversion", null);
            config.set("hooks.viaversion.enabled", oldViaVersion);
            config.set("hooks.viaversion.export_protocol_version", true);

            boolean oldFloodgate = config.getBoolean("hooks.floodgate", true);
            config.set("hooks.floodgate", null);
            config.set("hooks.floodgate.enabled", oldFloodgate);
            config.set("hooks.floodgate.export_is_bedrock", true);
            config.set("hooks.floodgate.export_device_os", true);

            boolean oldMcmmo = config.getBoolean("hooks.mcmmo", true);
            config.set("hooks.mcmmo", null);
            config.set("hooks.mcmmo.enabled", oldMcmmo);
            config.set("hooks.mcmmo.export_power_level", true);

            boolean oldPlayerPoints = config.getBoolean("hooks.playerpoints", true);
            config.set("hooks.playerpoints", null);
            config.set("hooks.playerpoints.enabled", oldPlayerPoints);
            config.set("hooks.playerpoints.export_balance", true);

            boolean oldGriefPrevention = config.getBoolean("hooks.griefprevention", true);
            config.set("hooks.griefprevention", null);
            config.set("hooks.griefprevention.enabled", oldGriefPrevention);
            config.set("hooks.griefprevention.export_claim_blocks", true);
            config.set("hooks.griefprevention.export_bonus_blocks", true);

            boolean oldAuraSkills = config.getBoolean("hooks.auraskills", true);
            config.set("hooks.auraskills", null);
            config.set("hooks.auraskills.enabled", oldAuraSkills);
            config.set("hooks.auraskills.export_mana", true);
            config.set("hooks.auraskills.export_power_level", true);
            
            plugin.saveConfig();
            Bukkit.getLogger().info("[MinelaunchedStats] Config migration complete!");
        }
    }
}
