package me.duong.mrp.utils.http;

import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;

public record Request(HttpExchange httpExchange, String path, Map<String, List<String>> query,
                      Map<String, String> wildcards, String body, int userId) {
}
