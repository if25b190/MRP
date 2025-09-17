package me.duong.mrp.model;

public record User(int id, String username, String password, String salt) {
}
