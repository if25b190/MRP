package me.duong.mrp.utils.parser;

import me.duong.mrp.model.BaseValidator;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;

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
