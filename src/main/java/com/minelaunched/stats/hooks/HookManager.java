package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class HookManager {

    public static void initAll() {
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
        com.minelaunched.stats.hooks.CombatLogXHook.init();
        com.minelaunched.stats.hooks.SuperiorSkyblockHook.init();
        com.minelaunched.stats.hooks.DeluxeTagsHook.init();
        com.minelaunched.stats.hooks.SkinsRestorerHook.init();
        com.minelaunched.stats.hooks.CitizensHook.init();
        com.minelaunched.stats.hooks.TokenManagerHook.init();
        com.minelaunched.stats.hooks.PvPManagerHook.init();
        com.minelaunched.stats.hooks.SimpleClansHook.init();
        com.minelaunched.stats.hooks.VotingPluginHook.init();
        com.minelaunched.stats.hooks.BountyHuntersHook.init();

        com.minelaunched.stats.hooks.BentoBoxHook.init();
        com.minelaunched.stats.hooks.IridiumSkyblockHook.init();
        com.minelaunched.stats.hooks.GriefDefenderHook.init();
        com.minelaunched.stats.hooks.SpartanHook.init();
        com.minelaunched.stats.hooks.GrimACHook.init();
        com.minelaunched.stats.hooks.MatrixHook.init();
        com.minelaunched.stats.hooks.GemsEconomyHook.init();
        com.minelaunched.stats.hooks.BeastTokensHook.init();
        com.minelaunched.stats.hooks.AlonsoTagsHook.init();
        com.minelaunched.stats.hooks.MarriageMasterHook.init();
        com.minelaunched.stats.hooks.MyPetHook.init();
        com.minelaunched.stats.hooks.SlimefunHook.init();
        com.minelaunched.stats.hooks.QuestsHook.init();
        com.minelaunched.stats.hooks.VampireHook.init();
        com.minelaunched.stats.hooks.AureliumSkillsHook.init();
        com.minelaunched.stats.hooks.CrazyCratesHook.init();
        com.minelaunched.stats.hooks.ExcellentCratesHook.init();
        com.minelaunched.stats.hooks.GeyserHook.init();

    }

    public static void appendServerHooks(JsonObject server, FileConfiguration config) {
            if (config.getBoolean("hooks.vault.enabled", true) && config.getBoolean("hooks.vault.export_server_economy", true) && com.minelaunched.stats.hooks.VaultHook.isEnabled()) {
                double totalEconomy = 0;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    totalEconomy += com.minelaunched.stats.hooks.VaultHook.getBalance(p);
                }
                server.addProperty("vault_total_economy", totalEconomy);
            }

            if (config.getBoolean("hooks.placeholderapi.enabled", true) && com.minelaunched.stats.hooks.PapiHook.isEnabled()) {
                JsonObject papi = new JsonObject();
                for (String ph : config.getStringList("hooks.placeholderapi.server_placeholders")) {
                    papi.addProperty(ph, com.minelaunched.stats.hooks.PapiHook.setPlaceholders(null, ph));
                }
                server.add("placeholders", papi);
            }

    }

    public static void appendPlayerHooks(JsonObject po, Player p, FileConfiguration config) {
                if (config.getBoolean("hooks.vault.enabled", true) && config.getBoolean("hooks.vault.export_player_balance", true) && com.minelaunched.stats.hooks.VaultHook.isEnabled()) {
                    po.addProperty("vault_balance", com.minelaunched.stats.hooks.VaultHook.getBalance(p));
                }

                if (config.getBoolean("hooks.placeholderapi.enabled", true) && com.minelaunched.stats.hooks.PapiHook.isEnabled()) {
                    JsonObject papi = new JsonObject();
                    for (String ph : config.getStringList("hooks.placeholderapi.player_placeholders")) {
                        papi.addProperty(ph, com.minelaunched.stats.hooks.PapiHook.setPlaceholders(p, ph));
                    }
                    po.add("placeholders", papi);
                }

                if (config.getBoolean("hooks.luckperms.enabled", true) && com.minelaunched.stats.hooks.LuckPermsHook.isEnabled()) {
                    JsonObject lpData = com.minelaunched.stats.hooks.LuckPermsHook.getPlayerData(p);
                    if (lpData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.luckperms.export_primary_group", true) && lpData.has("primary_group")) filtered.add("primary_group", lpData.get("primary_group"));
                        if (config.getBoolean("hooks.luckperms.export_prefix", true) && lpData.has("prefix")) filtered.add("prefix", lpData.get("prefix"));
                        if (config.getBoolean("hooks.luckperms.export_suffix", true) && lpData.has("suffix")) filtered.add("suffix", lpData.get("suffix"));
                        if (filtered.size() > 0) po.add("luckperms", filtered);
                    }
                }

                if (config.getBoolean("hooks.essentials.enabled", true) && com.minelaunched.stats.hooks.EssentialsHook.isEnabled()) {
                    JsonObject essData = com.minelaunched.stats.hooks.EssentialsHook.getPlayerData(p);
                    if (essData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.essentials.export_afk", true) && essData.has("is_afk")) filtered.add("is_afk", essData.get("is_afk"));
                        if (config.getBoolean("hooks.essentials.export_vanish", true) && essData.has("is_vanished")) filtered.add("is_vanished", essData.get("is_vanished"));
                        if (config.getBoolean("hooks.essentials.export_god_mode", true) && essData.has("is_god_mode")) filtered.add("is_god_mode", essData.get("is_god_mode"));
                        if (config.getBoolean("hooks.essentials.export_muted", true) && essData.has("is_muted")) filtered.add("is_muted", essData.get("is_muted"));
                        if (config.getBoolean("hooks.essentials.export_nickname", true) && essData.has("nickname")) filtered.add("nickname", essData.get("nickname"));
                        if (filtered.size() > 0) po.add("essentials", filtered);
                    }
                }

                if (config.getBoolean("hooks.cmi.enabled", true) && com.minelaunched.stats.hooks.CMIHook.isEnabled()) {
                    JsonObject cmiData = com.minelaunched.stats.hooks.CMIHook.getPlayerData(p);
                    if (cmiData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.cmi.export_afk", true) && cmiData.has("is_afk")) filtered.add("is_afk", cmiData.get("is_afk"));
                        if (config.getBoolean("hooks.cmi.export_vanish", true) && cmiData.has("is_vanished")) filtered.add("is_vanished", cmiData.get("is_vanished"));
                        if (config.getBoolean("hooks.cmi.export_god_mode", true) && cmiData.has("is_god_mode")) filtered.add("is_god_mode", cmiData.get("is_god_mode"));
                        if (filtered.size() > 0) po.add("cmi", filtered);
                    }
                }

                if (config.getBoolean("hooks.viaversion.enabled", true) && config.getBoolean("hooks.viaversion.export_protocol_version", true) && com.minelaunched.stats.hooks.ViaVersionHook.isEnabled()) {
                    JsonObject viaData = com.minelaunched.stats.hooks.ViaVersionHook.getPlayerData(p);
                    if (viaData != null) po.add("viaversion", viaData);
                }

                if (config.getBoolean("hooks.floodgate.enabled", true) && com.minelaunched.stats.hooks.FloodgateHook.isEnabled()) {
                    JsonObject floodgateData = com.minelaunched.stats.hooks.FloodgateHook.getPlayerData(p);
                    if (floodgateData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.floodgate.export_is_bedrock", true) && floodgateData.has("is_bedrock")) filtered.add("is_bedrock", floodgateData.get("is_bedrock"));
                        if (config.getBoolean("hooks.floodgate.export_device_os", true) && floodgateData.has("device_os")) filtered.add("device_os", floodgateData.get("device_os"));
                        if (floodgateData.has("language_code")) filtered.add("language_code", floodgateData.get("language_code"));
                        if (filtered.size() > 0) po.add("floodgate", filtered);
                    }
                }

                if (config.getBoolean("hooks.mcmmo.enabled", true) && config.getBoolean("hooks.mcmmo.export_power_level", true) && com.minelaunched.stats.hooks.McMMOHook.isEnabled()) {
                    JsonObject mcmmoData = com.minelaunched.stats.hooks.McMMOHook.getPlayerData(p);
                    if (mcmmoData != null) po.add("mcmmo", mcmmoData);
                }

                if (config.getBoolean("hooks.playerpoints.enabled", true) && config.getBoolean("hooks.playerpoints.export_balance", true) && com.minelaunched.stats.hooks.PlayerPointsHook.isEnabled()) {
                    JsonObject ppData = com.minelaunched.stats.hooks.PlayerPointsHook.getPlayerData(p);
                    if (ppData != null) po.add("playerpoints", ppData);
                }

                if (config.getBoolean("hooks.griefprevention.enabled", true) && com.minelaunched.stats.hooks.GriefPreventionHook.isEnabled()) {
                    JsonObject gpData = com.minelaunched.stats.hooks.GriefPreventionHook.getPlayerData(p);
                    if (gpData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.griefprevention.export_claim_blocks", true) && gpData.has("claim_blocks")) filtered.add("claim_blocks", gpData.get("claim_blocks"));
                        if (config.getBoolean("hooks.griefprevention.export_bonus_blocks", true) && gpData.has("bonus_blocks")) filtered.add("bonus_blocks", gpData.get("bonus_blocks"));
                        if (gpData.has("total_blocks")) filtered.add("total_blocks", gpData.get("total_blocks"));
                        if (filtered.size() > 0) po.add("griefprevention", filtered);
                    }
                }

                if (config.getBoolean("hooks.auraskills.enabled", true) && com.minelaunched.stats.hooks.AuraSkillsHook.isEnabled()) {
                    JsonObject auraData = com.minelaunched.stats.hooks.AuraSkillsHook.getPlayerData(p);
                    if (auraData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.auraskills.export_mana", true) && auraData.has("mana")) filtered.add("mana", auraData.get("mana"));
                        if (config.getBoolean("hooks.auraskills.export_power_level", true) && auraData.has("power_level")) filtered.add("power_level", auraData.get("power_level"));
                        if (filtered.size() > 0) po.add("auraskills", filtered);
                    }
                }
                
                if (config.getBoolean("hooks.towny.enabled", true) && com.minelaunched.stats.hooks.TownyHook.isEnabled()) {
                    JsonObject townyData = com.minelaunched.stats.hooks.TownyHook.getPlayerData(p);
                    if (townyData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.towny.export_town", true) && townyData.has("town")) filtered.add("town", townyData.get("town"));
                        if (config.getBoolean("hooks.towny.export_nation", true) && townyData.has("nation")) filtered.add("nation", townyData.get("nation"));
                        if (config.getBoolean("hooks.towny.export_title", true) && townyData.has("title")) filtered.add("title", townyData.get("title"));
                        if (filtered.size() > 0) po.add("towny", filtered);
                    }
                }
                
                if (config.getBoolean("hooks.jobsreborn.enabled", true) && config.getBoolean("hooks.jobsreborn.export_jobs_list", true) && com.minelaunched.stats.hooks.JobsRebornHook.isEnabled()) {
                    JsonObject jobsData = com.minelaunched.stats.hooks.JobsRebornHook.getPlayerData(p);
                    if (jobsData != null) po.add("jobsreborn", jobsData);
                }

                if (config.getBoolean("hooks.discordsrv.enabled", true) && config.getBoolean("hooks.discordsrv.export_discord_id", true) && com.minelaunched.stats.hooks.DiscordSRVHook.isEnabled()) {
                    String discordId = com.minelaunched.stats.hooks.DiscordSRVHook.getDiscordId(p);
                    if (discordId != null) po.addProperty("discord_id", discordId);
                }

                if (config.getBoolean("hooks.vulcan.enabled", true) && config.getBoolean("hooks.vulcan.export_violations", true) && com.minelaunched.stats.hooks.VulcanHook.isEnabled()) {
                    po.addProperty("vulcan_violations", com.minelaunched.stats.hooks.VulcanHook.getViolations(p));
                }

                if (config.getBoolean("hooks.supervanish.enabled", true) && config.getBoolean("hooks.supervanish.export_is_vanished", true) && com.minelaunched.stats.hooks.SuperVanishHook.isEnabled()) {
                    po.addProperty("is_supervanished", com.minelaunched.stats.hooks.SuperVanishHook.isVanished(p));
                }

                if (config.getBoolean("hooks.lands.enabled", true) && config.getBoolean("hooks.lands.export_lands_count", true) && com.minelaunched.stats.hooks.LandsHook.isEnabled()) {
                    po.addProperty("lands_count", com.minelaunched.stats.hooks.LandsHook.getLandsCount(p));
                }

                if (config.getBoolean("hooks.factionsuuid.enabled", true) && com.minelaunched.stats.hooks.FactionsHook.isEnabled()) {
                    JsonObject factionsData = new JsonObject();
                    if (config.getBoolean("hooks.factionsuuid.export_faction_name", true)) {
                        String fName = com.minelaunched.stats.hooks.FactionsHook.getFactionName(p);
                        if (fName != null) factionsData.addProperty("name", fName);
                    }
                    if (config.getBoolean("hooks.factionsuuid.export_faction_role", true)) {
                        String fRole = com.minelaunched.stats.hooks.FactionsHook.getFactionRole(p);
                        if (fRole != null) factionsData.addProperty("role", fRole);
                    }
                    if (factionsData.size() > 0) po.add("factions", factionsData);
                }

                if (config.getBoolean("hooks.combatlogx.enabled", true) && config.getBoolean("hooks.combatlogx.export_is_in_combat", true) && com.minelaunched.stats.hooks.CombatLogXHook.isEnabled()) {
                    po.addProperty("is_in_combat", com.minelaunched.stats.hooks.CombatLogXHook.isInCombat(p));
                }

                if (config.getBoolean("hooks.superiorskyblock2.enabled", true) && com.minelaunched.stats.hooks.SuperiorSkyblockHook.isEnabled()) {
                    JsonObject ssData = com.minelaunched.stats.hooks.SuperiorSkyblockHook.getPlayerData(p);
                    if (ssData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.superiorskyblock2.export_island_level", true) && ssData.has("island_level")) filtered.add("island_level", ssData.get("island_level"));
                        if (config.getBoolean("hooks.superiorskyblock2.export_island_name", true) && ssData.has("island_name")) filtered.add("island_name", ssData.get("island_name"));
                        if (filtered.size() > 0) po.add("superiorskyblock2", filtered);
                    }
                }

                if (config.getBoolean("hooks.deluxetags.enabled", true) && config.getBoolean("hooks.deluxetags.export_active_tag", true) && com.minelaunched.stats.hooks.DeluxeTagsHook.isEnabled()) {
                    String activeTag = com.minelaunched.stats.hooks.DeluxeTagsHook.getActiveTag(p);
                    if (activeTag != null) po.addProperty("active_tag", activeTag);
                }

                if (config.getBoolean("hooks.skinsrestorer.enabled", true) && config.getBoolean("hooks.skinsrestorer.export_skin_name", true) && com.minelaunched.stats.hooks.SkinsRestorerHook.isEnabled()) {
                    String skinName = com.minelaunched.stats.hooks.SkinsRestorerHook.getSkinName(p);
                    if (skinName != null) po.addProperty("skin_name", skinName);
                }

                if (config.getBoolean("hooks.citizens.enabled", true) && config.getBoolean("hooks.citizens.export_is_npc", true) && com.minelaunched.stats.hooks.CitizensHook.isEnabled()) {
                    po.addProperty("is_npc", com.minelaunched.stats.hooks.CitizensHook.isNPC(p));
                }

                if (config.getBoolean("hooks.tokenmanager.enabled", true) && config.getBoolean("hooks.tokenmanager.export_tokens", true) && com.minelaunched.stats.hooks.TokenManagerHook.isEnabled()) {
                    po.addProperty("tokens", com.minelaunched.stats.hooks.TokenManagerHook.getTokens(p));
                }

                if (config.getBoolean("hooks.pvpmanager.enabled", true) && com.minelaunched.stats.hooks.PvPManagerHook.isEnabled()) {
                    if (config.getBoolean("hooks.pvpmanager.export_is_in_combat", true)) po.addProperty("is_in_pvpmanager_combat", com.minelaunched.stats.hooks.PvPManagerHook.isInCombat(p));
                    if (config.getBoolean("hooks.pvpmanager.export_has_pvp_enabled", true)) po.addProperty("has_pvp_enabled", com.minelaunched.stats.hooks.PvPManagerHook.hasPvPEnabled(p));
                }

                if (config.getBoolean("hooks.simpleclans.enabled", true) && com.minelaunched.stats.hooks.SimpleClansHook.isEnabled()) {
                    JsonObject clanData = com.minelaunched.stats.hooks.SimpleClansHook.getClanData(p);
                    if (clanData != null) {
                        JsonObject filtered = new JsonObject();
                        if (config.getBoolean("hooks.simpleclans.export_clan_name", true) && clanData.has("name")) filtered.add("clan_name", clanData.get("name"));
                        if (config.getBoolean("hooks.simpleclans.export_clan_tag", true) && clanData.has("tag")) filtered.add("clan_tag", clanData.get("tag"));
                        if (filtered.size() > 0) po.add("simpleclans", filtered);
                    }
                }

                if (config.getBoolean("hooks.votingplugin.enabled", true) && config.getBoolean("hooks.votingplugin.export_total_votes", true) && com.minelaunched.stats.hooks.VotingPluginHook.isEnabled()) {
                    po.addProperty("total_votes", com.minelaunched.stats.hooks.VotingPluginHook.getTotalVotes(p));
                }

                if (config.getBoolean("hooks.bountyhunters.enabled", true) && config.getBoolean("hooks.bountyhunters.export_bounty_reward", true) && com.minelaunched.stats.hooks.BountyHuntersHook.isEnabled()) {
                    po.addProperty("bounty_reward", com.minelaunched.stats.hooks.BountyHuntersHook.getBountyReward(p));
                }

                if (config.getBoolean("hooks.bentobox.enabled", true) && config.getBoolean("hooks.bentobox.export_island_level", true) && com.minelaunched.stats.hooks.BentoBoxHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.BentoBoxHook.getStats(p);
                    if (val instanceof Number) po.addProperty("island_level", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("island_level", (Boolean) val);
                    else if (val instanceof String) po.addProperty("island_level", (String) val);
                }
                if (config.getBoolean("hooks.iridiumskyblock.enabled", true) && config.getBoolean("hooks.iridiumskyblock.export_island", true) && com.minelaunched.stats.hooks.IridiumSkyblockHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.IridiumSkyblockHook.getStats(p);
                    if (val instanceof Number) po.addProperty("has_island", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("has_island", (Boolean) val);
                    else if (val instanceof String) po.addProperty("has_island", (String) val);
                }
                if (config.getBoolean("hooks.griefdefender.enabled", true) && config.getBoolean("hooks.griefdefender.export_claims", true) && com.minelaunched.stats.hooks.GriefDefenderHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.GriefDefenderHook.getStats(p);
                    if (val instanceof Number) po.addProperty("claims_count", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("claims_count", (Boolean) val);
                    else if (val instanceof String) po.addProperty("claims_count", (String) val);
                }
                if (config.getBoolean("hooks.spartan.enabled", true) && config.getBoolean("hooks.spartan.export_violations", true) && com.minelaunched.stats.hooks.SpartanHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.SpartanHook.getStats(p);
                    if (val instanceof Number) po.addProperty("spartan_violations", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("spartan_violations", (Boolean) val);
                    else if (val instanceof String) po.addProperty("spartan_violations", (String) val);
                }
                if (config.getBoolean("hooks.grimac.enabled", true) && config.getBoolean("hooks.grimac.export_alerts", true) && com.minelaunched.stats.hooks.GrimACHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.GrimACHook.getStats(p);
                    if (val instanceof Number) po.addProperty("grim_alerts", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("grim_alerts", (Boolean) val);
                    else if (val instanceof String) po.addProperty("grim_alerts", (String) val);
                }
                if (config.getBoolean("hooks.matrix.enabled", true) && config.getBoolean("hooks.matrix.export_violations", true) && com.minelaunched.stats.hooks.MatrixHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.MatrixHook.getStats(p);
                    if (val instanceof Number) po.addProperty("matrix_violations", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("matrix_violations", (Boolean) val);
                    else if (val instanceof String) po.addProperty("matrix_violations", (String) val);
                }
                if (config.getBoolean("hooks.gemseconomy.enabled", true) && config.getBoolean("hooks.gemseconomy.export_balance", true) && com.minelaunched.stats.hooks.GemsEconomyHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.GemsEconomyHook.getStats(p);
                    if (val instanceof Number) po.addProperty("gems_balance", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("gems_balance", (Boolean) val);
                    else if (val instanceof String) po.addProperty("gems_balance", (String) val);
                }
                if (config.getBoolean("hooks.beasttokens.enabled", true) && config.getBoolean("hooks.beasttokens.export_tokens", true) && com.minelaunched.stats.hooks.BeastTokensHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.BeastTokensHook.getStats(p);
                    if (val instanceof Number) po.addProperty("beast_tokens", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("beast_tokens", (Boolean) val);
                    else if (val instanceof String) po.addProperty("beast_tokens", (String) val);
                }
                if (config.getBoolean("hooks.alonsotags.enabled", true) && config.getBoolean("hooks.alonsotags.export_tag", true) && com.minelaunched.stats.hooks.AlonsoTagsHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.AlonsoTagsHook.getStats(p);
                    if (val instanceof Number) po.addProperty("alonso_tag", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("alonso_tag", (Boolean) val);
                    else if (val instanceof String) po.addProperty("alonso_tag", (String) val);
                }
                if (config.getBoolean("hooks.marriagemaster.enabled", true) && config.getBoolean("hooks.marriagemaster.export_partner", true) && com.minelaunched.stats.hooks.MarriageMasterHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.MarriageMasterHook.getStats(p);
                    if (val instanceof Number) po.addProperty("partner_name", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("partner_name", (Boolean) val);
                    else if (val instanceof String) po.addProperty("partner_name", (String) val);
                }
                if (config.getBoolean("hooks.mypet.enabled", true) && config.getBoolean("hooks.mypet.export_pet_name", true) && com.minelaunched.stats.hooks.MyPetHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.MyPetHook.getStats(p);
                    if (val instanceof Number) po.addProperty("pet_name", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("pet_name", (Boolean) val);
                    else if (val instanceof String) po.addProperty("pet_name", (String) val);
                }
                if (config.getBoolean("hooks.slimefun.enabled", true) && config.getBoolean("hooks.slimefun.export_researches", true) && com.minelaunched.stats.hooks.SlimefunHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.SlimefunHook.getStats(p);
                    if (val instanceof Number) po.addProperty("slimefun_researches", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("slimefun_researches", (Boolean) val);
                    else if (val instanceof String) po.addProperty("slimefun_researches", (String) val);
                }
                if (config.getBoolean("hooks.quests.enabled", true) && config.getBoolean("hooks.quests.export_completed_quests", true) && com.minelaunched.stats.hooks.QuestsHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.QuestsHook.getStats(p);
                    if (val instanceof Number) po.addProperty("completed_quests", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("completed_quests", (Boolean) val);
                    else if (val instanceof String) po.addProperty("completed_quests", (String) val);
                }
                if (config.getBoolean("hooks.vampire.enabled", true) && config.getBoolean("hooks.vampire.export_is_vampire", true) && com.minelaunched.stats.hooks.VampireHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.VampireHook.getStats(p);
                    if (val instanceof Number) po.addProperty("is_vampire", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("is_vampire", (Boolean) val);
                    else if (val instanceof String) po.addProperty("is_vampire", (String) val);
                }
                if (config.getBoolean("hooks.aureliumskills.enabled", true) && config.getBoolean("hooks.aureliumskills.export_power_level", true) && com.minelaunched.stats.hooks.AureliumSkillsHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.AureliumSkillsHook.getStats(p);
                    if (val instanceof Number) po.addProperty("power_level", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("power_level", (Boolean) val);
                    else if (val instanceof String) po.addProperty("power_level", (String) val);
                }
                if (config.getBoolean("hooks.crazycrates.enabled", true) && config.getBoolean("hooks.crazycrates.export_keys", true) && com.minelaunched.stats.hooks.CrazyCratesHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.CrazyCratesHook.getStats(p);
                    if (val instanceof Number) po.addProperty("crazy_keys", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("crazy_keys", (Boolean) val);
                    else if (val instanceof String) po.addProperty("crazy_keys", (String) val);
                }
                if (config.getBoolean("hooks.excellentcrates.enabled", true) && config.getBoolean("hooks.excellentcrates.export_keys", true) && com.minelaunched.stats.hooks.ExcellentCratesHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.ExcellentCratesHook.getStats(p);
                    if (val instanceof Number) po.addProperty("excellent_keys", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("excellent_keys", (Boolean) val);
                    else if (val instanceof String) po.addProperty("excellent_keys", (String) val);
                }
                if (config.getBoolean("hooks.geyser.enabled", true) && config.getBoolean("hooks.geyser.export_is_bedrock", true) && com.minelaunched.stats.hooks.GeyserHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.GeyserHook.getStats(p);
                    if (val instanceof Number) po.addProperty("is_bedrock", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("is_bedrock", (Boolean) val);
                    else if (val instanceof String) po.addProperty("is_bedrock", (String) val);
                }
                if (config.getBoolean("hooks.litebans.enabled", true) && config.getBoolean("hooks.litebans.export_is_banned", true) && com.minelaunched.stats.hooks.LiteBansHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.LiteBansHook.getStats(p);
                    if (val instanceof Number) po.addProperty("is_banned", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("is_banned", (Boolean) val);
                    else if (val instanceof String) po.addProperty("is_banned", (String) val);
                }
                if (config.getBoolean("hooks.advancedban.enabled", true) && config.getBoolean("hooks.advancedban.export_is_banned", true) && com.minelaunched.stats.hooks.AdvancedBanHook.isEnabled()) {
                    Object val = com.minelaunched.stats.hooks.AdvancedBanHook.getStats(p);
                    if (val instanceof Number) po.addProperty("is_banned", (Number) val);
                    else if (val instanceof Boolean) po.addProperty("is_banned", (Boolean) val);
                    else if (val instanceof String) po.addProperty("is_banned", (String) val);
                }


    }
}
