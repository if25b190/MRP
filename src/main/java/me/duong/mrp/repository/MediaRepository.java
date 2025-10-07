package me.duong.mrp.repository;

import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;

import java.util.List;
import java.util.Optional;

public interface MediaRepository {
    Optional<Media> findMediaById(int id);

    List<Media> findAllMedia(MediaFilter filter);

    List<Media> findAllFavorites(int userId);

    Media insertMedia(Media media);

    Media updateMedia(Media media);

    void deleteMedia(int id, int userId);

    boolean checkAlreadyMarked(int userId, int mediaId);

    void markMediaAsFavorite(int userId, int mediaId);

    void unmarkMediaAsFavorite(int userId, int mediaId);
}
