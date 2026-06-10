import os

with open("src/main/java/com/minelaunched/stats/StatsCollector.java", "r") as f:
    stats_lines = f.readlines()

server_hooks_logic = "".join(stats_lines[165:180])
player_hooks_logic = "".join(stats_lines[301:617])

# Keep the parts of StatsCollector
new_stats = "".join(stats_lines[:165]) + """            com.minelaunched.stats.hooks.HookManager.appendServerHooks(server, config);
""" + "".join(stats_lines[180:301]) + """                com.minelaunched.stats.hooks.HookManager.appendPlayerHooks(po, p, config);
""" + "".join(stats_lines[617:])

with open("src/main/java/com/minelaunched/stats/StatsCollector.java", "w") as f:
    f.write(new_stats)


with open("src/main/java/com/minelaunched/stats/MinelaunchedStatsPlugin.java", "r") as f:
    plugin_lines = f.readlines()

init_hooks_logic = "".join(plugin_lines[26:73])

new_plugin = "".join(plugin_lines[:26]) + """        com.minelaunched.stats.hooks.HookManager.initAll();
""" + "".join(plugin_lines[73:])

with open("src/main/java/com/minelaunched/stats/MinelaunchedStatsPlugin.java", "w") as f:
    f.write(new_plugin)

hook_manager = f"""package com.minelaunched.stats.hooks;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class HookManager {{

    public static void initAll() {{
{init_hooks_logic}
    }}

    public static void appendServerHooks(JsonObject server, FileConfiguration config) {{
{server_hooks_logic}
    }}

    public static void appendPlayerHooks(JsonObject po, Player p, FileConfiguration config) {{
{player_hooks_logic}
    }}
}}
"""

with open("src/main/java/com/minelaunched/stats/hooks/HookManager.java", "w") as f:
    f.write(hook_manager)

print("Done organizing!")
