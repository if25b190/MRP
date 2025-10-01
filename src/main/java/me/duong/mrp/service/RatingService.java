package me.duong.mrp.service;

import me.duong.mrp.entity.Rating;
import me.duong.mrp.repository.RatingRepository;

import java.util.Optional;

public class RatingService extends BaseService {
    public Optional<Rating> createRating(Rating rating) {
        return super.callDbSession(session -> {
            RatingRepository repository = new RatingRepository(session);
            if (repository.checkRatingExists(rating.getUserId(), rating.getMediaId())) {
                return Optional.empty();
            }
            return Optional.of(repository.insertRating(rating));
        });
    }

    public Optional<Rating> updateRating(Rating rating) {
        return super.callDbSession(session -> {
            RatingRepository repository = new RatingRepository(session);
            if (repository.findRatingById(rating.getId()).isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(repository.updateRating(rating));
        });
    }

    public void deleteRating(int id, int userId) {
        super.callDbSessionWithoutReturn(session -> {
            RatingRepository repository = new RatingRepository(session);
            repository.deleteRating(id, userId);
        });
    }

    public boolean likeRating(int id, int userId) {
        return super.callDbSession(session -> {
            RatingRepository repository = new RatingRepository(session);
            if (repository.checkAlreadyLiked(id, userId)) {
                return false;
            }
            repository.likeRating(id, userId);
            return true;
        });
    }

    public boolean confirmRatingComment(int id, int userId) {
        return super.callDbSession(session -> {
            RatingRepository repository = new RatingRepository(session);
            if (repository.isConfirmAllowed(id, userId)) {
                repository.confirmRatingComment(id);
                return true;
            }
            return false;
        });
    }
}
