package me.duong.mrp.utils.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.OffsetDateTime;
import java.util.Optional;

public class DtoReader {
    public static <T> Optional<T> readJson(String data, Class<T> valueType) {
        try {
            var mapper = getMapper();
            return Optional.of(mapper.readValue(data, valueType));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }
    private static ObjectMapper getMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer()));
        return mapper;
    }
}
