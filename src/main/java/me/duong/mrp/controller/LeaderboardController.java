package me.duong.mrp.controller;

import me.duong.mrp.presentation.Controller;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;
import me.duong.mrp.service.UserService;
import me.duong.mrp.utils.Injector;
import me.duong.mrp.utils.parser.DtoWriter;

public class LeaderboardController {
    @Controller(path = "/api/leaderboard")
    public static void getLeaderboard(Request request) {
        var service = Injector.INSTANCE.resolve(UserService.class);
        var result = service.getLeaderboard();
        var response = DtoWriter.writeJson(result);
        Responders.sendResponse(request, 200, response);
    }
}
