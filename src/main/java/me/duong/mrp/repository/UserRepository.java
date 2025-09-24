package me.duong.mrp.repository;

import me.duong.mrp.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {
    private final DbSession session;

    public UserRepository(DbSession session) {
        this.session = session;
    }

    public Optional<User> findUserById(int id) throws SQLException {
        var statement = session.prepareStatement("""
                SELECT * FROM users where id = ?
                """);
        statement.setInt(1, id);
        var result = statement.executeQuery();
        return getUserFromResult(result);
    }

    public Optional<User> findUserByUsername(String username) throws SQLException {
        var statement = session.prepareStatement("""
                SELECT * FROM users where username = ?
                """);
        statement.setString(1, username);
        var result = statement.executeQuery();
        return getUserFromResult(result);
    }

    private Optional<User> getUserFromResult(ResultSet result) throws SQLException {
        if (result.next()) {
            var user = new User(
                    result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4)
            );
            return Optional.of(user);
        }
        return Optional.empty();
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
