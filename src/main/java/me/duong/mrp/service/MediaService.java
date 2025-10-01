package me.duong.mrp.service;

import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.repository.MediaRepository;
import me.duong.mrp.repository.RatingRepository;

import java.util.List;
import java.util.Optional;

public class MediaService extends BaseService {
    public Media createMedia(Media media) {
        return super.callDbSession(session -> {
            MediaRepository repository = new MediaRepository(session);
            return repository.insertMedia(media);
        });
    }

    public Optional<Media> updateMedia(Media media) {
        return super.callDbSession(session -> {
            MediaRepository repository = new MediaRepository(session);
            if (repository.findMediaById(media.getId()).isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(repository.updateMedia(media));
        });
    }

    public Optional<Media> getMediaById(int id, int loggedId) {
        return super.callDbSession(session -> {
            MediaRepository repository = new MediaRepository(session);
            RatingRepository ratingRepository = new RatingRepository(session);
            return repository.findMediaById(id).map(media ->
                    media.setRatings(ratingRepository.findAllFilteredRatingsByMediaId(media.getId(), loggedId)));
        });
    }

    public List<Media> getAllMedia(MediaFilter filter, int loggedId) {
        return super.callDbSession(session -> {
            MediaRepository repository = new MediaRepository(session);
            RatingRepository ratingRepository = new RatingRepository(session);
            return repository.findAllMedia(filter).stream().map(media ->
                            media.setRatings(ratingRepository.findAllFilteredRatingsByMediaId(media.getId(), loggedId)))
                    .toList();
        });
    }

    public void deleteMedia(int id, int userId) {
        super.callDbSessionWithoutReturn(session -> {
            MediaRepository repository = new MediaRepository(session);
            repository.deleteMedia(id, userId);
        });
    }

    public boolean markMediaAsFavorite(int userId, int mediaId) {
        return super.callDbSession(session -> {
            MediaRepository repository = new MediaRepository(session);
            if (repository.checkAlreadyMarked(userId, mediaId)) {
                return false;
            }
            repository.markMediaAsFavorite(userId, mediaId);
            return true;
        });
    }

    public boolean unmarkMediaAsFavorite(int userId, int mediaId) {
        return super.callDbSession(session -> {
            MediaRepository repository = new MediaRepository(session);
            if (!repository.checkAlreadyMarked(userId, mediaId)) {
                return false;
            }
            repository.unmarkMediaAsFavorite(userId, mediaId);
            return true;
        });
    }
}
