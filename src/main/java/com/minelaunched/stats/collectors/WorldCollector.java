package com.minelaunched.stats.collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.World;
import org.bukkit.Chunk;
import java.io.File;
import java.lang.reflect.Method;

public class WorldCollector {
    public static JsonObject collect(FileConfiguration config) {
        JsonObject root = new JsonObject(); // Fake root to hold the collected data, wait actually we should return the collected array/object directly.
        // Actually, the blocks add directly to `root`. So let's provide a `root` and return the element it added.
        // Let's just wrap the block as it is, since it adds to `root`.
        JsonObject result = new JsonObject();
        JsonObject rootSubstitute = new JsonObject();
        // 3. Worlds Stats
        if (config.getBoolean("stats.worlds.enabled", true)) {
            JsonArray worldsArray = new JsonArray();
            int totalLoadedChunks = 0;
            int totalEntities = 0;
            String cp = "stats.worlds";
            for (World world : Bukkit.getWorlds()) {
                JsonObject w = new JsonObject();
                CollectorUtils.addSafe(config, w, cp, "name", () -> world.getName());
                CollectorUtils.addSafe(config, w, cp, "uuid", () -> world.getUID().toString());
                CollectorUtils.addSafe(config, w, cp, "environment", () -> world.getEnvironment().name());
                CollectorUtils.addSafe(config, w, cp, "world_type", () -> world.getWorldType().name());

                Chunk[] loadedChunks = world.getLoadedChunks();
                int chunkCount = loadedChunks != null ? loadedChunks.length : 0;
                CollectorUtils.addSafe(config, w, cp, "loaded_chunks", () -> chunkCount);
                totalLoadedChunks += chunkCount;

                int entityCount = world.getEntities().size();
                CollectorUtils.addSafe(config, w, cp, "entities", () -> entityCount);
                totalEntities += entityCount;

                CollectorUtils.addSafe(config, w, cp, "living_entities", () -> world.getLivingEntities().size());
                CollectorUtils.addSafe(config, w, cp, "players", () -> world.getPlayers().size());

                CollectorUtils.addSafe(config, w, cp, "time", () -> world.getTime());
                CollectorUtils.addSafe(config, w, cp, "full_time", () -> world.getFullTime());
                CollectorUtils.addSafe(config, w, cp, "has_storm", () -> world.hasStorm());
                CollectorUtils.addSafe(config, w, cp, "weather_duration", () -> world.getWeatherDuration());
                CollectorUtils.addSafe(config, w, cp, "is_thundering", () -> world.isThundering());
                CollectorUtils.addSafe(config, w, cp, "thunder_duration", () -> world.getThunderDuration());
                CollectorUtils.addSafe(config, w, cp, "seed", () -> world.getSeed());
                CollectorUtils.addSafe(config, w, cp, "difficulty", () -> world.getDifficulty().name());
                CollectorUtils.addSafe(config, w, cp, "max_height", () -> world.getMaxHeight());
                CollectorUtils.addSafe(config, w, cp, "sea_level", () -> world.getSeaLevel());
                CollectorUtils.addSafe(config, w, cp, "keep_spawn_in_memory", () -> world.getKeepSpawnInMemory());
                CollectorUtils.addSafe(config, w, cp, "pvp", () -> world.getPVP());
                CollectorUtils.addSafe(config, w, cp, "allow_animals", () -> world.getAllowAnimals());
                CollectorUtils.addSafe(config, w, cp, "allow_monsters", () -> world.getAllowMonsters());
                CollectorUtils.addSafe(config, w, cp, "ticks_per_animal_spawns", () -> world.getTicksPerAnimalSpawns());
                CollectorUtils.addSafe(config, w, cp, "ticks_per_monster_spawns", () -> world.getTicksPerMonsterSpawns());
                CollectorUtils.addSafe(config, w, cp, "animal_spawn_limit", () -> world.getAnimalSpawnLimit());
                CollectorUtils.addSafe(config, w, cp, "monster_spawn_limit", () -> world.getMonsterSpawnLimit());
                CollectorUtils.addSafe(config, w, cp, "water_animal_spawn_limit", () -> world.getWaterAnimalSpawnLimit());
                CollectorUtils.addSafe(config, w, cp, "ambient_spawn_limit", () -> world.getAmbientSpawnLimit());

                if (config.getBoolean(cp + ".spawn_location", true) && world.getSpawnLocation() != null) {
                    JsonObject spawnLoc = new JsonObject();
                    try { spawnLoc.addProperty("x", world.getSpawnLocation().getX()); } catch (Exception ignored) {}
                    try { spawnLoc.addProperty("y", world.getSpawnLocation().getY()); } catch (Exception ignored) {}
                    try { spawnLoc.addProperty("z", world.getSpawnLocation().getZ()); } catch (Exception ignored) {}
                    w.add("spawn_location", spawnLoc);
                }

                worldsArray.add(w);
            }
            rootSubstitute.add("worlds", worldsArray);
            rootSubstitute.addProperty("total_loaded_chunks", totalLoadedChunks);
            rootSubstitute.addProperty("total_entities", totalEntities);
        }

        
        return rootSubstitute;
    }
}
