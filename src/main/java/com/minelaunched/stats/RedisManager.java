package com.minelaunched.stats;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class RedisManager {

    private final MinelaunchedStatsPlugin plugin;
    private JedisPool jedisPool;
    private BukkitRunnable publishTask;
    private final String channel;

    public RedisManager(MinelaunchedStatsPlugin plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.channel = config.getString("redis.channel", "minelaunched_stats");
        
        String host = config.getString("redis.host", "127.0.0.1");
        int port = config.getInt("redis.port", 6379);
        String password = config.getString("redis.password", "");
        
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        
        if (password != null && !password.isEmpty()) {
            jedisPool = new JedisPool(poolConfig, host, port, 2000, password);
        } else {
            jedisPool = new JedisPool(poolConfig, host, port, 2000);
        }
        
        startPublishing(config.getInt("redis.update_interval_seconds", 5));
    }

    private void startPublishing(int intervalSeconds) {
        publishTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Fetch stats on main thread safely
                Future<JsonObject> future = Bukkit.getScheduler().callSyncMethod(plugin, new StatsCollector(plugin.getConfig()));
                
                // Then publish async
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        JsonObject stats = future.get();
                        try (Jedis jedis = jedisPool.getResource()) {
                            jedis.publish(channel, stats.toString());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        plugin.getLogger().warning("[RedisManager] Failed to fetch stats for publishing: " + e.getMessage());
                    } catch (Exception e) {
                        plugin.getLogger().warning("[RedisManager] Failed to publish stats to Redis: " + e.getMessage());
                    }
                });
            }
        };
        
        // Convert seconds to ticks (20 ticks = 1 second)
        publishTask.runTaskTimer(plugin, 20L * intervalSeconds, 20L * intervalSeconds);
        plugin.getLogger().info("Started publishing stats to Redis channel '" + channel + "' every " + intervalSeconds + " seconds.");
    }

    public void stop() {
        if (publishTask != null) {
            publishTask.cancel();
        }
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }
}
