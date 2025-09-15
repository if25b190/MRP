package me.duong.mrp.utils;

import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;

public record Request(HttpExchange httpExchange, String path, Map<String, List<String>> query, String body) {
}
