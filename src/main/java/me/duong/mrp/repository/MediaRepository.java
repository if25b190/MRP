package me.duong.mrp.repository;

import me.duong.mrp.model.Media;

import java.sql.SQLException;

public class MediaRepository {
    private final DbSession session;

    public MediaRepository(DbSession session) {
        this.session = session;
    }

    public boolean insertMedia(Media media) throws SQLException {
        var statement = session.prepareStatement("""
                INSERT INTO media (title, description, mediaType, releaseYear, genres, ageRestriction)\s
                VALUES (?, ?, ?, ?, ?, ?)
               \s""");
        statement.setString(1, media.getTitle());
        statement.setString(2, media.getDescription());
        statement.setString(3, media.getMediaType());
        statement.setInt(4, media.getReleaseYear());
        statement.setString(5, String.join(",", media.getGenres()));
        statement.setInt(6, media.getAgeRestriction());
        var result = statement.executeUpdate();
        return result == 1;
    }
}
