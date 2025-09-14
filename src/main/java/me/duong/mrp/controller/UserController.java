package me.duong.mrp.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.duong.mrp.Logger;
import me.duong.mrp.MRP;
import me.duong.mrp.utils.Controller;
import me.duong.mrp.utils.Request;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Map<String, List<String>> queryParameters = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
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
        String body = new String(exchange.getRequestBody().readAllBytes());
        Logger.info("REQUEST: %s / %s", path, query);
        MRP.controllers.entrySet()
                .stream()
                .filter(entry -> entry.getKey().method().name().equalsIgnoreCase(method) && entry.getKey().path().equals(path))
                .map(Map.Entry::getValue)
                .forEach(consumer -> consumer.accept(new Request(exchange, path, queryParameters, body)));
    }

    @Controller(path = "/")
    public static void process(Request request) throws IOException {
        String response = "Hello, this is a simple HTTP server response!";
        request.getHttpExchange().sendResponseHeaders(200, response.length());
        OutputStream os = request.getHttpExchange().getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
