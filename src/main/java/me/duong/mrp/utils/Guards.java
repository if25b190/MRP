package me.duong.mrp.utils;

import com.sun.net.httpserver.HttpExchange;
import me.duong.mrp.Logger;
import me.duong.mrp.dto.BaseDto;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class Guards {
    public static boolean checkDto(Request request, Optional<? extends BaseDto> value) {
        if (value.isPresent() && value.get().validate()) {
            return true;
        }
        try (HttpExchange exchange = request.httpExchange()) {
            exchange.sendResponseHeaders(400, 0);
            OutputStream os = exchange.getResponseBody();
            os.close();
        } catch (IOException exception) {
            Logger.error("Guard Dto failed: %s", exception.getLocalizedMessage());
            return false;
        }
        return false;
    }
}
