package me.duong.mrp.repository;

import me.duong.mrp.Logger;
import me.duong.mrp.model.Entity;
import me.duong.mrp.utils.CheckedConsumer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseRepository<T extends Entity<Integer>> {
    protected final DbSession session;

    public BaseRepository(DbSession session) {
        this.session = session;
    }

    protected T insert(T entity, String dml, CheckedConsumer<PreparedStatement> bindParams) {
        try {
            var prepared = session.prepareStatement(dml, Statement.RETURN_GENERATED_KEYS);
            bindParams.accept(prepared);
            int result = prepared.executeUpdate();
            var key = prepared.getGeneratedKeys();
            if (result != 1 || !key.next()) {
                throw new RuntimeException("Save " +
                        entity.getClass().getName() + " failed!");
            }
            var id = key.getInt(1);
            entity.setId(id);
            return entity;
        } catch (SQLException exception) {
            Logger.error("Failed to insert entity: %s", exception.getMessage());
            throw new DbException("Failed to insert entity", exception);
        }
    }

    protected boolean update(String dml, CheckedConsumer<PreparedStatement> bindParams) {
        try {
            var prepared = session.prepareStatement(dml);
            bindParams.accept(prepared);
            int result = prepared.executeUpdate();
            return result == 1;
        } catch (SQLException exception) {
            Logger.error("Failed to update entity: %s", exception.getMessage());
            throw new DbException("Failed to update entity", exception);
        }
    }

    protected Optional<T> findBy(String query, CheckedConsumer<PreparedStatement> bindParams, Function<ResultSet, T> map) {
        try {
            var prepared = session.prepareStatement(query);
            bindParams.accept(prepared);
            var result = prepared.executeQuery();
            if (!result.next()) {
                return Optional.empty();
            }
            return Optional.ofNullable(map.apply(result));
        } catch (SQLException exception) {
            Logger.error("Failed to findBy entity: %s", exception.getMessage());
            throw new DbException("Failed to findBy entity", exception);
        }
    }

    protected List<T> findAll(String query, CheckedConsumer<PreparedStatement> bindParams, Function<ResultSet, T> map) {
        try {
            var prepared = session.prepareStatement(query);
            bindParams.accept(prepared);
            var result = prepared.executeQuery();
            List<T> entities = new ArrayList<>();
            while (result.next()) {
                entities.add(map.apply(result));
            }
            return entities;
        } catch (SQLException exception) {
            Logger.error("Failed to findAll entities: %s", exception.getMessage());
            throw new DbException("Failed to findAll entities", exception);
        }
    }

    protected void delete(String dml, CheckedConsumer<PreparedStatement> bindParams) {
        try {
            var prepared = session.prepareStatement(dml);
            bindParams.accept(prepared);
            prepared.executeUpdate();
        } catch (SQLException exception) {
            Logger.error("Failed to delete entity: %s", exception.getMessage());
            throw new DbException("Failed to delete entity", exception);
        }
    }
}
