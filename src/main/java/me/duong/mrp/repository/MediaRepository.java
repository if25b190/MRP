package me.duong.mrp.repository;

import me.duong.mrp.Logger;
import me.duong.mrp.model.Media;
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

    public Media insertMedia(Media media) {
        return super.insert(media, """
                INSERT INTO media (title, description, mediaType, releaseYear, genres, ageRestriction)\s
                VALUES (?, ?, ?, ?, ?, ?)
               \s""", prepared -> {
            prepared.setString(1, media.getTitle());
            prepared.setString(2, media.getDescription());
            prepared.setString(3, media.getMediaType());
            prepared.setInt(4, media.getReleaseYear());
            prepared.setString(5, String.join(",", media.getGenres()));
            prepared.setInt(6, media.getAgeRestriction());
        });
    }

    public Media updateMedia(Media media) {
        super.update("""
                UPDATE media SET title = ?, description = ?, mediaType = ?, releaseYear = ?,\s
                genres = ?, ageRestriction = ? WHERE id = ?
              \s""", prepared -> {
            prepared.setString(1, media.getTitle());
            prepared.setString(2, media.getDescription());
            prepared.setString(3, media.getMediaType());
            prepared.setInt(4, media.getReleaseYear());
            prepared.setString(5, String.join(",", media.getGenres()));
            prepared.setInt(6, media.getAgeRestriction());
            prepared.setInt(7, media.getId());
        });
        return media;
    }

    public void deleteMedia(int id) {
        super.delete("""
                DELETE FROM media WHERE id = ?
                """, prepared -> prepared.setInt(1, id));
    }

    private static Media mapMedia(ResultSet result) {
        try {
            return new Media()
                    .setId(result.getInt(1))
                    .setTitle(result.getString(2))
                    .setDescription(result.getString(3))
                    .setMediaType(result.getString(4))
                    .setReleaseYear(result.getInt(5))
                    .setGenres(result.getString(6) == null ? null : List.of(result.getString(6).split(",")))
                    .setAgeRestriction(result.getInt(7));
        } catch (SQLException exception) {
            Logger.error("Failed to map user: %s", exception.getMessage());
            throw new DbException("Failed to map user", exception);
        }
    }
}
