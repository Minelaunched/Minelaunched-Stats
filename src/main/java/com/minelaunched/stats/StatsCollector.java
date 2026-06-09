package com.minelaunched.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.Statistic;
import org.bukkit.BanList;

import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

public class StatsCollector implements Callable<JsonObject> {

    private final FileConfiguration config;

    public StatsCollector(FileConfiguration config) {
        this.config = config;
    }

    private void addSafe(JsonObject obj, String confPath, String key, Callable<Object> valueSupplier) {
        if (!config.getBoolean(confPath + "." + key, true)) return;
        try {
            Object value = valueSupplier.call();
            if (value == null) return;
            if (value instanceof Number) obj.addProperty(key, (Number) value);
            else if (value instanceof String) obj.addProperty(key, (String) value);
            else if (value instanceof Boolean) obj.addProperty(key, (Boolean) value);
        } catch (Error | Exception ignored) {}
    }

    private void addSafe(JsonObject obj, String confPath, String confKey, String jsonKey, Callable<Object> valueSupplier) {
        if (!config.getBoolean(confPath + "." + confKey, true)) return;
        try {
            Object value = valueSupplier.call();
            if (value == null) return;
            if (value instanceof Number) obj.addProperty(jsonKey, (Number) value);
            else if (value instanceof String) obj.addProperty(jsonKey, (String) value);
            else if (value instanceof Boolean) obj.addProperty(jsonKey, (Boolean) value);
        } catch (Error | Exception ignored) {}
    }

    @Override
    public JsonObject call() {
        JsonObject root = new JsonObject();

        // 1. System Stats
        if (config.getBoolean("stats.system.enabled", true)) {
            JsonObject system = new JsonObject();
            String cp = "stats.system";
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            addSafe(system, cp, "os_name", () -> osBean.getName());
            addSafe(system, cp, "os_version", () -> osBean.getVersion());
            addSafe(system, cp, "os_arch", () -> osBean.getArch());
            addSafe(system, cp, "cores", () -> osBean.getAvailableProcessors());

            addSafe(system, cp, "cpu_load", () -> {
                Method m = osBean.getClass().getMethod("getSystemCpuLoad");
                m.setAccessible(true);
                return m.invoke(osBean);
            });
            addSafe(system, cp, "process_cpu_load", () -> {
                Method m = osBean.getClass().getMethod("getProcessCpuLoad");
                m.setAccessible(true);
                return m.invoke(osBean);
            });

            addSafe(system, cp, "java_version", () -> System.getProperty("java.version"));
            addSafe(system, cp, "java_vendor", () -> System.getProperty("java.vendor"));

            Runtime runtime = Runtime.getRuntime();
            addSafe(system, cp, "ram_max_mb", () -> runtime.maxMemory() / 1048576L);
            addSafe(system, cp, "ram_total_mb", () -> runtime.totalMemory() / 1048576L);
            addSafe(system, cp, "ram_free_mb", () -> runtime.freeMemory() / 1048576L);
            addSafe(system, cp, "ram_used_mb", () -> (runtime.totalMemory() - runtime.freeMemory()) / 1048576L);

            File rootDir = new File(".");
            addSafe(system, cp, "disk_total_mb", () -> rootDir.getTotalSpace() / 1048576L);
            addSafe(system, cp, "disk_usable_mb", () -> rootDir.getUsableSpace() / 1048576L);
            addSafe(system, cp, "jvm_uptime_ms", () -> ManagementFactory.getRuntimeMXBean().getUptime());

            addSafe(system, cp, "thread_count", () -> ManagementFactory.getThreadMXBean().getThreadCount());
            addSafe(system, cp, "peak_thread_count", () -> ManagementFactory.getThreadMXBean().getPeakThreadCount());
            addSafe(system, cp, "loaded_classes", () -> ManagementFactory.getClassLoadingMXBean().getLoadedClassCount());
            addSafe(system, cp, "total_loaded_classes", () -> ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount());
            addSafe(system, cp, "total_compilation_time_ms", () -> ManagementFactory.getCompilationMXBean().getTotalCompilationTime());

            if (config.getBoolean("stats.system.garbage_collectors", true)) {
                JsonArray gcArray = new JsonArray();
                for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
                    JsonObject gcObj = new JsonObject();
                    gcObj.addProperty("name", gc.getName());
                    gcObj.addProperty("collection_count", gc.getCollectionCount());
                    gcObj.addProperty("collection_time_ms", gc.getCollectionTime());
                    gcArray.add(gcObj);
                }
                system.add("garbage_collectors", gcArray);
            }

            if (config.getBoolean("stats.system.properties", true)) {
                JsonObject sysProps = new JsonObject();
                for (String key : System.getProperties().stringPropertyNames()) {
                    sysProps.addProperty(key, System.getProperty(key));
                }
                system.add("properties", sysProps);
            }

            if (config.getBoolean("stats.system.environment_variables", true)) {
                JsonObject envVars = new JsonObject();
                for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                    envVars.addProperty(entry.getKey(), entry.getValue());
                }
                system.add("environment_variables", envVars);
            }

            root.add("system", system);
        }

        // 2. Server Stats
        if (config.getBoolean("stats.server.enabled", true)) {
            JsonObject server = new JsonObject();
            String cp = "stats.server";
            addSafe(server, cp, "name", () -> Bukkit.getServerName());
            addSafe(server, cp, "bukkit_name", () -> Bukkit.getName());
            addSafe(server, cp, "version", () -> Bukkit.getVersion());
            addSafe(server, cp, "bukkit_version", () -> Bukkit.getBukkitVersion());
            addSafe(server, cp, "motd", () -> Bukkit.getMotd());
            addSafe(server, cp, "online_mode", () -> Bukkit.getOnlineMode());
            addSafe(server, cp, "is_hardcore", () -> Bukkit.isHardcore());
            addSafe(server, cp, "port", () -> Bukkit.getPort());
            addSafe(server, cp, "ip", () -> Bukkit.getIp());
            addSafe(server, cp, "tps", () -> TpsTracker.getTPS());
            addSafe(server, cp, "max_players", () -> Bukkit.getMaxPlayers());
            addSafe(server, cp, "whitelist_enabled", () -> Bukkit.hasWhitelist());

            addSafe(server, cp, "banned_ips_count", () -> Bukkit.getBanList(BanList.Type.IP).getBanEntries().size());
            addSafe(server, cp, "banned_names_count", () -> Bukkit.getBanList(BanList.Type.NAME).getBanEntries().size());
            
            addSafe(server, cp, "whitelisted_players_count", () -> Bukkit.getWhitelistedPlayers().size());
            addSafe(server, cp, "op_count", () -> Bukkit.getOperators().size());
            addSafe(server, cp, "idle_timeout", () -> Bukkit.getIdleTimeout());
            addSafe(server, cp, "generate_structures", () -> Bukkit.getGenerateStructures());
            addSafe(server, cp, "allow_nether", () -> Bukkit.getAllowNether());
            addSafe(server, cp, "allow_end", () -> Bukkit.getAllowEnd());
            addSafe(server, cp, "default_gamemode", () -> Bukkit.getDefaultGameMode().name());
            addSafe(server, cp, "spawn_radius", () -> Bukkit.getSpawnRadius());
            addSafe(server, cp, "view_distance", () -> Bukkit.getViewDistance());
            addSafe(server, cp, "connection_throttle", () -> Bukkit.getConnectionThrottle());
            addSafe(server, cp, "animal_spawn_limit", () -> Bukkit.getAnimalSpawnLimit());
            addSafe(server, cp, "monster_spawn_limit", () -> Bukkit.getMonsterSpawnLimit());
            addSafe(server, cp, "water_animal_spawn_limit", () -> Bukkit.getWaterAnimalSpawnLimit());
            addSafe(server, cp, "ambient_spawn_limit", () -> Bukkit.getAmbientSpawnLimit());
            addSafe(server, cp, "update_folder", () -> Bukkit.getUpdateFolder());
            addSafe(server, cp, "world_type", () -> Bukkit.getWorldType());

            if (config.getBoolean("hooks.vault", true) && com.minelaunched.stats.hooks.VaultHook.isEnabled()) {
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

            root.add("server", server);
        }

        // 3. Worlds Stats
        if (config.getBoolean("stats.worlds.enabled", true)) {
            JsonArray worldsArray = new JsonArray();
            int totalLoadedChunks = 0;
            int totalEntities = 0;
            String cp = "stats.worlds";
            for (World world : Bukkit.getWorlds()) {
                JsonObject w = new JsonObject();
                addSafe(w, cp, "name", () -> world.getName());
                addSafe(w, cp, "uuid", () -> world.getUID().toString());
                addSafe(w, cp, "environment", () -> world.getEnvironment().name());
                addSafe(w, cp, "world_type", () -> world.getWorldType().name());

                Chunk[] loadedChunks = world.getLoadedChunks();
                int chunkCount = loadedChunks != null ? loadedChunks.length : 0;
                addSafe(w, cp, "loaded_chunks", () -> chunkCount);
                totalLoadedChunks += chunkCount;

                int entityCount = world.getEntities().size();
                addSafe(w, cp, "entities", () -> entityCount);
                totalEntities += entityCount;

                addSafe(w, cp, "living_entities", () -> world.getLivingEntities().size());
                addSafe(w, cp, "players", () -> world.getPlayers().size());

                addSafe(w, cp, "time", () -> world.getTime());
                addSafe(w, cp, "full_time", () -> world.getFullTime());
                addSafe(w, cp, "has_storm", () -> world.hasStorm());
                addSafe(w, cp, "weather_duration", () -> world.getWeatherDuration());
                addSafe(w, cp, "is_thundering", () -> world.isThundering());
                addSafe(w, cp, "thunder_duration", () -> world.getThunderDuration());
                addSafe(w, cp, "seed", () -> world.getSeed());
                addSafe(w, cp, "difficulty", () -> world.getDifficulty().name());
                addSafe(w, cp, "max_height", () -> world.getMaxHeight());
                addSafe(w, cp, "sea_level", () -> world.getSeaLevel());
                addSafe(w, cp, "keep_spawn_in_memory", () -> world.getKeepSpawnInMemory());
                addSafe(w, cp, "pvp", () -> world.getPVP());
                addSafe(w, cp, "allow_animals", () -> world.getAllowAnimals());
                addSafe(w, cp, "allow_monsters", () -> world.getAllowMonsters());
                addSafe(w, cp, "ticks_per_animal_spawns", () -> world.getTicksPerAnimalSpawns());
                addSafe(w, cp, "ticks_per_monster_spawns", () -> world.getTicksPerMonsterSpawns());
                addSafe(w, cp, "animal_spawn_limit", () -> world.getAnimalSpawnLimit());
                addSafe(w, cp, "monster_spawn_limit", () -> world.getMonsterSpawnLimit());
                addSafe(w, cp, "water_animal_spawn_limit", () -> world.getWaterAnimalSpawnLimit());
                addSafe(w, cp, "ambient_spawn_limit", () -> world.getAmbientSpawnLimit());

                if (config.getBoolean(cp + ".spawn_location", true) && world.getSpawnLocation() != null) {
                    JsonObject spawnLoc = new JsonObject();
                    try { spawnLoc.addProperty("x", world.getSpawnLocation().getX()); } catch (Exception ignored) {}
                    try { spawnLoc.addProperty("y", world.getSpawnLocation().getY()); } catch (Exception ignored) {}
                    try { spawnLoc.addProperty("z", world.getSpawnLocation().getZ()); } catch (Exception ignored) {}
                    w.add("spawn_location", spawnLoc);
                }

                worldsArray.add(w);
            }
            root.add("worlds", worldsArray);
            root.addProperty("total_loaded_chunks", totalLoadedChunks);
            root.addProperty("total_entities", totalEntities);
        }

        // 4. Players Stats
        if (config.getBoolean("stats.players.enabled", true)) {
            JsonObject playersData = new JsonObject();
            String cp = "stats.players";
            addSafe(playersData, cp, "online_count", () -> Bukkit.getOnlinePlayers().size());
            addSafe(playersData, cp, "offline_total", () -> Bukkit.getOfflinePlayers().length);

            JsonArray onlinePlayersArray = new JsonArray();
            for (Player p : Bukkit.getOnlinePlayers()) {
                JsonObject po = new JsonObject();
                addSafe(po, cp, "name", () -> p.getName());
                addSafe(po, cp, "display_name", () -> p.getDisplayName());
                if (p.getCustomName() != null) addSafe(po, cp, "custom_name", () -> p.getCustomName());
                addSafe(po, cp, "custom_name_visible", () -> p.isCustomNameVisible());
                addSafe(po, cp, "uuid", () -> p.getUniqueId().toString());

                if (p.getAddress() != null) {
                    if (config.getBoolean(cp + ".ip", true) && p.getAddress().getAddress() != null) {
                        try { po.addProperty("ip", p.getAddress().getAddress().getHostAddress()); } catch (Exception ignored) {}
                    }
                    addSafe(po, cp, "port", () -> p.getAddress().getPort());
                }

                addSafe(po, cp, "health", () -> p.getHealth());
                addSafe(po, cp, "max_health", () -> p.getMaxHealth());
                addSafe(po, cp, "food", () -> p.getFoodLevel());
                addSafe(po, cp, "saturation", () -> p.getSaturation());
                addSafe(po, cp, "exhaustion", () -> p.getExhaustion());

                addSafe(po, cp, "level", () -> p.getLevel());
                addSafe(po, cp, "exp", () -> p.getExp());
                addSafe(po, cp, "total_exp", () -> p.getTotalExperience());
                addSafe(po, cp, "exp_to_level", () -> p.getExpToLevel());

                addSafe(po, cp, "gamemode", () -> p.getGameMode().name());
                addSafe(po, cp, "is_op", () -> p.isOp());
                addSafe(po, cp, "is_dead", () -> p.isDead());
                addSafe(po, cp, "is_flying", () -> p.isFlying());
                addSafe(po, cp, "allow_flight", () -> p.getAllowFlight());
                addSafe(po, cp, "is_sneaking", () -> p.isSneaking());
                addSafe(po, cp, "is_sprinting", () -> p.isSprinting());
                addSafe(po, cp, "is_sleeping", () -> p.isSleeping());

                addSafe(po, cp, "fire_ticks", () -> p.getFireTicks());
                addSafe(po, cp, "max_fire_ticks", () -> p.getMaxFireTicks());
                addSafe(po, cp, "fall_distance", () -> p.getFallDistance());
                addSafe(po, cp, "ticks_lived", () -> p.getTicksLived());
                addSafe(po, cp, "player_time", () -> p.getPlayerTime());
                addSafe(po, cp, "player_time_offset", () -> p.getPlayerTimeOffset());
                if (p.getPlayerWeather() != null) addSafe(po, cp, "player_weather", () -> p.getPlayerWeather().name());

                addSafe(po, cp, "fly_speed", () -> p.getFlySpeed());
                addSafe(po, cp, "walk_speed", () -> p.getWalkSpeed());
                addSafe(po, cp, "first_played_ms", () -> p.getFirstPlayed());
                addSafe(po, cp, "last_played_ms", () -> p.getLastPlayed());

                if (config.getBoolean("hooks.vault", true) && com.minelaunched.stats.hooks.VaultHook.isEnabled()) {
                    po.addProperty("vault_balance", com.minelaunched.stats.hooks.VaultHook.getBalance(p));
                }

                if (config.getBoolean("hooks.placeholderapi.enabled", true) && com.minelaunched.stats.hooks.PapiHook.isEnabled()) {
                    JsonObject papi = new JsonObject();
                    for (String ph : config.getStringList("hooks.placeholderapi.player_placeholders")) {
                        papi.addProperty(ph, com.minelaunched.stats.hooks.PapiHook.setPlaceholders(p, ph));
                    }
                    po.add("placeholders", papi);
                }

                if (config.getBoolean("hooks.luckperms", true) && com.minelaunched.stats.hooks.LuckPermsHook.isEnabled()) {
                    JsonObject lpData = com.minelaunched.stats.hooks.LuckPermsHook.getPlayerData(p);
                    if (lpData != null) po.add("luckperms", lpData);
                }

                if (config.getBoolean("hooks.essentials", true) && com.minelaunched.stats.hooks.EssentialsHook.isEnabled()) {
                    JsonObject essData = com.minelaunched.stats.hooks.EssentialsHook.getPlayerData(p);
                    if (essData != null) po.add("essentials", essData);
                }

                if (config.getBoolean("hooks.viaversion", true) && com.minelaunched.stats.hooks.ViaVersionHook.isEnabled()) {
                    JsonObject viaData = com.minelaunched.stats.hooks.ViaVersionHook.getPlayerData(p);
                    if (viaData != null) po.add("viaversion", viaData);
                }

                if (config.getBoolean("hooks.floodgate", true) && com.minelaunched.stats.hooks.FloodgateHook.isEnabled()) {
                    JsonObject floodgateData = com.minelaunched.stats.hooks.FloodgateHook.getPlayerData(p);
                    if (floodgateData != null) po.add("floodgate", floodgateData);
                }

                if (config.getBoolean("hooks.mcmmo", true) && com.minelaunched.stats.hooks.McMMOHook.isEnabled()) {
                    JsonObject mcmmoData = com.minelaunched.stats.hooks.McMMOHook.getPlayerData(p);
                    if (mcmmoData != null) po.add("mcmmo", mcmmoData);
                }

                if (config.getBoolean("hooks.playerpoints", true) && com.minelaunched.stats.hooks.PlayerPointsHook.isEnabled()) {
                    JsonObject ppData = com.minelaunched.stats.hooks.PlayerPointsHook.getPlayerData(p);
                    if (ppData != null) po.add("playerpoints", ppData);
                }

                if (config.getBoolean("hooks.griefprevention", true) && com.minelaunched.stats.hooks.GriefPreventionHook.isEnabled()) {
                    JsonObject gpData = com.minelaunched.stats.hooks.GriefPreventionHook.getPlayerData(p);
                    if (gpData != null) po.add("griefprevention", gpData);
                }

                if (config.getBoolean("hooks.auraskills", true) && com.minelaunched.stats.hooks.AuraSkillsHook.isEnabled()) {
                    JsonObject auraData = com.minelaunched.stats.hooks.AuraSkillsHook.getPlayerData(p);
                    if (auraData != null) po.add("auraskills", auraData);
                }

                if (config.getBoolean(cp + ".potion_effects", true)) {
                    JsonArray effects = new JsonArray();
                    for (PotionEffect effect : p.getActivePotionEffects()) {
                        JsonObject ef = new JsonObject();
                        ef.addProperty("type", effect.getType().getName());
                        ef.addProperty("amplifier", effect.getAmplifier());
                        ef.addProperty("duration_ticks", effect.getDuration());
                        effects.add(ef);
                    }
                    po.add("potion_effects", effects);
                }

                if (config.getBoolean(cp + ".ping", true)) {
                    addSafe(po, cp, "ping", () -> {
                        try {
                            Method getPing = p.getClass().getMethod("getPing");
                            return getPing.invoke(p);
                        } catch (Exception e) {
                            Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
                            return entityPlayer.getClass().getField("ping").getInt(entityPlayer);
                        }
                    });
                }

                if (config.getBoolean(cp + ".location", true)) {
                    JsonObject loc = new JsonObject();
                    try { loc.addProperty("world", p.getLocation().getWorld().getName()); } catch (Exception ignored) {}
                    try { loc.addProperty("x", p.getLocation().getX()); } catch (Exception ignored) {}
                    try { loc.addProperty("y", p.getLocation().getY()); } catch (Exception ignored) {}
                    try { loc.addProperty("z", p.getLocation().getZ()); } catch (Exception ignored) {}
                    po.add("location", loc);
                }
                
                if (config.getBoolean(cp + ".compass_target", true) && p.getCompassTarget() != null) {
                    JsonObject compass = new JsonObject();
                    try { compass.addProperty("x", p.getCompassTarget().getX()); } catch (Exception ignored) {}
                    try { compass.addProperty("y", p.getCompassTarget().getY()); } catch (Exception ignored) {}
                    try { compass.addProperty("z", p.getCompassTarget().getZ()); } catch (Exception ignored) {}
                    po.add("compass_target", compass);
                }

                if (config.getBoolean(cp + ".inventory", true)) {
                    JsonArray inventoryArray = new JsonArray();
                    for (int i = 0; i < p.getInventory().getSize(); i++) {
                        org.bukkit.inventory.ItemStack item = p.getInventory().getItem(i);
                        if (item != null && item.getType() != org.bukkit.Material.AIR) {
                            JsonObject itemObj = new JsonObject();
                            itemObj.addProperty("slot", i);
                            itemObj.addProperty("type", item.getType().name());
                            itemObj.addProperty("amount", item.getAmount());
                            itemObj.addProperty("durability", item.getDurability());
                            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                itemObj.addProperty("display_name", item.getItemMeta().getDisplayName());
                            }
                            inventoryArray.add(itemObj);
                        }
                    }
                    po.add("inventory", inventoryArray);
                }

                if (config.getBoolean(cp + ".armor", true)) {
                    JsonArray armorArray = new JsonArray();
                    org.bukkit.inventory.ItemStack[] armorContents = p.getInventory().getArmorContents();
                    for (int i = 0; i < armorContents.length; i++) {
                        org.bukkit.inventory.ItemStack item = armorContents[i];
                        if (item != null && item.getType() != org.bukkit.Material.AIR) {
                            JsonObject itemObj = new JsonObject();
                            itemObj.addProperty("slot", i); // 0=boots, 1=leggings, 2=chestplate, 3=helmet
                            itemObj.addProperty("type", item.getType().name());
                            itemObj.addProperty("amount", item.getAmount());
                            itemObj.addProperty("durability", item.getDurability());
                            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                                itemObj.addProperty("display_name", item.getItemMeta().getDisplayName());
                            }
                            armorArray.add(itemObj);
                        }
                    }
                    po.add("armor", armorArray);
                }

                if (config.getBoolean(cp + ".game_statistics", true)) {
                    JsonObject statsObj = new JsonObject();
                    for (Statistic stat : Statistic.values()) {
                        try {
                            int val = p.getStatistic(stat);
                            statsObj.addProperty(stat.name(), val);
                        } catch (IllegalArgumentException | NoSuchMethodError e) {
                            // Requires parameter or missing in old version
                        }
                    }
                    po.add("game_statistics", statsObj);
                }

                onlinePlayersArray.add(po);
            }
            playersData.add("online_players", onlinePlayersArray);
            root.add("players", playersData);
        }

        // 5. Plugins
        if (config.getBoolean("stats.plugins.enabled", true)) {
            JsonArray pluginsArray = new JsonArray();
            String cp = "stats.plugins";
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                JsonObject pl = new JsonObject();
                addSafe(pl, cp, "name", () -> plugin.getName());
                addSafe(pl, cp, "version", () -> plugin.getDescription().getVersion());
                addSafe(pl, cp, "plugin_enabled", "enabled", () -> plugin.isEnabled());
                
                if (config.getBoolean(cp + ".author", true) && plugin.getDescription().getAuthors() != null && !plugin.getDescription().getAuthors().isEmpty()) {
                    pl.addProperty("author", String.join(", ", plugin.getDescription().getAuthors()));
                }
                if (config.getBoolean(cp + ".description", true) && plugin.getDescription().getDescription() != null) {
                    pl.addProperty("description", plugin.getDescription().getDescription());
                }
                if (config.getBoolean(cp + ".website", true) && plugin.getDescription().getWebsite() != null) {
                    pl.addProperty("website", plugin.getDescription().getWebsite());
                }
                pluginsArray.add(pl);
            }
            root.add("plugins", pluginsArray);
        }

        return root;
    }
}
