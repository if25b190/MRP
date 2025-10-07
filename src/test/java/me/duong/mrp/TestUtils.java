package me.duong.mrp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.duong.mrp.utils.parser.OffsetDateTimeSerializer;
import me.duong.mrp.utils.parser.Views;

import java.net.URI;
import java.time.OffsetDateTime;

public class TestUtils {
    public static URI getUri(String path) {
        return URI.create(RestServer.INSTANCE.pathToUrl(path));
    }
    public static ObjectWriter publicWriter() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer()));
        return mapper.writerWithView(Views.Public.class);
    }
    public static ObjectWriter testingWriter() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer()));
        return mapper.writerWithView(Views.Testing.class);
    }
}
