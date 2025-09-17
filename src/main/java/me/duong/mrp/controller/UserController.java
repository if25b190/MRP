package me.duong.mrp.controller;

import me.duong.mrp.dto.LoginDto;
import me.duong.mrp.service.UserService;
import me.duong.mrp.utils.*;

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
            Responders.sendResponse(request, 200, response);
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
        String response = String.format("Hello, %s! You tried to login using the pw \"%s\"", login.getUsername(), login.getPassword());
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/users/:id/profile")
    public static void getUserProfile(Request request) {
        Responders.sendResponse(request, 200, request.wildcards().get("id"));
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
