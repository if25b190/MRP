package me.duong.mrp.service.impl;

import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.repository.MediaRepository;
import me.duong.mrp.repository.RatingRepository;
import me.duong.mrp.service.BaseService;
import me.duong.mrp.service.MediaService;
import me.duong.mrp.utils.Injector;

import java.util.List;
import java.util.Optional;

public class MediaServiceImpl extends BaseService implements MediaService {
    @Override
    public Media createMedia(Media media) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            return repository.insertMedia(media);
        });
    }

    @Override
    public Optional<Media> updateMedia(Media media) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            if (repository.findMediaById(media.getId()).isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(repository.updateMedia(media));
        });
    }

    @Override
    public Optional<Media> getMediaById(int id, int loggedId) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            RatingRepository ratingRepository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            return repository.findMediaById(id).map(media ->
                    media.setRatings(ratingRepository.findAllFilteredRatingsByMediaId(media.getId(), loggedId)));
        });
    }

    @Override
    public List<Media> getAllMedia(MediaFilter filter, int loggedId) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            RatingRepository ratingRepository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            return repository.findAllMedia(filter).stream().map(media ->
                            media.setRatings(ratingRepository.findAllFilteredRatingsByMediaId(media.getId(), loggedId)))
                    .toList();
        });
    }

    @Override
    public boolean deleteMedia(int id, int userId) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            if (repository.findMediaById(id).isEmpty()) {
                return false;
            }
            repository.deleteMedia(id, userId);
            return true;
        });
    }

    @Override
    public boolean markMediaAsFavorite(int userId, int mediaId) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            if (repository.checkAlreadyMarked(userId, mediaId)) {
                return false;
            }
            repository.markMediaAsFavorite(userId, mediaId);
            return true;
        });
    }

    @Override
    public boolean unmarkMediaAsFavorite(int userId, int mediaId) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            if (!repository.checkAlreadyMarked(userId, mediaId)) {
                return false;
            }
            repository.unmarkMediaAsFavorite(userId, mediaId);
            return true;
        });
    }
}
