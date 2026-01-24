package me.duong.mrp;

import me.duong.mrp.repository.DbException;
import me.duong.mrp.utils.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum TestDbConnection {
    INSTANCE;

    public Connection getConnection(String url, String user, String password) {
        try {
            return DriverManager.getConnection(
                    url,
                    user,
                    password
            );
        } catch (SQLException exception) {
            Logger.error("Failed to create connection: %s", exception.getMessage());
            throw new DbException("Failed to create connection", exception);
        }
    }
}
