package me.duong.mrp.repository;

import me.duong.mrp.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DbConnection {
    INSTANCE;

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mrp",
                    "mrp",
                    "mrp"
            );
        } catch (SQLException exception) {
            Logger.error("Failed to create connection: %s", exception.getMessage());
            throw new DbException("Failed to create connection", exception);
        }
    }
}
