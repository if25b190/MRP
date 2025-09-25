package me.duong.mrp.repository;

import me.duong.mrp.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Consumer;

public class DbSession implements AutoCloseable {

    private Connection connection;

    public DbSession() {
        createConnection();
        disableAutoCommit();
    }

    public PreparedStatement prepareStatement(String sql) {
        if (connection == null) throw new DbException("No active connection");
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException exception) {
            throw new DbException(exception.getMessage());
        }
    }

    public void commit() {
        if (connection == null) return;
        try {
            connection.commit();
        } catch (SQLException exception) {
            Logger.error("Failed to commit: %s", exception.getMessage());
        }
    }

    public void rollback() {
        if (connection == null) return;
        try {
            connection.rollback();
        } catch (SQLException exception) {
            Logger.error("Failed to rollback: %s", exception.getMessage());
        }
    }

    private void createConnection() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/mrp",
                    "mrp",
                    "mrp"
            );
        } catch (SQLException exception) {
            Logger.error("Failed to create connection: %s", exception.getMessage());
        }
    }

    private void disableAutoCommit() {
        if (connection == null) return;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException exception) {
            Logger.error("Auto-Commit not disabled: %s", exception.getMessage());
        }
    }

    private void dispose() {
        if (connection == null) return;
        try {
            connection.close();
            connection = null;
        } catch (SQLException exception) {
            Logger.error("Failed to close connection: %s", exception.getMessage());
        }
    }

    @Override
    public void close() {
        dispose();
    }

    public static void execute(DbSession session, Consumer<Void> func, Consumer<Exception> callbackError) {
        try (session) {
            func.accept(null);
            session.commit();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            callbackError.accept(exception);
        }
    }
}
