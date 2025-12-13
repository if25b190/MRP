package me.duong.mrp.controller;

import me.duong.mrp.entity.Rating;
import me.duong.mrp.presentation.*;
import me.duong.mrp.service.RatingService;
import me.duong.mrp.utils.Injector;
import me.duong.mrp.utils.parser.DtoReader;
import me.duong.mrp.utils.parser.DtoWriter;
import me.duong.mrp.utils.parser.Guards;

public class RatingController {
    @Controller(path = "/api/media/:id/rate", method = Method.POST)
    public static void rateMedia(Request request) {
        var mediaId = Guards.verifyWildcardInt(request, "id");
        if (mediaId == -1) return;
        var dto = DtoReader.readJson(request.body(), Rating.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var rating = dto.get();
        rating.setMediaId(mediaId);
        rating.setUserId(request.userId());
        var service = Injector.INSTANCE.resolve(RatingService.class);
        var result = service.createRating(rating);
        if (result.isPresent()) {
            String response = DtoWriter.writeJson(result.get());
            Responders.sendResponse(request, HttpStatusCode.CREATED, response);
        } else {
            Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
        }
    }

    @Controller(path = "/api/ratings/:id/like", method = Method.POST)
    public static void likeRatings(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(RatingService.class);
        var result = service.likeRating(id, request.userId());
        Responders.sendResponse(request, result ? HttpStatusCode.OK : HttpStatusCode.BAD_REQUEST);
    }

    @Controller(path = "/api/ratings/:id", method = Method.PUT)
    public static void updateRating(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var dto = DtoReader.readJson(request.body(), Rating.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var rating = dto.get();
        rating.setId(id);
        rating.setUserId(request.userId());
        var service = Injector.INSTANCE.resolve(RatingService.class);
        var result = service.updateRating(rating);
        String response = DtoWriter.writeJson(result);
        Responders.sendResponse(request, HttpStatusCode.OK, response);
    }

    @Controller(path = "/api/ratings/:id", method = Method.DELETE)
    public static void deleteRating(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(RatingService.class);
        var result = service.deleteRating(id, request.userId());
        Responders.sendResponse(request, result ? HttpStatusCode.NO_CONTENT : HttpStatusCode.NOT_FOUND);
    }

    @Controller(path = "/api/ratings/:id/confirm", method = Method.POST)
    public static void confirmRating(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(RatingService.class);
        var result = service.confirmRatingComment(id, request.userId());
        Responders.sendResponse(request, result ? HttpStatusCode.OK : HttpStatusCode.BAD_REQUEST);
    }
}
