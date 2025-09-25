package me.duong.mrp.repository;

import me.duong.mrp.Logger;
import me.duong.mrp.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository extends BaseRepository<User> {
    public UserRepository(DbSession session) {
        super(session);
    }

    public Optional<User> findUserById(int id) {
        return super.findBy("""
                SELECT * FROM users where id = ?
                """,
                prepared -> prepared.setInt(1, id),
                UserRepository::mapUser);
    }

    public Optional<User> findUserByUsername(String username) {
        return super.findBy("""
                SELECT * FROM users where username = ?
                """,
                prepared -> prepared.setString(1, username),
                UserRepository::mapUser);
    }

    public User insertUser(User user) {
        return super.insert(user, """
                INSERT INTO users (username, password, salt) VALUES (?, ?, ?)
                """, prepared -> {
            prepared.setString(1, user.getUsername());
            prepared.setString(2, user.getPassword());
            prepared.setString(3, user.getSalt());
        });
    }

    public User updateUser(User user) {
        super.update("""
                UPDATE users SET username = ?, favoriteGenre = ? WHERE id = ?
                """, prepared -> {
            prepared.setString(1, user.getUsername());
            prepared.setString(2, user.getFavoriteGenre());
            prepared.setObject(3, user.getId());
        });
        return user;
    }

    public void delete(User user) {
        super.delete(user, """
                DELETE FROM users WHERE id = ?
                """);
    }

    private static User mapUser(ResultSet result) {
        try {
            return new User()
                    .setId(result.getInt(1))
                    .setUsername(result.getString(2))
                    .setPassword(result.getString(3))
                    .setSalt(result.getString(4))
                    .setFavoriteGenre(result.getString(5));
        } catch (SQLException exception) {
            Logger.error("Failed to map user: %s", exception.getMessage());
            throw new DbException("Failed to map user", exception);
        }
    }
}
