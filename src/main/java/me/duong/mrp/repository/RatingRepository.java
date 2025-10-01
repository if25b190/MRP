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
                         SELECT id, ratings.user_id, ratings.media_id, stars, comment, confirmed, created_at,
                         COUNT(likes.rating_id) AS likes
                         FROM ratings
                         LEFT JOIN likes ON id = likes.rating_id
                         WHERE id = ?
                         GROUP BY id
                        """,
                prepared -> prepared.setInt(1, id),
                RatingRepository::mapRating);
    }

    public List<Rating> findAllRatingsByMediaId(int mediaId) {
        return super.findAll("""
                         SELECT id, ratings.user_id, ratings.media_id, stars, comment, confirmed, created_at,
                         COUNT(likes.rating_id) AS likes
                         FROM ratings
                         LEFT JOIN likes ON id = likes.rating_id
                         WHERE media_id = ?
                         GROUP BY id
                        """,
                prepared -> prepared.setInt(1, mediaId),
                RatingRepository::mapRating);
    }

    public List<Rating> findUserRatings(int userId) {
        return super.findAll("""
                         SELECT id, ratings.user_id, ratings.media_id, stars, comment, confirmed, created_at,
                         COUNT(likes.rating_id) AS likes
                         FROM ratings
                         LEFT JOIN likes ON id = likes.rating_id
                         WHERE ratings.user_id = ?
                         GROUP BY id
                        """,
                prepared -> prepared.setInt(1, userId),
                RatingRepository::mapRating);
    }

    public boolean checkRatingExists(int userId, int mediaId) {
        return super.findBy("""
                                SELECT * FROM ratings WHERE user_id = ? AND media_id = ?
                                """,
                        prepared -> {
                            prepared.setInt(1, userId);
                            prepared.setInt(2, mediaId);
                        },
                        resultSet -> new Rating())
                .isPresent();
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

    public void deleteRating(int id, int userId) {
        super.delete("""
                DELETE FROM ratings WHERE id = ? AND user_id = ?
                """, prepared -> {
            prepared.setInt(1, id);
            prepared.setInt(2, userId);
        });
    }

    public boolean isCommentAllowed(int id, int userId) {
        return super.findBy("""
                                SELECT * FROM ratings INNER JOIN media ON media.id = ratings.media_id
                                WHERE ratings.id = ? AND
                                (ratings.confirmed OR ratings.user_id = ? OR media.user_id = ?)
                                """,
                        prepared -> {
                            prepared.setInt(1, id);
                            prepared.setInt(2, userId);
                            prepared.setInt(3, userId);
                        },
                        resultSet -> new Rating())
                .isPresent();
    }

    public boolean isConfirmAllowed(int id, int userId) {
        return super.findBy("""
                                SELECT * FROM ratings INNER JOIN media ON media.id = ratings.media_id
                                WHERE ratings.id = ? AND media.user_id = ?
                                """,
                        prepared -> {
                            prepared.setInt(1, id);
                            prepared.setInt(2, userId);
                        },
                        resultSet -> new Rating())
                .isPresent();
    }

    public void confirmRatingComment(int id) {
        super.update("""
                UPDATE ratings SET confirmed = true WHERE id = ?
                """, prepared -> {
            prepared.setInt(1, id);
        });
    }

    public void likeRating(int id, int userId) {
        super.insert(null, """
                INSERT INTO likes (rating_id, user_id) VALUES (?, ?)
                """, prepared -> {
            prepared.setInt(1, id);
            prepared.setInt(2, userId);
        });
    }

    public boolean checkAlreadyLiked(int id, int userId) {
        return super.findBy("""
                                SELECT * FROM likes WHERE rating_id = ? AND user_id = ?
                                """,
                        prepared -> {
                            prepared.setInt(1, id);
                            prepared.setInt(2, userId);
                        },
                        resultSet -> new Rating())
                .isPresent();
    }

    public List<Rating> findAllFilteredRatingsByMediaId(int mediaId, int loggedId) {
        return findAllRatingsByMediaId(mediaId)
                .stream()
                .peek(rating -> {
                    if (!isCommentAllowed(rating.getId(), loggedId)) {
                        rating.setComment(null);
                    }
                })
                .toList();
    }

    private static Rating mapRating(ResultSet result) {
        try {
            return new Rating()
                    .setId(result.getInt("id"))
                    .setUserId(result.getInt("user_id"))
                    .setMediaId(result.getInt("media_id"))
                    .setStars(result.getInt("stars"))
                    .setComment(result.getString("comment"))
                    .setConfirmed(result.getBoolean("confirmed"))
                    .setCreatedAt(result.getTimestamp("created_at").toInstant().atOffset(ZoneOffset.of("+0200")))
                    .setLikes(result.getInt("likes"));
        } catch (SQLException exception) {
            Logger.error("Failed to map user: %s", exception.getMessage());
            throw new DbException("Failed to map user", exception);
        }
    }
}
