package com.minelaunched.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WebServer {

    private final MinelaunchedStatsPlugin plugin;
    private HttpServer server;
    private final int port;

    public WebServer(MinelaunchedStatsPlugin plugin, int port) {
        this.plugin = plugin;
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        String endpoint = plugin.getConfig().getString("web.endpoint", "/");
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }

        server.createContext(endpoint, new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                    return;
                }

                String responseStr;
                JsonObject jsonResponse;
                try {
                    // Fetch stats on the main server thread to avoid ConcurrentModificationExceptions
                    Future<JsonObject> future = Bukkit.getScheduler().callSyncMethod(plugin, new StatsCollector(plugin.getConfig()));
                    jsonResponse = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    plugin.getLogger().severe("Failed to generate stats: " + e.getMessage());
                    sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
                    return;
                }
                
                boolean prettyPrint = plugin.getConfig().getBoolean("web.pretty_print", false);
                Gson gson = prettyPrint ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
                responseStr = gson.toJson(jsonResponse);

                sendResponse(exchange, 200, responseStr);
            }
        });

        // Use a default executor (background thread)
        server.setExecutor(null);
        server.start();
        plugin.getLogger().info("Web server started on port " + port + " at endpoint " + endpoint);
    }
    
    private void sendResponse(HttpExchange exchange, int statusCode, String responseStr) throws IOException {
        byte[] responseBytes = responseStr.getBytes(StandardCharsets.UTF_8);

        String corsOrigin = plugin.getConfig().getString("web.cors_origin", "*");
        
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", corsOrigin);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            plugin.getLogger().info("Web server stopped.");
        }
    }
}
