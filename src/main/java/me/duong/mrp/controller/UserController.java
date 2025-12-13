package me.duong.mrp.controller;

import me.duong.mrp.entity.User;
import me.duong.mrp.presentation.*;
import me.duong.mrp.service.UserService;
import me.duong.mrp.utils.Injector;
import me.duong.mrp.utils.Logger;
import me.duong.mrp.utils.parser.DtoReader;
import me.duong.mrp.utils.parser.DtoWriter;
import me.duong.mrp.utils.parser.Guards;

public class UserController {
    @Controller(path = "/api/users/register", method = Method.POST, authRequired = false)
    public static void register(Request request) {
        var dto = DtoReader.readJson(request.body(), User.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.registerUser(login);
        if (result.isPresent()) {
            Responders.sendResponse(request, HttpStatusCode.CREATED);
        } else {
            Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
        }
    }

    @Controller(path = "/api/users/login", method = Method.POST, authRequired = false)
    public static void login(Request request) {
        var dto = DtoReader.readJson(request.body(), User.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var login = dto.get();
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.loginUser(login);
        if (result.isPresent()) {
            Responders.sendResponse(request, HttpStatusCode.OK, String.format("{\"token\": \"%s\"}", result.get()));
        } else {
            Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
        }
    }

    @Controller(path = "/api/users/:id/profile")
    public static void getUserProfile(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.getUserById(id);
        if (result.isPresent()) {
            var response = DtoWriter.writeJson(result.get());
            Responders.sendResponse(request, HttpStatusCode.OK, response);
        } else {
            Responders.sendResponse(request, HttpStatusCode.NOT_FOUND);
        }
    }

    @Controller(path = "/api/users/profile", method = Method.PUT)
    public static void updateUserProfile(Request request) {
        var dto = DtoReader.readJson(request.body(), User.class);
        if (dto.isEmpty()) {
            Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
            return;
        }
        var user = dto.get();
        user.setId(request.userId());
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.updateUser(dto.get());
        if (result.isPresent()) {
            var response = DtoWriter.writeJson(result.get());
            Responders.sendResponse(request, HttpStatusCode.OK, response);
        } else {
            Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
        }
    }

    @Controller(path = "/api/users/:id/ratings")
    public static void getUserRatings(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.getUserRatingHistory(id, request.userId());
        var response = DtoWriter.writeJson(result);
        Responders.sendResponse(request, HttpStatusCode.OK, response);
    }

    @Controller(path = "/api/users/:id/favorites")
    public static void getUserFavorites(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.getUserFavorites(id, request.userId());
        var response = DtoWriter.writeJson(result);
        Responders.sendResponse(request, HttpStatusCode.OK, response);
    }

    @Controller(path = "/api/users/:id/recommendations")
    public static void getUserRecommendations(Request request) {
        var type = request.query().get("type");
        if (type == null || type.isEmpty()) {
            Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
            return;
        }
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.getUserRecommendations(request.userId(), type.getFirst());
        var response = DtoWriter.writeJson(result);
        Responders.sendResponse(request, HttpStatusCode.OK, response);
    }
}
