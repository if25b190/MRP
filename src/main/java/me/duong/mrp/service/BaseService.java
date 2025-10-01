package me.duong.mrp.service;

import me.duong.mrp.repository.DbException;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.utils.Logger;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseService {
    protected <R> R callDbSession(Function<DbSession, R> function) {
        DbSession session = new DbSession();
        try (session) {
            var result = function.apply(session);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException("Session failed to execute:", exception);
        }
    }
    protected void callDbSessionWithoutReturn(Consumer<DbSession> consumer) {
        callDbSession(session -> {
            consumer.accept(session);
            return null;
        });
    }
}
