package me.duong.mrp.service;

import me.duong.mrp.Logger;
import me.duong.mrp.model.Media;
import me.duong.mrp.repository.DbException;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.MediaRepository;

import java.util.Optional;

public class MediaService {
    public Optional<Media> createMedia(Media media) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            var result = repository.insertMedia(media);
            if (!result) {
                return Optional.empty();
            }
            session.commit();
            return Optional.of(media);
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }
}
