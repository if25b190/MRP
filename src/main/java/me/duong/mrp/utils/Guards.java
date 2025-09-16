package me.duong.mrp.utils;

import me.duong.mrp.dto.BaseDto;

import java.util.Optional;

public class Guards {
    public static boolean checkDto(Request request, Optional<? extends BaseDto> value) {
        if (value.isPresent() && value.get().validate()) {
            return true;
        }
        Responders.sendResponse(request, 400);
        return false;
    }
}
