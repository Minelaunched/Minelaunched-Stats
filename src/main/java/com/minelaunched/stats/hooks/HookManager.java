package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HookManager {

    private static final List<MinelaunchedHook> registeredHooks = new ArrayList<>();

    public static void register(MinelaunchedHook hook) {
        registeredHooks.add(hook);
    }

    public static void initAll() {
        register(new VaultHook());
        register(new PapiHook());
        register(new LuckPermsHook());
        register(new EssentialsHook());
        register(new ViaVersionHook());
        register(new FloodgateHook());
        register(new McMMOHook());
        register(new PlayerPointsHook());
        register(new GriefPreventionHook());
        register(new AuraSkillsHook());
        register(new TownyHook());
        register(new JobsRebornHook());
        register(new CMIHook());
        register(new DiscordSRVHook());
        register(new VulcanHook());
        register(new SuperVanishHook());
        register(new LandsHook());
        register(new FactionsHook());
        register(new CombatLogXHook());
        register(new SuperiorSkyblockHook());
        register(new DeluxeTagsHook());
        register(new SkinsRestorerHook());
        register(new CitizensHook());
        register(new TokenManagerHook());
        register(new PvPManagerHook());
        register(new SimpleClansHook());
        register(new VotingPluginHook());
        register(new BountyHuntersHook());

        register(new BentoBoxHook());
        register(new IridiumSkyblockHook());
        register(new GriefDefenderHook());
        register(new SpartanHook());
        register(new GrimACHook());
        register(new MatrixHook());
        register(new GemsEconomyHook());
        register(new BeastTokensHook());
        register(new AlonsoTagsHook());
        register(new MarriageMasterHook());
        register(new MyPetHook());
        register(new SlimefunHook());
        register(new QuestsHook());
        register(new VampireHook());
        register(new AureliumSkillsHook());
        register(new CrazyCratesHook());
        register(new ExcellentCratesHook());
        register(new GeyserHook());
        register(new LiteBansHook());
        register(new AdvancedBanHook());

        for (MinelaunchedHook hook : registeredHooks) {
            hook.init();
        }
    }

    /**
     * Called by ConfigManager to generate the defaults dynamically!
     */
    public static void registerAllDefaults(FileConfiguration config) {
        for (MinelaunchedHook hook : registeredHooks) {
            hook.registerDefaults(config);
        }
    }

    public static void appendServerHooks(JsonObject server, FileConfiguration config) {
        for (MinelaunchedHook hook : registeredHooks) {
            if (hook.isEnabled() && config.getBoolean("hooks." + hook.getPluginName() + ".enabled", true)) {
                // Technically we should check export keys, but since appendServerStats handles the specific logic
                hook.appendServerStats(server, config);
            }
        }
    }

    public static void appendPlayerHooks(JsonObject po, Player p, FileConfiguration config) {
        for (MinelaunchedHook hook : registeredHooks) {
            if (hook.isEnabled() && config.getBoolean("hooks." + hook.getPluginName() + ".enabled", true)) {
                hook.appendPlayerStats(po, p, config);
            }
        }
    }
}
