package me.duong.mrp.presentation;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.duong.mrp.ControllerStore;
import me.duong.mrp.Logger;
import me.duong.mrp.utils.security.TokenStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DefaultHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(200, 0);
            exchange.getResponseBody().close();
            return;
        }
        String path = exchange.getRequestURI().getPath();
        Map<String, List<String>> queryParameters = parseQueryParameters(exchange.getRequestURI().getQuery());
        String body = new String(exchange.getRequestBody().readAllBytes());
        Logger.info("REQUEST: %s - %s", method, path);
        var result = filteredControllers(method, path)
                .flatMap(entry -> {
                    var userId = checkAuth(exchange.getRequestHeaders());
                    if (entry.getKey().authRequired() && userId == -1) {
                        return Stream.empty();
                    }
                    var wildcards = extractWildcards(entry.getKey().path(), path);
                    entry.getValue().accept(new Request(exchange, path, queryParameters, wildcards, body, userId));
                    return Stream.of(0);
                }).findAny();
        if (result.isEmpty()) {
            var authRequired = filteredControllers(method, path).anyMatch(entry ->
                    entry.getKey().authRequired());
            exchange.sendResponseHeaders(authRequired ? 401 : 404, 0);
            exchange.getResponseBody().close();
        }
    }

    private Stream<Map.Entry<Mapping, Consumer<Request>>> filteredControllers(String method, String path) {
        return ControllerStore.INSTANCE.getControllers().entrySet()
                .stream()
                .filter(entry ->
                        entry.getKey().method().name().equalsIgnoreCase(method) &&
                                matchesPath(entry.getKey().path(), path));
    }

    private int checkAuth(Headers headers) {
        return headers.containsKey("Authorization") ?
                TokenStore.INSTANCE.verifyToken(headers.get("Authorization").getFirst()) : -1;
    }

    private boolean matchesPath(String targetPath, String requestPath) {
        return requestPath.matches(targetPath.replaceAll("/:[A-Za-z0-9]+", "/(.+)"));
    }

    private Map<String, String> extractWildcards(String targetPath, String requestPath) {
        Map<String, String> wildcards = new HashMap<>();
        if (!targetPath.matches(".*/:[A-Za-z0-9]+.*")) {
            return wildcards;
        }
        try {
            var pattern = Pattern.compile(targetPath.replaceAll("/:[A-Za-z0-9]+", "/(.+)"));
            var matcher = pattern.matcher(requestPath);
            var wildcardNames = Pattern
                    .compile(targetPath.replaceAll("/:[A-Za-z0-9]+", "/:(.+)"))
                    .matcher(targetPath);
            int i = 1;
            while (matcher.find() && wildcardNames.find()) {
                wildcards.put(wildcardNames.group(i), matcher.group(i));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wildcards;
    }

    private Map<String, List<String>> parseQueryParameters(String query) {
        Map<String, List<String>> queryParameters = new HashMap<>();
        if (query != null) {
            for (String part : query.split("&")) {
                String[] data = part.split("=");
                queryParameters.compute(data[0], (key, value) -> {
                    List<String> temp = value != null ? value : new ArrayList<>();
                    temp.add(data[1]);
                    return temp;
                });
            }
        }
        return queryParameters;
    }
}
