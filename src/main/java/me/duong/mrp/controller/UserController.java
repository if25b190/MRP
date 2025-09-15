package me.duong.mrp.controller;

import com.sun.net.httpserver.HttpExchange;
import me.duong.mrp.dto.LoginDto;
import me.duong.mrp.utils.*;

import java.io.IOException;
import java.io.OutputStream;

public class UserController {
    @Controller(path = "/")
    public static void process(Request request) throws IOException {
        String response = "Hello, this is a simple HTTP server response!";
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    @Controller(path = "/api/users/register", method = Method.POST)
    public static void register(Request request) throws IOException {
        var dto = DtoParser.parseJson(request.body(), LoginDto.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        String response = String.format("Hello, %s! You tried to register using the pw \"%s\"", login.getUsername(), login.getPassword());
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    @Controller(path = "/api/users/login", method = Method.POST)
    public static void login(Request request) throws IOException {
        var dto = DtoParser.parseJson(request.body(), LoginDto.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        String response = String.format("Hello, %s! You tried to login using the pw \"%s\"", login.getUsername(), login.getPassword());
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
