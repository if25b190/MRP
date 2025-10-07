package me.duong.mrp.utils.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.OffsetDateTime;

public class DtoWriter {
    public static String writeJson(Object obj) {
        try {
            var mapper = getMapper();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
    private static ObjectWriter getMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer()));
        return mapper.writerWithView(Views.Public.class);
    }
}
