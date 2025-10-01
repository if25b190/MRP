package me.duong.mrp.utils.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.OffsetDateTime;
import java.util.Optional;

public class DtoParser {
    public static <T> Optional<T> parseJson(String data, Class<T> valueType) {
        try {
            var mapper = getMapper();
            return Optional.of(mapper.readValue(data, valueType));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
    public static String toJson(Object obj) {
        try {
            var mapper = getMapper();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
    private static ObjectMapper getMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer()));
        return mapper;
    }
}
