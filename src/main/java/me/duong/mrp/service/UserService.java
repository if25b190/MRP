package me.duong.mrp.service;

import me.duong.mrp.entity.Media;
import me.duong.mrp.entity.Rating;
import me.duong.mrp.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(int id);

    List<Media> getUserFavorites(int userId, int loggedId);

    List<Rating> getUserRatingHistory(int userId, int loggedId);

    Optional<User> updateUser(User user);

    Optional<String> loginUser(User loginDto);

    Optional<User> registerUser(User loginDto);
}
