package me.duong.mrp.controller;

import me.duong.mrp.utils.http.Controller;
import me.duong.mrp.utils.http.Method;
import me.duong.mrp.utils.http.Request;

public class MediaController {
    @Controller(path = "/api/media")
    public static void getMedia(Request request) {
        var title = request.query().get("title");
        var genre = request.query().get("genre");
        var mediaType = request.query().get("mediaType");
        var releaseYear = request.query().get("releaseYear");
        var ageRestriction = request.query().get("ageRestriction");
        var rating = request.query().get("rating");
        var sortBy = request.query().get("sortBy");
    }

    @Controller(path = "/api/media/:id")
    public static void getMediaById(Request request) {
    }

    @Controller(path = "/api/media", method = Method.POST)
    public static void createMedia(Request request) {
    }

    @Controller(path = "/api/media/:id", method = Method.PUT)
    public static void updateMedia(Request request) {
    }

    @Controller(path = "/api/media/:id", method = Method.DELETE)
    public static void deleteMedia(Request request) {
    }

    @Controller(path = "/api/media/:id/favorite", method = Method.POST)
    public static void markFavoriteMedia(Request request) {
    }

    @Controller(path = "/api/media/:id/favorite", method = Method.DELETE)
    public static void unmarkFavoriteMedia(Request request) {
    }
}
