package me.duong.mrp.controller;

import me.duong.mrp.dto.LoginDto;
import me.duong.mrp.utils.*;

import java.io.IOException;

public class UserController {
    @Controller(path = "/")
    public static void process(Request request) {
        String response = "Hello, this is a simple HTTP server response!";
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/users/register", method = Method.POST)
    public static void register(Request request) throws IOException {
        var dto = DtoParser.parseJson(request.body(), LoginDto.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        String response = String.format("Hello, %s! You tried to register using the pw \"%s\"", login.getUsername(), login.getPassword());
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/users/login", method = Method.POST)
    public static void login(Request request) throws IOException {
        var dto = DtoParser.parseJson(request.body(), LoginDto.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        String response = String.format("Hello, %s! You tried to login using the pw \"%s\"", login.getUsername(), login.getPassword());
        Responders.sendResponse(request, 200, response);
    }
}
