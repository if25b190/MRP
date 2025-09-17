package me.duong.mrp.repository;

public class DbException extends RuntimeException {
    public DbException(String message) {
        super(message);
    }
}
