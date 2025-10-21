package me.duong.mrp.controller;

import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.service.MediaService;
import me.duong.mrp.presentation.Controller;
import me.duong.mrp.presentation.Method;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;
import me.duong.mrp.utils.Injector;
import me.duong.mrp.utils.parser.DtoReader;
import me.duong.mrp.utils.parser.DtoWriter;
import me.duong.mrp.utils.parser.Guards;

public class MediaController {
    @Controller(path = "/api/media")
    public static void getAllMedia(Request request) {
        var filter = MediaFilter.fromQuery(request.query());
        var service = Injector.INSTANCE.resolve(MediaService.class);
        var result = service.getAllMedia(filter, request.userId());
        String response = DtoWriter.writeJson(result);
        Responders.sendResponse(request, 200, response);
    }

    @Controller(path = "/api/media/:id")
    public static void getMediaById(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(MediaService.class);
        var result = service.getMediaById(id, request.userId());
        if (result.isPresent()) {
            String response = DtoWriter.writeJson(result.get());
            Responders.sendResponse(request, 200, response);
        } else {
            Responders.sendResponse(request, 404);
        }
    }

    @Controller(path = "/api/media", method = Method.POST)
    public static void createMedia(Request request) {
        var dto = DtoReader.readJson(request.body(), Media.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var media = dto.get();
        media.setUserId(request.userId());
        var service = Injector.INSTANCE.resolve(MediaService.class);
        var result = service.createMedia(media);
        String response = DtoWriter.writeJson(result);
        Responders.sendResponse(request, 201, response);
    }

    @Controller(path = "/api/media/:id", method = Method.PUT)
    public static void updateMedia(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var dto = DtoReader.readJson(request.body(), Media.class);
        if (!Guards.checkDto(request, dto)) {
            return;
        }
        var media = dto.get();
        media.setId(id);
        media.setUserId(request.userId());
        var service = Injector.INSTANCE.resolve(MediaService.class);
        var result = service.updateMedia(media);
        if (result.isPresent()) {
            String response = DtoWriter.writeJson(result.get());
            Responders.sendResponse(request, 200, response);
        } else {
            Responders.sendResponse(request, 404);
        }
    }

    @Controller(path = "/api/media/:id", method = Method.DELETE)
    public static void deleteMedia(Request request) {
        var id = Guards.verifyWildcardInt(request, "id");
        if (id == -1) return;
        var service = Injector.INSTANCE.resolve(MediaService.class);
        var result = service.deleteMedia(id, request.userId());
        Responders.sendResponse(request, result ? 204 : 404);
    }

    @Controller(path = "/api/media/:id/favorite", method = Method.POST)
    public static void markFavoriteMedia(Request request) {
        var mediaId = Guards.verifyWildcardInt(request, "id");
        if (mediaId == -1) return;
        var service = Injector.INSTANCE.resolve(MediaService.class);
        var result = service.markMediaAsFavorite(request.userId(), mediaId);
        Responders.sendResponse(request, result ? 200 : 400);
    }

    @Controller(path = "/api/media/:id/favorite", method = Method.DELETE)
    public static void unmarkFavoriteMedia(Request request) {
        var mediaId = Guards.verifyWildcardInt(request, "id");
        if (mediaId == -1) return;
        var service = Injector.INSTANCE.resolve(MediaService.class);
        var result = service.unmarkMediaAsFavorite(request.userId(), mediaId);
        Responders.sendResponse(request, result ? 204 : 400);
    }
}
