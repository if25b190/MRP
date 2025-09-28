package me.duong.mrp.controller;

import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.service.MediaService;
import me.duong.mrp.presentation.Controller;
import me.duong.mrp.presentation.Method;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;
import me.duong.mrp.utils.parser.DtoParser;
import me.duong.mrp.utils.parser.Guards;

public class MediaController {
    @Controller(path = "/api/media")
    public static void getAllMedia(Request request) {
        var filter = MediaFilter.fromQuery(request.query());
        var service = new MediaService();
        var result = service.getAllMedia(filter);
        String response = DtoParser.toJson(result);
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/media/:id")
    public static void getMediaById(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = new MediaService();
        var result = service.getMediaById(id);
        if (result.isPresent()) {
            String response = DtoParser.toJson(result.get());
            Responders.sendResponse(request, 200, response);
        } else {
            Responders.sendResponse(request, 404);
        }
    }

    @Controller(path = "/api/media", method = Method.POST)
    public static void createMedia(Request request) {
        var dto = DtoParser.parseJson(request.body(), Media.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var media = dto.get();
        media.setUserId(request.userId());
        var service = new MediaService();
        var result = service.createMedia(media);
        String response = DtoParser.toJson(result);
        Responders.sendResponse(request, 201, response);
    }

    @Controller(path = "/api/media/:id", method = Method.PUT)
    public static void updateMedia(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var dto = DtoParser.parseJson(request.body(), Media.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var media = dto.get();
        media.setId(id);
        media.setUserId(request.userId());
        var service = new MediaService();
        var result = service.updateMedia(media);
        if (result.isPresent()) {
            String response = DtoParser.toJson(result.get());
            Responders.sendResponse(request, 200, response);
        } else {
            Responders.sendResponse(request, 404);
        }
    }

    @Controller(path = "/api/media/:id", method = Method.DELETE)
    public static void deleteMedia(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = new MediaService();
        service.deleteMedia(id, request.userId());
        Responders.sendResponse(request, 204);
    }

    @Controller(path = "/api/media/:id/favorite", method = Method.POST)
    public static void markFavoriteMedia(Request request) {
        var mediaId = Guards.verifyWildcardInt(request, "id");
        if (mediaId == -1) return;
        var service = new MediaService();
        service.markMediaAsFavorite(request.userId(), mediaId);
        Responders.sendResponse(request, 200);
    }

    @Controller(path = "/api/media/:id/favorite", method = Method.DELETE)
    public static void unmarkFavoriteMedia(Request request) {
        var mediaId = Guards.verifyWildcardInt(request, "id");
        if (mediaId == -1) return;
        var service = new MediaService();
        service.unmarkMediaAsFavorite(request.userId(), mediaId);
        Responders.sendResponse(request, 200);
    }
}
