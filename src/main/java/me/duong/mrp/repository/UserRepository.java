package me.duong.mrp.repository;

import me.duong.mrp.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findUserById(int id);

    Optional<User> findUserByUsername(String username);

    User insertUser(User user);

    User updateUser(User user);
}
