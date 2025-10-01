package me.duong.mrp.controller;

import me.duong.mrp.entity.User;
import me.duong.mrp.service.UserService;
import me.duong.mrp.presentation.Controller;
import me.duong.mrp.presentation.Method;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;
import me.duong.mrp.utils.parser.DtoParser;
import me.duong.mrp.utils.parser.Guards;

public class UserController {
    @Controller(path = "/api/users/register", method = Method.POST, authRequired = false)
    public static void register(Request request) {
        var dto = DtoParser.parseJson(request.body(), User.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        var service = new UserService();
        var result = service.registerUser(login);
        if (result.isPresent()) {
            Responders.sendResponse(request, 201);
        } else {
            Responders.sendResponse(request, 400);
        }
    }

    @Controller(path = "/api/users/login", method = Method.POST, authRequired = false)
    public static void login(Request request) {
        var dto = DtoParser.parseJson(request.body(), User.class);
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

    @Controller(path = "/api/users/:id/profile")
    public static void getUserProfile(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = new UserService();
        var result = service.getUserById(id);
        if (result.isPresent()) {
            var response = DtoParser.toJson(result.get());
            Responders.sendResponse(request, 200, response);
        } else {
            Responders.sendResponse(request, 404);
        }
    }

    @Controller(path = "/api/users/profile", method = Method.PUT)
    public static void updateUserProfile(Request request) {
        var dto = DtoParser.parseJson(request.body(), User.class);
        if (dto.isEmpty()) {
            Responders.sendResponse(request, 400);
            return;
        }
        var user = dto.get();
        user.setId(request.userId());
        var service = new UserService();
        var result = service.updateUser(dto.get());
        if (result.isPresent()) {
            var response = DtoParser.toJson(result.get());
            Responders.sendResponse(request, 200, response);
        } else {
            Responders.sendResponse(request, 400);
        }
    }

    @Controller(path = "/api/users/:id/ratings")
    public static void getUserRatings(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = new UserService();
        var result = service.getUserRatingHistory(id, request.userId());
        var response = DtoParser.toJson(result);
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/users/:id/favorites")
    public static void getUserFavorites(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = new UserService();
        var result = service.getUserFavorites(id, request.userId());
        var response = DtoParser.toJson(result);
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/users/:id/recommendations")
    public static void getUserRecommendations(Request request) {
        var type = request.query().get("type");
    }
}
