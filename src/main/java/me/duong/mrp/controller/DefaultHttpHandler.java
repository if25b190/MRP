package me.duong.mrp.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.duong.mrp.Logger;
import me.duong.mrp.MRP;
import me.duong.mrp.utils.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        var result = MRP.controllers.entrySet()
                .stream()
                .filter(entry -> entry.getKey().method().name().equalsIgnoreCase(method) && entry.getKey().path().equals(path))
                .map(Map.Entry::getValue)
                .map(consumer -> {
                    consumer.accept(new Request(exchange, path, queryParameters, body));
                    return 0;
                }).findAny();
        if (result.isEmpty()) {
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().close();
        }
    }

    private Map<String, List<String>> parseQueryParameters(String query) {
        Map<String, List<String>> queryParameters = new HashMap<>();
        if (query != null) {
            for (String part : query.split("&")) {
                String[] data = part.split("=");
                Logger.info("QUERY: %s / %s", data[0], data[1]);
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
