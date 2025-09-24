package me.duong.mrp.controller;

import me.duong.mrp.utils.http.Controller;
import me.duong.mrp.utils.http.Method;
import me.duong.mrp.utils.http.Request;

public class RatingController {
    @Controller(path = "/api/media/:id/rate", method = Method.POST)
    public static void rateMedia(Request request) {
    }

    @Controller(path = "/api/media/:id/like", method = Method.POST)
    public static void likeMedia(Request request) {
    }

    @Controller(path = "/api/ratings/:id", method = Method.PUT)
    public static void updateRating(Request request) {
    }

    @Controller(path = "/api/ratings/:id/confirm", method = Method.POST)
    public static void confirmRating(Request request) {
    }
}
