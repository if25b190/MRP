package me.duong.mrp.repository;

import me.duong.mrp.utils.Logger;
import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MediaRepository extends BaseRepository<Media> {
    public MediaRepository(DbSession session) {
        super(session);
    }

    public Optional<Media> findMediaById(int id) {
        return super.findBy("""
                        SELECT * FROM media WHERE id = ?
                        """,
                prepared -> prepared.setInt(1, id),
                MediaRepository::mapMedia);
    }

    public List<Media> findAllMedia(MediaFilter filter) {
        return super.findAll(filter.buildPreparedStatement(),
                prepared -> {
                    int i = 1;
                    if (filter.title() != null) prepared.setString(i++, filter.title());
                    if (filter.genre() != null) prepared.setString(i++, filter.genre());
                    if (filter.mediaType() != null) prepared.setString(i++, filter.mediaType());
                    if (filter.releaseYear() != -1) prepared.setInt(i++, filter.releaseYear());
                    if (filter.ageRestriction() != -1) prepared.setInt(i, filter.ageRestriction());
                    // if (filter.rating() != -1) prepared.setFloat(i++, filter.rating());
                },
                MediaRepository::mapMedia);
    }

    public List<Media> findAllFavorites(int userId) {
        return super.findAll("""
                        SELECT id, user_id, title, description, media_type, release_year, genres, age_restriction\s
                        FROM media INNER JOIN favorites ON media.id = favorites.media_id\s
                        WHERE favorites.user_id = ?
                       \s""",
                prepared -> prepared.setInt(1, userId),
                MediaRepository::mapMedia);
    }

    public Media insertMedia(Media media) {
        return super.insert(media, """
                 INSERT INTO media (user_id, title, description, media_type, release_year, genres, age_restriction)\s
                 VALUES (?, ?, ?, ?, ?, ?, ?)
                \s""", prepared -> {
            prepared.setInt(1, media.getUserId());
            prepared.setString(2, media.getTitle());
            prepared.setString(3, media.getDescription());
            prepared.setString(4, media.getMediaType());
            prepared.setInt(5, media.getReleaseYear());
            prepared.setString(6, String.join(",", media.getGenres()));
            prepared.setInt(7, media.getAgeRestriction());
        });
    }

    public Media updateMedia(Media media) {
        super.update("""
                  UPDATE media SET title = ?, description = ?, media_type = ?, release_year = ?,\s
                  genres = ?, age_restriction = ? WHERE id = ? AND user_id = ?
                \s""", prepared -> {
            prepared.setString(1, media.getTitle());
            prepared.setString(2, media.getDescription());
            prepared.setString(3, media.getMediaType());
            prepared.setInt(4, media.getReleaseYear());
            prepared.setString(5, String.join(",", media.getGenres()));
            prepared.setInt(6, media.getAgeRestriction());
            prepared.setInt(7, media.getId());
            prepared.setInt(8, media.getUserId());
        });
        return media;
    }

    public void deleteMedia(int id, int userId) {
        super.delete("""
                DELETE FROM media WHERE id = ? AND user_id = ?
                """, prepared -> {
            prepared.setInt(1, id);
            prepared.setInt(2, userId);
        });
    }

    public void markMediaAsFavorite(int userId, int mediaId) {
        super.insert(null, """
                INSERT INTO favorites (user_id, media_id) VALUES (?, ?)
                """, prepared -> {
            prepared.setInt(1, userId);
            prepared.setInt(2, mediaId);
        });
    }

    public void unmarkMediaAsFavorite(int userId, int mediaId) {
        super.delete("""
                DELETE FROM favorites WHERE user_id = ? AND media_id = ?
                """, prepared -> {
            prepared.setInt(1, userId);
            prepared.setInt(2, mediaId);
        });
    }

    private static Media mapMedia(ResultSet result) {
        try {
            return new Media()
                    .setId(result.getInt(1))
                    .setUserId(result.getInt(2))
                    .setTitle(result.getString(3))
                    .setDescription(result.getString(4))
                    .setMediaType(result.getString(5))
                    .setReleaseYear(result.getInt(6))
                    .setGenres(result.getString(7) == null ? null : List.of(result.getString(7).split(",")))
                    .setAgeRestriction(result.getInt(8));
        } catch (SQLException exception) {
            Logger.error("Failed to map user: %s", exception.getMessage());
            throw new DbException("Failed to map user", exception);
        }
    }
}
