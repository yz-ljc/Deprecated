package top.yzljc.atri.web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import top.yzljc.atri.Atri;
import top.yzljc.atri.config.Config;
import top.yzljc.atri.database.PlayerRecordData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Deprecated
public class WebServer {

    private final Atri plugin;
    private HttpServer server;
    private ExecutorService executor;
    private final Gson gson = new Gson();
    private static final int WORKER_THREADS = 1;

    public WebServer(Atri plugin) {
        this.plugin = plugin;
    }

    public void start() {
        int port = 8080; // 默认端口

        try {
            server = HttpServer.create(new InetSocketAddress(port), 8);
            executor = Executors.newFixedThreadPool(WORKER_THREADS);
            server.setExecutor(executor);

            server.createContext("/playerinfo", new PlayerInfoHandler());

            server.start();
            plugin.getLogger().info("HTTP API 服务器已启动，监听端口: " + port);
        } catch (IOException e) {
            plugin.getLogger().severe("无法启动 HTTP API 服务器！端口可能被占用: " + port);
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            plugin.getLogger().info("HTTP API 服务器已停止。");
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            executor = null;
        }
    }

    class PlayerInfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "{\"error\": \"Method Not Allowed\"}");
                    return;
                }

                Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
                String playerName = queryParams.get("name");

                if (playerName == null || playerName.isEmpty()) {
                    sendResponse(exchange, 400, "{\"error\": \"Missing 'name' parameter\"}");
                    return;
                }

                PlayerRecordData data = plugin.getPlayerDataManager().getPlayerDataByName(playerName);

                if (data == null) {
                    sendResponse(exchange, 404, "{\"error\": \"Player not found\"}");
                } else {
                    String json = gson.toJson(data);
                    sendResponse(exchange, 200, json);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("HTTP /playerinfo 处理异常: " + e.getMessage());
                try {
                    sendResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
                } catch (IOException ignored) { }
            } finally {
                exchange.close();
            }
        }
    }

    // 辅助：发送响应
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // 辅助：解析查询参数
    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}
