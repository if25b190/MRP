package me.duong.mrp.utils.parser;

import me.duong.mrp.model.BaseValidator;
import me.duong.mrp.presentation.HttpStatusCode;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;

import java.util.Optional;

public class Guards {
    public static boolean checkDto(Request request, Optional<? extends BaseValidator> value) {
        if (value.isPresent() && value.get().validate()) {
            return true;
        }
        Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
        return false;
    }
    public static int verifyWildcardInt(Request request, String key) {
        if (request.wildcards().get(key).matches("[0-9]+")) {
            return Integer.parseInt(request.wildcards().get(key));
        }
        Responders.sendResponse(request, HttpStatusCode.BAD_REQUEST);
        return -1;
    }
}
