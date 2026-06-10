package com.minelaunched.stats.collectors;

import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.concurrent.Callable;

public class CollectorUtils {
    public static void addSafe(FileConfiguration config, JsonObject obj, String confPath, String key, Callable<Object> valueSupplier) {
        if (!config.getBoolean(confPath + "." + key, true)) return;
        try {
            Object value = valueSupplier.call();
            if (value == null) return;
            if (value instanceof Number) obj.addProperty(key, (Number) value);
            else if (value instanceof String) obj.addProperty(key, (String) value);
            else if (value instanceof Boolean) obj.addProperty(key, (Boolean) value);
        } catch (Error | Exception ignored) {}
    }

    public static void addSafe(FileConfiguration config, JsonObject obj, String confPath, String confKey, String jsonKey, Callable<Object> valueSupplier) {
        if (!config.getBoolean(confPath + "." + confKey, true)) return;
        try {
            Object value = valueSupplier.call();
            if (value == null) return;
            if (value instanceof Number) obj.addProperty(jsonKey, (Number) value);
            else if (value instanceof String) obj.addProperty(jsonKey, (String) value);
            else if (value instanceof Boolean) obj.addProperty(jsonKey, (Boolean) value);
        } catch (Error | Exception ignored) {}
    }
}
