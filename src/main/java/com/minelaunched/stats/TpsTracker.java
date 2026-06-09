package com.minelaunched.stats;

import org.bukkit.scheduler.BukkitRunnable;

public class TpsTracker extends BukkitRunnable {
    private static int tickCount = 0;
    private static final long[] ticks = new long[600];

    @Override
    public void run() {
        ticks[(tickCount % ticks.length)] = System.currentTimeMillis();
        tickCount++;
    }

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticksToMeasure) {
        if (tickCount < ticksToMeasure) return 20.0;
        int target = (tickCount - 1 - ticksToMeasure) % ticks.length;
        long elapsed = System.currentTimeMillis() - ticks[target];
        double tps = ticksToMeasure / (elapsed / 1000.0);
        return Math.min(20.0, Math.round(tps * 100.0) / 100.0);
    }
}
