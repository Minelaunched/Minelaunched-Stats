package com.minelaunched.stats.collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.BanList;
import com.minelaunched.stats.utils.TpsTracker;
import java.io.File;
import java.lang.reflect.Method;

public class ServerCollector {
    public static JsonObject collect(FileConfiguration config) {
        JsonObject root = new JsonObject(); // Fake root to hold the collected data, wait actually we should return the collected array/object directly.
        // Actually, the blocks add directly to `root`. So let's provide a `root` and return the element it added.
        // Let's just wrap the block as it is, since it adds to `root`.
        JsonObject result = new JsonObject();
        JsonObject rootSubstitute = new JsonObject();
        // 2. Server Stats
        if (config.getBoolean("stats.server.enabled", true)) {
            JsonObject server = new JsonObject();
            String cp = "stats.server";
            CollectorUtils.addSafe(config, server, cp, "name", () -> Bukkit.getServerName());
            CollectorUtils.addSafe(config, server, cp, "bukkit_name", () -> Bukkit.getName());
            CollectorUtils.addSafe(config, server, cp, "version", () -> Bukkit.getVersion());
            CollectorUtils.addSafe(config, server, cp, "bukkit_version", () -> Bukkit.getBukkitVersion());
            CollectorUtils.addSafe(config, server, cp, "motd", () -> Bukkit.getMotd());
            CollectorUtils.addSafe(config, server, cp, "online_mode", () -> Bukkit.getOnlineMode());
            CollectorUtils.addSafe(config, server, cp, "is_hardcore", () -> Bukkit.isHardcore());
            CollectorUtils.addSafe(config, server, cp, "port", () -> Bukkit.getPort());
            CollectorUtils.addSafe(config, server, cp, "ip", () -> Bukkit.getIp());
            CollectorUtils.addSafe(config, server, cp, "tps", () -> TpsTracker.getTPS());
            CollectorUtils.addSafe(config, server, cp, "max_players", () -> Bukkit.getMaxPlayers());
            CollectorUtils.addSafe(config, server, cp, "whitelist_enabled", () -> Bukkit.hasWhitelist());

            CollectorUtils.addSafe(config, server, cp, "banned_ips_count", () -> Bukkit.getBanList(BanList.Type.IP).getBanEntries().size());
            CollectorUtils.addSafe(config, server, cp, "banned_names_count", () -> Bukkit.getBanList(BanList.Type.NAME).getBanEntries().size());
            
            CollectorUtils.addSafe(config, server, cp, "whitelisted_players_count", () -> Bukkit.getWhitelistedPlayers().size());
            CollectorUtils.addSafe(config, server, cp, "op_count", () -> Bukkit.getOperators().size());
            CollectorUtils.addSafe(config, server, cp, "idle_timeout", () -> Bukkit.getIdleTimeout());
            CollectorUtils.addSafe(config, server, cp, "generate_structures", () -> Bukkit.getGenerateStructures());
            CollectorUtils.addSafe(config, server, cp, "allow_nether", () -> Bukkit.getAllowNether());
            CollectorUtils.addSafe(config, server, cp, "allow_end", () -> Bukkit.getAllowEnd());
            CollectorUtils.addSafe(config, server, cp, "default_gamemode", () -> Bukkit.getDefaultGameMode().name());
            CollectorUtils.addSafe(config, server, cp, "spawn_radius", () -> Bukkit.getSpawnRadius());
            CollectorUtils.addSafe(config, server, cp, "view_distance", () -> Bukkit.getViewDistance());
            CollectorUtils.addSafe(config, server, cp, "connection_throttle", () -> Bukkit.getConnectionThrottle());
            CollectorUtils.addSafe(config, server, cp, "animal_spawn_limit", () -> Bukkit.getAnimalSpawnLimit());
            CollectorUtils.addSafe(config, server, cp, "monster_spawn_limit", () -> Bukkit.getMonsterSpawnLimit());
            CollectorUtils.addSafe(config, server, cp, "water_animal_spawn_limit", () -> Bukkit.getWaterAnimalSpawnLimit());
            CollectorUtils.addSafe(config, server, cp, "ambient_spawn_limit", () -> Bukkit.getAmbientSpawnLimit());
            CollectorUtils.addSafe(config, server, cp, "update_folder", () -> Bukkit.getUpdateFolder());
            CollectorUtils.addSafe(config, server, cp, "world_type", () -> Bukkit.getWorldType());

            com.minelaunched.stats.hooks.HookManager.appendServerHooks(server, config);

            rootSubstitute.add("server", server);
        }

        
        return rootSubstitute;
    }
}
