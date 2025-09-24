package me.duong.mrp.utils.http;

public record Mapping(Method method, String path, boolean authRequired) {
}
