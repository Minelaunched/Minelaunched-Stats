package com.minelaunched.stats.collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

public class SystemCollector {
    public static JsonObject collect(FileConfiguration config) {
        JsonObject root = new JsonObject(); // Fake root to hold the collected data, wait actually we should return the collected array/object directly.
        // Actually, the blocks add directly to `root`. So let's provide a `root` and return the element it added.
        // Let's just wrap the block as it is, since it adds to `root`.
        JsonObject result = new JsonObject();
        JsonObject rootSubstitute = new JsonObject();
        // 1. System Stats
        if (config.getBoolean("stats.system.enabled", true)) {
            JsonObject system = new JsonObject();
            String cp = "stats.system";
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            CollectorUtils.addSafe(config, system, cp, "os_name", () -> osBean.getName());
            CollectorUtils.addSafe(config, system, cp, "os_version", () -> osBean.getVersion());
            CollectorUtils.addSafe(config, system, cp, "os_arch", () -> osBean.getArch());
            CollectorUtils.addSafe(config, system, cp, "cores", () -> osBean.getAvailableProcessors());

            CollectorUtils.addSafe(config, system, cp, "cpu_load", () -> {
                Method m = osBean.getClass().getMethod("getSystemCpuLoad");
                m.setAccessible(true);
                return m.invoke(osBean);
            });
            CollectorUtils.addSafe(config, system, cp, "process_cpu_load", () -> {
                Method m = osBean.getClass().getMethod("getProcessCpuLoad");
                m.setAccessible(true);
                return m.invoke(osBean);
            });

            CollectorUtils.addSafe(config, system, cp, "java_version", () -> System.getProperty("java.version"));
            CollectorUtils.addSafe(config, system, cp, "java_vendor", () -> System.getProperty("java.vendor"));

            Runtime runtime = Runtime.getRuntime();
            CollectorUtils.addSafe(config, system, cp, "ram_max_mb", () -> runtime.maxMemory() / 1048576L);
            CollectorUtils.addSafe(config, system, cp, "ram_total_mb", () -> runtime.totalMemory() / 1048576L);
            CollectorUtils.addSafe(config, system, cp, "ram_free_mb", () -> runtime.freeMemory() / 1048576L);
            CollectorUtils.addSafe(config, system, cp, "ram_used_mb", () -> (runtime.totalMemory() - runtime.freeMemory()) / 1048576L);

            File rootDir = new File(".");
            CollectorUtils.addSafe(config, system, cp, "disk_total_mb", () -> rootDir.getTotalSpace() / 1048576L);
            CollectorUtils.addSafe(config, system, cp, "disk_usable_mb", () -> rootDir.getUsableSpace() / 1048576L);
            CollectorUtils.addSafe(config, system, cp, "jvm_uptime_ms", () -> ManagementFactory.getRuntimeMXBean().getUptime());

            CollectorUtils.addSafe(config, system, cp, "thread_count", () -> ManagementFactory.getThreadMXBean().getThreadCount());
            CollectorUtils.addSafe(config, system, cp, "peak_thread_count", () -> ManagementFactory.getThreadMXBean().getPeakThreadCount());
            CollectorUtils.addSafe(config, system, cp, "loaded_classes", () -> ManagementFactory.getClassLoadingMXBean().getLoadedClassCount());
            CollectorUtils.addSafe(config, system, cp, "total_loaded_classes", () -> ManagementFactory.getClassLoadingMXBean().getTotalLoadedClassCount());
            CollectorUtils.addSafe(config, system, cp, "total_compilation_time_ms", () -> ManagementFactory.getCompilationMXBean().getTotalCompilationTime());

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

            rootSubstitute.add("system", system);
        }

        
        return rootSubstitute;
    }
}
