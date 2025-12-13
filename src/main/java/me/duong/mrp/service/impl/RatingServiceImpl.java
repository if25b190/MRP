package me.duong.mrp.service.impl;

import me.duong.mrp.entity.Rating;
import me.duong.mrp.repository.RatingRepository;
import me.duong.mrp.service.BaseService;
import me.duong.mrp.service.RatingService;
import me.duong.mrp.utils.Injector;

import java.util.Optional;

public class RatingServiceImpl extends BaseService implements RatingService {
    @Override
    public Optional<Rating> createRating(Rating rating) {
        return super.callDbSession(session -> {
            RatingRepository repository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            if (repository.checkRatingExists(rating.getUserId(), rating.getMediaId())) {
                return Optional.empty();
            }
            return Optional.of(repository.insertRating(rating));
        });
    }

    @Override
    public Optional<Rating> updateRating(Rating rating) {
        return super.callDbSession(session -> {
            RatingRepository repository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            if (repository.findRatingById(rating.getId()).isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(repository.updateRating(rating));
        });
    }

    @Override
    public boolean deleteRating(int id, int userId) {
        return super.callDbSession(session -> {
            RatingRepository repository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            if (!repository.checkRatingExists(id, userId)) {
                return false;
            }
            repository.deleteRating(id, userId);
            return true;
        });
    }

    @Override
    public boolean likeRating(int id, int userId) {
        return super.callDbSession(session -> {
            RatingRepository repository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            if (repository.checkAlreadyLiked(id, userId)) {
                return false;
            }
            repository.likeRating(id, userId);
            return true;
        });
    }

    @Override
    public boolean confirmRatingComment(int id, int userId) {
        return super.callDbSession(session -> {
            RatingRepository repository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            if (repository.isConfirmAllowed(id, userId)) {
                repository.confirmRatingComment(id);
                return true;
            }
            return false;
        });
    }
}
