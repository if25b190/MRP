package me.duong.mrp.repository.impl;

import me.duong.mrp.repository.BaseRepository;
import me.duong.mrp.repository.DbException;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.UserRepository;
import me.duong.mrp.utils.Logger;
import me.duong.mrp.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {
    public UserRepositoryImpl(DbSession session) {
        super(session);
    }

    @Override
    public Optional<User> findUserById(int id) {
        return super.findBy("""
                        SELECT * FROM users WHERE id = ?
                        """,
                prepared -> prepared.setInt(1, id),
                UserRepositoryImpl::mapUser);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return super.findBy("""
                        SELECT * FROM users WHERE username = ?
                        """,
                prepared -> prepared.setString(1, username),
                UserRepositoryImpl::mapUser);
    }

    @Override
    public User insertUser(User user) {
        return super.insert(user, """
                INSERT INTO users (username, password, salt) VALUES (?, ?, ?)
                """, prepared -> {
            prepared.setString(1, user.getUsername());
            prepared.setString(2, user.getPassword());
            prepared.setString(3, user.getSalt());
        });
    }

    @Override
    public User updateUser(User user) {
        var fields = new ArrayList<String>();
        if (user.getUsername() != null) fields.add("username = ?");
        if (user.getEmail() != null) fields.add("email = ?");
        if (user.getFavoriteGenre() != null) fields.add("favorite_genre = ?");
        if (user.getPassword() != null && user.getSalt() != null) {
            fields.add("password = ?");
            fields.add("salt = ?");
        }
        if (fields.isEmpty()) {
            return user;
        }
        super.update(String.format("""
                UPDATE users SET %s WHERE id = ?
                """, String.join(", ", fields)), prepared -> {
            int i = 1;
            if (user.getUsername() != null) prepared.setString(i++, user.getUsername());
            if (user.getEmail() != null) prepared.setString(i++, user.getEmail());
            if (user.getFavoriteGenre() != null) prepared.setString(i++, user.getFavoriteGenre());
            if (user.getPassword() != null && user.getSalt() != null) {
                prepared.setString(i++, user.getPassword());
                prepared.setString(i++, user.getSalt());
            }
            prepared.setInt(i, user.getId());
        });
        return user;
    }

    private static User mapUser(ResultSet result) {
        try {
            return new User()
                    .setId(result.getInt("id"))
                    .setUsername(result.getString("username"))
                    .setEmail(result.getString("email"))
                    .setPassword(result.getString("password"))
                    .setSalt(result.getString("salt"))
                    .setFavoriteGenre(result.getString("favorite_genre"));
        } catch (SQLException exception) {
            Logger.error("Failed to map user: %s", exception.getMessage());
            throw new DbException("Failed to map user", exception);
        }
    }
}
