package com.minelaunched.stats.hooks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class HookManager {

    private static final List<MinelaunchedHook> registeredHooks = new ArrayList<>();

    public static void initAll() {
        registeredHooks.clear();
        
        registeredHooks.add(new GriefDefenderHook());
        registeredHooks.add(new NoCheatPlusHook());
        registeredHooks.add(new Pl3xMapHook());
        registeredHooks.add(new HuskyCratesHook());
        registeredHooks.add(new AuthMeHook());
        registeredHooks.add(new PlayerPointsHook());
        registeredHooks.add(new PreciousStonesHook());
        registeredHooks.add(new SkinsRestorerHook());
        registeredHooks.add(new ThemisHook());
        registeredHooks.add(new BeastTokensHook());
        registeredHooks.add(new LuckPermsHook());
        registeredHooks.add(new CrazyCratesHook());
        registeredHooks.add(new GrimACHook());
        registeredHooks.add(new EcoEnchantsHook());
        registeredHooks.add(new VotifierHook());
        registeredHooks.add(new ChestShopHook());
        registeredHooks.add(new TownyHook());
        registeredHooks.add(new IntaveHook());
        registeredHooks.add(new QuickShopHook());
        registeredHooks.add(new HolographicDisplaysHook());
        registeredHooks.add(new PvPManagerHook());
        registeredHooks.add(new MyPetHook());
        registeredHooks.add(new CrazyAuctionsHook());
        registeredHooks.add(new SkillAPIHook());
        registeredHooks.add(new CMIHook());
        registeredHooks.add(new BuycraftXHook());
        registeredHooks.add(new FactionsHook());
        registeredHooks.add(new AlonsoTagsHook());
        registeredHooks.add(new AACHook());
        registeredHooks.add(new GriefPreventionHook());
        registeredHooks.add(new SuperiorSkyblockHook());
        registeredHooks.add(new SimpleClansHook());
        registeredHooks.add(new MarriageMasterHook());
        registeredHooks.add(new PolarHook());
        registeredHooks.add(new MatrixHook());
        registeredHooks.add(new EconomyShopGUIHook());
        registeredHooks.add(new PapiHook());
        registeredHooks.add(new TradeSystemHook());
        registeredHooks.add(new Bedwars1058Hook());
        registeredHooks.add(new ViaVersionHook());
        registeredHooks.add(new WorldGuardHook());
        registeredHooks.add(new IridiumSkyblockHook());
        registeredHooks.add(new GemsEconomyHook());
        registeredHooks.add(new FactionsXHook());
        registeredHooks.add(new CrazyEnchantmentsHook());
        registeredHooks.add(new DeluxeTagsHook());
        registeredHooks.add(new ExcellentEnchantsHook());
        registeredHooks.add(new AureliumSkillsHook());
        registeredHooks.add(new MaxBansHook());
        registeredHooks.add(new PlanHook());
        registeredHooks.add(new BanManagerHook());
        registeredHooks.add(new VotingPluginHook());
        registeredHooks.add(new ResidenceHook());
        registeredHooks.add(new TABHook());
        registeredHooks.add(new MagicHook());
        registeredHooks.add(new VampireHook());
        registeredHooks.add(new VaultHook());
        registeredHooks.add(new SlimefunHook());
        registeredHooks.add(new SaberFactionsHook());
        registeredHooks.add(new GeyserHook());
        registeredHooks.add(new HeroesHook());
        registeredHooks.add(new ShopGUIPlusHook());
        registeredHooks.add(new DecentHologramsHook());
        registeredHooks.add(new DiscordSRVHook());
        registeredHooks.add(new SpartanHook());
        registeredHooks.add(new CombatLogXHook());
        registeredHooks.add(new ExcellentCratesHook());
        registeredHooks.add(new AdvancedEnchantmentsHook());
        registeredHooks.add(new AdvancedBanHook());
        registeredHooks.add(new RedProtectHook());
        registeredHooks.add(new LibertyBansHook());
        registeredHooks.add(new ProtectionStonesHook());
        registeredHooks.add(new TebexHook());
        registeredHooks.add(new VulcanHook());
        registeredHooks.add(new ClearLagHook());
        registeredHooks.add(new AuctionHouseHook());
        registeredHooks.add(new NegativityHook());
        registeredHooks.add(new ProSkillAPIHook());
        registeredHooks.add(new DynmapHook());
        registeredHooks.add(new TokenManagerHook());
        registeredHooks.add(new MMOCoreHook());
        registeredHooks.add(new CitizensHook());
        registeredHooks.add(new ASkyBlockHook());
        registeredHooks.add(new SuperVanishHook());
        registeredHooks.add(new JobsRebornHook());
        registeredHooks.add(new SquaremapHook());
        registeredHooks.add(new BlueMapHook());
        registeredHooks.add(new BentoBoxHook());
        registeredHooks.add(new LandsHook());
        registeredHooks.add(new BountyHuntersHook());
        registeredHooks.add(new MassiveCoreHook());
        registeredHooks.add(new ProjectKorraHook());
        registeredHooks.add(new KauriHook());
        registeredHooks.add(new CraftingStoreHook());
        registeredHooks.add(new AuraSkillsHook());
        registeredHooks.add(new LiteBansHook());
        registeredHooks.add(new QuestsHook());
        registeredHooks.add(new EssentialsHook());
        registeredHooks.add(new McMMOHook());
        registeredHooks.add(new FloodgateHook());

        List<String> activeHooks = new ArrayList<>();
        int missingHooks = 0;

        for (MinelaunchedHook hook : registeredHooks) {
            try {
                hook.init();
                if (hook.isEnabled()) {
                    activeHooks.add(hook.getPluginName());
                } else {
                    missingHooks++;
                }
            } catch (Exception e) {
                // Silently ignore reflection errors
                missingHooks++;
            }
        }
        
        // Detailed Logging
        Bukkit.getLogger().info("========================================");
        Bukkit.getLogger().info("[MinelaunchedStats] Hook System Initialized");
        Bukkit.getLogger().info("[MinelaunchedStats] " + activeHooks.size() + " integrations active.");
        Bukkit.getLogger().info("[MinelaunchedStats] " + missingHooks + " integrations skipped (plugins not found).");
        if (!activeHooks.isEmpty()) {
            Bukkit.getLogger().info("[MinelaunchedStats] Active: " + String.join(", ", activeHooks));
        }
        Bukkit.getLogger().info("========================================");
    }

    public static void registerAllDefaults(FileConfiguration config) {
        for (MinelaunchedHook hook : registeredHooks) {
            if (hook.isEnabled()) {
                // If the plugin is installed, generate the config dynamically
                hook.registerDefaults(config);
            } else {
                // DYNAMIC CONFIG CLEANUP: If the plugin was uninstalled, delete its config section
                if (config.contains("hooks." + hook.getPluginName())) {
                    config.set("hooks." + hook.getPluginName(), null);
                }
            }
        }
    }

    public static List<MinelaunchedHook> getActiveHooks() {
        List<MinelaunchedHook> active = new ArrayList<>();
        for (MinelaunchedHook hook : registeredHooks) {
            if (hook.isEnabled()) {
                active.add(hook);
            }
        }
        return active;
    }

    public static void appendPlayerHooks(com.google.gson.JsonObject po, org.bukkit.entity.Player p, FileConfiguration config) {
        for (MinelaunchedHook hook : getActiveHooks()) {
            try {
                hook.appendPlayerStats(po, p, config);
            } catch (Exception e) {}
        }
    }

    public static void appendServerHooks(com.google.gson.JsonObject po, FileConfiguration config) {
        for (MinelaunchedHook hook : getActiveHooks()) {
            try {
                hook.appendServerStats(po, config);
            } catch (Exception e) {}
        }
    }
}
