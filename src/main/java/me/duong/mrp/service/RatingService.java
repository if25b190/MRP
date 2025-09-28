package me.duong.mrp.service;

import me.duong.mrp.utils.Logger;
import me.duong.mrp.entity.Rating;
import me.duong.mrp.repository.DbException;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.RatingRepository;

import java.util.Optional;

public class RatingService {
    public Rating createRating(Rating rating) {
        DbSession session = new DbSession();
        try (session) {
            RatingRepository repository = new RatingRepository(session);
            var result = repository.insertRating(rating);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public Optional<Rating> updateRating(Rating rating) {
        DbSession session = new DbSession();
        try (session) {
            RatingRepository repository = new RatingRepository(session);
            if (repository.findRatingById(rating.getId()).isEmpty()) {
                return Optional.empty();
            }
            var result = repository.updateRating(rating);
            session.commit();
            return Optional.of(result);
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public boolean likeRating(int userId, int ratingId) {
        DbSession session = new DbSession();
        try (session) {
            RatingRepository repository = new RatingRepository(session);
            if (!repository.checkAlreadyLiked(userId, ratingId)) {
                return false;
            }
            repository.likeRating(userId, ratingId);
            session.commit();
            return true;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public void confirmRatingComment(int userId, int mediaId) {
        DbSession session = new DbSession();
        try (session) {
            RatingRepository repository = new RatingRepository(session);
            repository.confirmRatingComment(userId, mediaId);
            session.commit();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }
}
