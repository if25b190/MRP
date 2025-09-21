package me.duong.mrp.repository;

import me.duong.mrp.model.User;

import java.sql.SQLException;

public class UserRepository {
    private final DbSession session;

    public UserRepository(DbSession session) {
        this.session = session;
    }

    public User findUser() {
        return null;
    }

    public boolean insertUser(User user) throws SQLException {
        var statement = session.prepareStatement("""
                INSERT INTO users (username, password, salt) VALUES (?, ?, ?)
                """);
        statement.setString(1, user.username());
        statement.setString(2, user.password());
        statement.setString(3, user.salt());
        var result = statement.executeUpdate();
        return result == 1;
    }
}
