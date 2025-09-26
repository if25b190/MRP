package me.duong.mrp.presentation;

import com.sun.net.httpserver.HttpExchange;
import me.duong.mrp.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Responders {
    public static void sendResponse(Request request, int statusCode) {
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.getResponseHeaders().add("Cache-Control", "nocache");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, 0);
            exchange.getResponseBody().close();
        } catch (IOException exception) {
            Logger.error("Responder failed: %s", exception.getMessage());
        }
    }

    public static void sendResponse(Request request, int statusCode, String response) {
        sendResponse(request, statusCode, response, ContentType.JSON);
    }

    public static void sendResponse(Request request, int statusCode, String response, ContentType type) {
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.getResponseHeaders().add("Cache-Control", "nocache");
            exchange.getResponseHeaders().add("Content-Type", type.type);
            exchange.sendResponseHeaders(statusCode, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException exception) {
            Logger.error("Responder failed: %s", exception.getMessage());
        }
    }
}
