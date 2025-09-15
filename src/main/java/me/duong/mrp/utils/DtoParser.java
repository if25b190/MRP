package me.duong.mrp.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class DtoParser {
    public static <T> Optional<T> parseJson(String data, Class<T> valueType) {
        try {
            var mapper = new ObjectMapper();
            return Optional.of(mapper.readValue(data, valueType));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
}
