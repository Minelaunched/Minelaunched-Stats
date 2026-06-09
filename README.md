<div align="center">
  <h1>📊 MinelaunchedStats</h1>
  <p><b>The Ultimate, Universal Minecraft Server Statistics Exporter</b></p>
  <p><i>Export every single piece of data from your Minecraft server in a clean, comprehensive JSON API.</i></p>
</div>

---

## 🌟 Overview

**MinelaunchedStats** is a high-performance, universal Bukkit plugin designed to extract an exhaustive list of statistics from your Minecraft server and expose them via a fast, built-in HTTP JSON API. 

Whether you want to build a custom web dashboard, monitor your infrastructure, or integrate with Discord bots, this plugin gives you **unprecedented access** to your server's data without causing lag or requiring external databases.

### ✨ Key Features

- **🛡️ Universal Compatibility (1.8.8 to Latest):** Built with a unique "Safe-Wrap" architecture. It dynamically scans the server and extracts whatever data the API version supports. It works flawlessly on Spigot, Paper, Purpur, Pufferfish, and Folia without throwing `NoSuchMethodError`.
- **⚡ Native Fast Web Server:** Hosts a lightweight JVM web server directly inside the plugin. No Apache or Nginx required.
- **⚙️ Ultimate Customization:** Every single JSON field can be individually toggled in the `config.yml`.
- **🚀 Auto-Updater:** Built-in OTA (Over-The-Air) updates hooked directly into GitHub Releases.
- **🛡️ Anti-DDOS Caching:** Prevents server lag by caching responses to stop API spam.
- **🔒 Secure:** Optionally require an authorization token to access your data.

---

## 📦 Installation

1. Download the latest `MinelaunchedStats.jar` from the [Releases](https://github.com/Minelaunched/Minelaunched-Stats/releases) page.
2. Place the `.jar` file into your server's `plugins` folder.
3. Restart your server.
4. Open the `plugins/MinelaunchedStats/config.yml` file to adjust the port, endpoint, and toggles.
5. Access your stats by navigating to your server's IP and port in your browser (e.g., `http://<your-server-ip>:8080/`).

## ⌨️ Commands & Permissions

- `/mlstats reload` - Reloads the configuration file and restarts the internal web server without needing to reboot the entire Minecraft server.
- **Permission:** `mlstats.admin`

---

## ⚙️ Configuration

The `config.yml` is heavily documented and allows you to toggle exactly what data is exported. By disabling data you don't need, you can reduce the JSON size and save CPU cycles.

```yaml
web:
  port: 8080
  endpoint: "/"
  pretty_print: false
  cors_origin: "*"

auto_updater:
  enabled: true
  github_repo: "Minelaunched/Minelaunched-Stats"

stats:
  # Contains hundreds of individual toggles for System, Server, Worlds, Players, and Plugins.
```

---

## 📡 What Data is Exported?

The plugin exports a massive JSON object divided into 5 main categories:

### 1. 🖥️ System Stats
Real-time monitoring of the JVM and the host machine:
- CPU Load (Process & System), Cores Count
- RAM Usage (Total, Free, Max, Used)
- Disk Space
- Thread counts, Loaded Classes, Compilation times
- Advanced Garbage Collector statistics
- OS Info, Java version, and Environment Variables

### 2. 🌍 Server Stats
Everything about the Bukkit/Paper instance:
- True TPS (Ticks Per Second)
- Max Players, Online Mode, Hardcore
- IP, Port, MOTD
- Ban Lists, Whitelist metrics, OP counts
- View distance, Spawn limits, and exact Paper/Bukkit version strings

### 3. 🗺️ Worlds Stats
Granular data for every loaded dimension (Overworld, Nether, End):
- Loaded Chunks count
- Total Entities and Living Entities counts
- Time, Weather, and Storm durations
- Difficulty, Seeds, PvP status
- Spawn Locations

### 4. 👥 Players Stats
Extensive data for every online player:
- Health, Food, Exhaustion, Saturation, EXP, Level
- Latency (Ping)
- Exact Location (X, Y, Z, World) & Compass targets
- Active Potion Effects
- **Full Inventory & Armor Dump:** Slot numbers, Material types, amounts, durability, and custom item names.
- **Game Statistics:** A massive dump of all native Minecraft statistics (blocks broken, distance walked, kills, deaths, etc.).

### 5. 🔌 Plugins
A list of all installed plugins, including:
- Name, Version, Enabled Status
- Authors, Descriptions, and Website links

---

## 🔗 3rd Party API Hooks

To make MinelaunchedStats truly universal, we have implemented completely safe, modular integrations with the biggest plugins in Minecraft. If you don't have them installed, the plugin ignores them gracefully without crashing:

- **Vault:** Exports the exact balance of each player, and calculates the total server economy (GDP) for inflation tracking.
- **PlaceholderAPI:** Allows you to translate ANY custom placeholder strings globally (e.g. `%server_uptime%`) or per-player (e.g. `%player_ping%`).
- **LuckPerms:** Exports the Primary Group (Rank), Prefix, and Suffix of each online player.
- **EssentialsX:** Exports real-time status attributes: `is_afk`, `is_vanished`, `is_god_mode`, `is_muted`, and `nickname`.
- **ViaVersion:** Exports the exact Minecraft client protocol version of each player (e.g. 1.20.4).
- **Floodgate (Geyser):** Automatically tags players as Java or Bedrock and exports their device platform (Xbox, iOS, Android).
- **mcMMO:** Exports the RPG `power_level` of players to track their progress.

---

## 🔄 Auto-Updater & CI/CD

**MinelaunchedStats** ships with a smart auto-updater. On startup, it checks GitHub for new releases. If an update is found, it automatically downloads the `.jar` to the `plugins/update/` folder. The update is applied seamlessly on the next server restart.

The repository also features **GitHub Actions CI/CD**. Pushing a tag like `v1.0.1` automatically compiles the code and publishes a new Release.

---

## 👨‍💻 Authors

Developed with ❤️ by:
- **NeXoS_20**
- **Minelaunched**
