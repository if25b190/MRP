package me.duong.mrp.repository;

import me.duong.mrp.utils.Logger;
import me.duong.mrp.entity.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public class RatingRepository extends BaseRepository<Rating> {
    public RatingRepository(DbSession session) {
        super(session);
    }

    public Optional<Rating> findRatingById(int id) {
        return super.findBy("""
                        SELECT id, stars, comment, created_at FROM ratings WHERE id = ?
                        """,
                prepared -> prepared.setInt(1, id),
                RatingRepository::mapRating);
    }

    public List<Rating> findUserRatings(int userId) {
        return super.findAll("""
                        SELECT id, stars, comment, created_at FROM ratings WHERE user_id = ?
                        """,
                prepared -> prepared.setInt(1, userId),
                RatingRepository::mapRating);
    }

    public Rating insertRating(Rating rating) {
        return super.insert(rating, """
                 INSERT INTO ratings (user_id, media_id, stars, comment)\s
                 VALUES (?, ?, ?, ?)
                \s""", prepared -> {
            prepared.setInt(1, rating.getUserId());
            prepared.setInt(2, rating.getMediaId());
            prepared.setInt(3, rating.getStars());
            prepared.setString(4, rating.getComment());
        });
    }

    public Rating updateRating(Rating rating) {
        super.update("""
                UPDATE ratings SET stars = ?, comment = ? WHERE id = ? AND user_id = ?
                """, prepared -> {
            prepared.setInt(1, rating.getStars());
            prepared.setString(2, rating.getComment());
            prepared.setInt(3, rating.getId());
            prepared.setInt(4, rating.getUserId());
        });
        return rating;
    }

    public void confirmRatingComment(int userId, int ratingId) {
        super.update("""
                UPDATE ratings SET confirmed = true WHERE id = ? AND user_id = ?
                """, prepared -> {
            prepared.setInt(1, ratingId);
            prepared.setInt(2, userId);
        });
    }

    public void likeRating(int userId, int ratingId) {
        super.insert(null, """
                INSERT INTO likes (user_id, rating_id) VALUES (?, ?)
                """, prepared -> {
            prepared.setInt(1, userId);
            prepared.setInt(2, ratingId);
        });
    }

    public boolean checkAlreadyLiked(int userId, int ratingId) {
        return super.findBy("""
                                SELECT * FROM likes WHERE userId = ?, ratingId = ?
                                """,
                        prepared -> {
                            prepared.setInt(1, userId);
                            prepared.setInt(2, ratingId);
                        },
                        resultSet -> new Rating())
                .isPresent();
    }

    private static Rating mapRating(ResultSet result) {
        try {
            return new Rating()
                    .setId(result.getInt(1))
                    .setStars(result.getInt(2))
                    .setComment(result.getString(3))
                    .setCreatedAt(result.getDate(4).toInstant().atOffset(ZoneOffset.UTC));
        } catch (SQLException exception) {
            Logger.error("Failed to map user: %s", exception.getMessage());
            throw new DbException("Failed to map user", exception);
        }
    }
}
