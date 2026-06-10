package com.minelaunched.stats.collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Statistic;
import org.bukkit.potion.PotionEffect;
import java.io.File;
import java.lang.reflect.Method;

public class PlayerCollector {
    public static JsonObject collect(FileConfiguration config) {
        JsonObject root = new JsonObject(); // Fake root to hold the collected data, wait actually we should return the collected array/object directly.
        // Actually, the blocks add directly to `root`. So let's provide a `root` and return the element it added.
        // Let's just wrap the block as it is, since it adds to `root`.
        JsonObject result = new JsonObject();
        JsonObject rootSubstitute = new JsonObject();
        // 4. Players Stats
        if (config.getBoolean("stats.players.enabled", true)) {
            JsonObject playersData = new JsonObject();
            String cp = "stats.players";
            CollectorUtils.addSafe(config, playersData, cp, "online_count", () -> Bukkit.getOnlinePlayers().size());
            CollectorUtils.addSafe(config, playersData, cp, "offline_total", () -> Bukkit.getOfflinePlayers().length);

            JsonArray onlinePlayersArray = new JsonArray();
            for (Player p : Bukkit.getOnlinePlayers()) {
                JsonObject po = new JsonObject();
                CollectorUtils.addSafe(config, po, cp, "name", () -> p.getName());
                CollectorUtils.addSafe(config, po, cp, "display_name", () -> p.getDisplayName());
                if (p.getCustomName() != null) CollectorUtils.addSafe(config, po, cp, "custom_name", () -> p.getCustomName());
                CollectorUtils.addSafe(config, po, cp, "custom_name_visible", () -> p.isCustomNameVisible());
                CollectorUtils.addSafe(config, po, cp, "uuid", () -> p.getUniqueId().toString());

                if (p.getAddress() != null) {
                    if (config.getBoolean(cp + ".ip", true) && p.getAddress().getAddress() != null) {
                        try { po.addProperty("ip", p.getAddress().getAddress().getHostAddress()); } catch (Exception ignored) {}
                    }
                    CollectorUtils.addSafe(config, po, cp, "port", () -> p.getAddress().getPort());
                }

                CollectorUtils.addSafe(config, po, cp, "health", () -> p.getHealth());
                CollectorUtils.addSafe(config, po, cp, "max_health", () -> p.getMaxHealth());
                CollectorUtils.addSafe(config, po, cp, "food", () -> p.getFoodLevel());
                CollectorUtils.addSafe(config, po, cp, "saturation", () -> p.getSaturation());
                CollectorUtils.addSafe(config, po, cp, "exhaustion", () -> p.getExhaustion());

                CollectorUtils.addSafe(config, po, cp, "level", () -> p.getLevel());
                CollectorUtils.addSafe(config, po, cp, "exp", () -> p.getExp());
                CollectorUtils.addSafe(config, po, cp, "total_exp", () -> p.getTotalExperience());
                CollectorUtils.addSafe(config, po, cp, "exp_to_level", () -> p.getExpToLevel());

                CollectorUtils.addSafe(config, po, cp, "gamemode", () -> p.getGameMode().name());
                CollectorUtils.addSafe(config, po, cp, "is_op", () -> p.isOp());
                CollectorUtils.addSafe(config, po, cp, "is_dead", () -> p.isDead());
                CollectorUtils.addSafe(config, po, cp, "is_flying", () -> p.isFlying());
                CollectorUtils.addSafe(config, po, cp, "allow_flight", () -> p.getAllowFlight());
                CollectorUtils.addSafe(config, po, cp, "is_sneaking", () -> p.isSneaking());
                CollectorUtils.addSafe(config, po, cp, "is_sprinting", () -> p.isSprinting());
                CollectorUtils.addSafe(config, po, cp, "is_sleeping", () -> p.isSleeping());

                CollectorUtils.addSafe(config, po, cp, "fire_ticks", () -> p.getFireTicks());
                CollectorUtils.addSafe(config, po, cp, "max_fire_ticks", () -> p.getMaxFireTicks());
                CollectorUtils.addSafe(config, po, cp, "fall_distance", () -> p.getFallDistance());
                CollectorUtils.addSafe(config, po, cp, "ticks_lived", () -> p.getTicksLived());
                CollectorUtils.addSafe(config, po, cp, "player_time", () -> p.getPlayerTime());
                CollectorUtils.addSafe(config, po, cp, "player_time_offset", () -> p.getPlayerTimeOffset());
                if (p.getPlayerWeather() != null) CollectorUtils.addSafe(config, po, cp, "player_weather", () -> p.getPlayerWeather().name());

                CollectorUtils.addSafe(config, po, cp, "fly_speed", () -> p.getFlySpeed());
                CollectorUtils.addSafe(config, po, cp, "walk_speed", () -> p.getWalkSpeed());
                CollectorUtils.addSafe(config, po, cp, "first_played_ms", () -> p.getFirstPlayed());
                CollectorUtils.addSafe(config, po, cp, "last_played_ms", () -> p.getLastPlayed());

                com.minelaunched.stats.hooks.HookManager.appendPlayerHooks(po, p, config);
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
                    CollectorUtils.addSafe(config, po, cp, "ping", () -> {
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
            rootSubstitute.add("players", playersData);
        }

        
        // The block puts its result into rootSubstitute under a key (e.g. "system"). 
        // We just return that element.
        if (rootSubstitute.has("player")) return rootSubstitute.getAsJsonObject("player");
        if (rootSubstitute.has("online_players")) return rootSubstitute; // Quick hack for players since playersData is what we want... wait, "players" is the key.
        if (rootSubstitute.has("players")) return rootSubstitute.getAsJsonArray("players") != null ? new JsonObject() : new JsonObject();
        
        // Actually, let's just return the rootSubstitute, and let StatsCollector combine them via addAll.
        return rootSubstitute;
    }
}
