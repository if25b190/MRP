package me.duong.mrp.service;

import me.duong.mrp.entity.Rating;
import me.duong.mrp.repository.RatingRepository;

import java.util.Optional;

public interface RatingService {
    Optional<Rating> createRating(Rating rating);

    Optional<Rating> updateRating(Rating rating);

    boolean deleteRating(int id, int userId);

    boolean likeRating(int id, int userId);

    boolean confirmRatingComment(int id, int userId);
}
