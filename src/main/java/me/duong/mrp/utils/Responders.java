package me.duong.mrp.utils;

import com.sun.net.httpserver.HttpExchange;
import me.duong.mrp.Logger;

import java.io.IOException;
import java.io.OutputStream;

public class Responders {
    public static void sendResponse(Request request, int statusCode) {
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.sendResponseHeaders(statusCode, 0);
            exchange.getResponseBody().close();
        } catch (IOException exception) {
            Logger.error("Responder failed: %s", exception.getLocalizedMessage());
        }
    }
    public static void sendResponse(Request request, int statusCode, String response) {
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.sendResponseHeaders(statusCode, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } catch (IOException exception) {
            Logger.error("Responder failed: %s", exception.getLocalizedMessage());
        }
    }
}
