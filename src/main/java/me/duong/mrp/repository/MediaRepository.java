package me.duong.mrp.repository;

import me.duong.mrp.model.Media;

public class MediaRepository extends BaseRepository<Media> {
    public MediaRepository(DbSession session) {
        super(session);
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

    public void delete(Media media) {
        super.delete(media, """
                DELETE FROM media WHERE id = ?
                """);
    }
}
