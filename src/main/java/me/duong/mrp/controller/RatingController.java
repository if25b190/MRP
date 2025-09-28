package me.duong.mrp.controller;

import me.duong.mrp.entity.Rating;
import me.duong.mrp.presentation.Controller;
import me.duong.mrp.presentation.Method;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;
import me.duong.mrp.service.RatingService;
import me.duong.mrp.utils.parser.DtoParser;
import me.duong.mrp.utils.parser.Guards;

public class RatingController {
    @Controller(path = "/api/media/:id/rate", method = Method.POST)
    public static void rateMedia(Request request) {
        var mediaId = Guards.verifyWildcardInt(request, "id");
        if (mediaId == -1) return;
        var dto = DtoParser.parseJson(request.body(), Rating.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var rating = dto.get();
        rating.setMediaId(mediaId);
        rating.setUserId(request.userId());
        var service = new RatingService();
        var result = service.createRating(rating);
        String response = DtoParser.toJson(result);
        Responders.sendResponse(request, 201, response);
    }

    @Controller(path = "/api/media/:id/like", method = Method.POST)
    public static void likeMedia(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = new RatingService();
        var result = service.likeRating(request.userId(), id);
        String response = DtoParser.toJson(result);
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/ratings/:id", method = Method.PUT)
    public static void updateRating(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var dto = DtoParser.parseJson(request.body(), Rating.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var rating = dto.get();
        rating.setId(id);
        rating.setUserId(request.userId());
        var service = new RatingService();
        var result = service.createRating(rating);
        String response = DtoParser.toJson(result);
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/ratings/:id/confirm", method = Method.POST)
    public static void confirmRating(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = new RatingService();
        service.confirmRatingComment(request.userId(), id);
        Responders.sendResponse(request, 200);
    }
}
