package me.duong.mrp.utils;

import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;

public class Request {
    private final HttpExchange httpExchange;
    private final String path;
    private final Map<String, List<String>> query;
    private final String body;

    public Request(HttpExchange httpExchange, String path, Map<String, List<String>> query, String body) {
        this.httpExchange = httpExchange;
        this.path = path;
        this.query = query;
        this.body = body;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public String getPath() {
        return path;
    }

    public Map<String, List<String>> getQuery() {
        return query;
    }

    public String getBody() {
        return body;
    }
}
