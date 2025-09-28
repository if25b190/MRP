package me.duong.mrp.service;

import me.duong.mrp.utils.Logger;
import me.duong.mrp.entity.Media;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.repository.DbException;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.MediaRepository;

import java.util.List;
import java.util.Optional;

public class MediaService {
    public Media createMedia(Media media) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            var result = repository.insertMedia(media);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public Optional<Media> updateMedia(Media media) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            if (repository.findMediaById(media.getId()).isEmpty()) {
                return Optional.empty();
            }
            var result = repository.updateMedia(media);
            session.commit();
            return Optional.of(result);
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public Optional<Media> getMediaById(int id) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            var result = repository.findMediaById(id);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public List<Media> getAllMedia(MediaFilter filter) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            var result = repository.findAllMedia(filter);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public void deleteMedia(int id, int userId) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            repository.deleteMedia(id, userId);
            session.commit();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public void markMediaAsFavorite(int userId, int mediaId) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            repository.markMediaAsFavorite(userId, mediaId);
            session.commit();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public void unmarkMediaAsFavorite(int userId, int mediaId) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            repository.unmarkMediaAsFavorite(userId, mediaId);
            session.commit();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }
}
