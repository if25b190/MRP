package me.duong.mrp.utils.parser;

import me.duong.mrp.model.BaseValidator;
import me.duong.mrp.utils.http.Request;
import me.duong.mrp.utils.http.Responders;

import java.util.Optional;

public class Guards {
    public static boolean checkDto(Request request, Optional<? extends BaseValidator> value) {
        if (value.isPresent() && value.get().validate()) {
            return true;
        }
        Responders.sendResponse(request, 400);
        return false;
    }
}
