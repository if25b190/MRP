package me.duong.mrp.controller;

import me.duong.mrp.dto.LoginDto;
import me.duong.mrp.service.UserService;
import me.duong.mrp.utils.http.Controller;
import me.duong.mrp.utils.http.Method;
import me.duong.mrp.utils.http.Request;
import me.duong.mrp.utils.http.Responders;
import me.duong.mrp.utils.parser.DtoParser;
import me.duong.mrp.utils.parser.Guards;

public class UserController {
    @Controller(path = "/api/users/register", method = Method.POST)
    public static void register(Request request) {
        var dto = DtoParser.parseJson(request.body(), LoginDto.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        var service = new UserService();
        var result = service.registerUser(login);
        String response = String.format("Hello, %s! You tried to register using the pw \"%s\"", login.getUsername(), login.getPassword());
        if (result.isPresent()) {
            Responders.sendResponse(request, 201, response);
        } else {
            Responders.sendResponse(request, 500);
        }
    }

    @Controller(path = "/api/users/login", method = Method.POST)
    public static void login(Request request) {
        var dto = DtoParser.parseJson(request.body(), LoginDto.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        var service = new UserService();
        var result = service.loginUser(login);
        if (result.isPresent()) {
            Responders.sendResponse(request, 200, String.format("{\"token\": \"%s\"}", result.get()));
        } else {
            Responders.sendResponse(request, 400);
        }
    }

    @Controller(path = "/api/users/:id/profile", authRequired = true)
    public static void getUserProfile(Request request) {
        Responders.sendResponse(request, 200, request.wildcards().get("id") + request.userId());
    }

    @Controller(path = "/api/users/:id/profile", method = Method.PUT)
    public static void updateUserProfile(Request request) {
    }

    @Controller(path = "/api/users/:id/ratings")
    public static void getUserRatings(Request request) {
    }

    @Controller(path = "/api/users/:id/favorites")
    public static void getUserFavorites(Request request) {
    }

    @Controller(path = "/api/users/:id/recommendations")
    public static void getUserRecommendations(Request request) {
        var type = request.query().get("type");
    }
}
