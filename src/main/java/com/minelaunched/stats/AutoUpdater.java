package com.minelaunched.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoUpdater {

    private final MinelaunchedStatsPlugin plugin;

    public AutoUpdater(MinelaunchedStatsPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        if (!plugin.getConfig().getBoolean("auto_updater.enabled", true)) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String repo = plugin.getConfig().getString("auto_updater.github_repo", "Minelaunched/Minelaunched-Stats");
                URL url = new URL("https://api.github.com/repos/" + repo + "/releases/latest");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
                conn.setRequestProperty("User-Agent", "MinelaunchedStats-Updater");

                if (conn.getResponseCode() != 200) {
                    plugin.getLogger().warning("AutoUpdater: Failed to check for updates (HTTP " + conn.getResponseCode() + ")");
                    return;
                }

                @SuppressWarnings("deprecation")
                JsonObject json = new JsonParser().parse(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
                String latestVersion = json.get("tag_name").getAsString().replace("v", "");
                String currentVersion = plugin.getDescription().getVersion().replace("v", "");

                if (isNewerVersion(currentVersion, latestVersion)) {
                    plugin.getLogger().info("AutoUpdater: A new version (" + latestVersion + ") is available! Downloading...");
                    
                    JsonArray assets = json.getAsJsonArray("assets");
                    if (assets.size() > 0) {
                        String downloadUrl = assets.get(0).getAsJsonObject().get("browser_download_url").getAsString();
                        downloadUpdate(downloadUrl);
                    } else {
                        plugin.getLogger().warning("AutoUpdater: No .jar asset found in the latest release!");
                    }
                } else {
                    plugin.getLogger().info("AutoUpdater: Plugin is up to date (" + currentVersion + ").");
                }

            } catch (Exception e) {
                plugin.getLogger().warning("AutoUpdater: Error checking for updates: " + e.getMessage());
            }
        });
    }

    private boolean isNewerVersion(String current, String latest) {
        try {
            String[] curParts = current.split("\\.");
            String[] latParts = latest.split("\\.");
            int length = Math.max(curParts.length, latParts.length);
            for (int i = 0; i < length; i++) {
                int c = i < curParts.length ? Integer.parseInt(curParts[i]) : 0;
                int l = i < latParts.length ? Integer.parseInt(latParts[i]) : 0;
                if (l > c) return true;
                if (c > l) return false;
            }
        } catch (Exception e) {
            return !current.equalsIgnoreCase(latest);
        }
        return false;
    }

    private void downloadUpdate(String downloadUrl) {
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "MinelaunchedStats-Updater");

            File updateFolder = Bukkit.getServer().getUpdateFolderFile();
            if (!updateFolder.exists()) {
                updateFolder.mkdirs();
            }

            File pluginFile = new File(updateFolder, "MinelaunchedStats.jar");

            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(pluginFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            plugin.getLogger().info("AutoUpdater: Update downloaded successfully! It will be applied on the next server restart.");

        } catch (Exception e) {
            plugin.getLogger().warning("AutoUpdater: Failed to download update: " + e.getMessage());
        }
    }
}
