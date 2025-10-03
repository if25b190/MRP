package me.duong.mrp.repository;

import me.duong.mrp.entity.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    Optional<Rating> findRatingById(int id);

    List<Rating> findAllRatingsByMediaId(int mediaId);

    List<Rating> findUserRatings(int userId);

    boolean checkRatingExists(int userId, int mediaId);

    Rating insertRating(Rating rating);

    Rating updateRating(Rating rating);

    void deleteRating(int id, int userId);

    boolean isCommentAllowed(int id, int userId);

    boolean isConfirmAllowed(int id, int userId);

    void confirmRatingComment(int id);

    void likeRating(int id, int userId);

    boolean checkAlreadyLiked(int id, int userId);

    List<Rating> findAllFilteredRatingsByMediaId(int mediaId, int loggedId);
}
