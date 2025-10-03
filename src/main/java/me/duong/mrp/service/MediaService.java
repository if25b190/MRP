package me.duong.mrp.service;

import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.repository.MediaRepository;
import me.duong.mrp.repository.RatingRepository;

import java.util.List;
import java.util.Optional;

public interface MediaService {
    Media createMedia(Media media);

    Optional<Media> updateMedia(Media media);

    Optional<Media> getMediaById(int id, int loggedId);

    List<Media> getAllMedia(MediaFilter filter, int loggedId);

    void deleteMedia(int id, int userId);

    boolean markMediaAsFavorite(int userId, int mediaId);

    boolean unmarkMediaAsFavorite(int userId, int mediaId);
}
