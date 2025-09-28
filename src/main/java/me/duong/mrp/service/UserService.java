package me.duong.mrp.service;

import me.duong.mrp.entity.Media;
import me.duong.mrp.entity.Rating;
import me.duong.mrp.repository.*;
import me.duong.mrp.utils.Logger;
import me.duong.mrp.utils.security.TokenStore;
import me.duong.mrp.entity.User;
import me.duong.mrp.utils.security.HashingUtils;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class UserService {
    public Optional<User> getUserById(int id) {
        DbSession session = new DbSession();
        try (session) {
            UserRepository repository = new UserRepository(session);
            var result = repository.findUserById(id);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public List<Media> getUserFavorites(int userId) {
        DbSession session = new DbSession();
        try (session) {
            MediaRepository repository = new MediaRepository(session);
            var result = repository.findAllFavorites(userId);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public List<Rating> getUserRatingHistory(int userId) {
        DbSession session = new DbSession();
        try (session) {
            RatingRepository repository = new RatingRepository(session);
            var result = repository.findUserRatings(userId);
            session.commit();
            return result;
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public Optional<User> updateUser(User user) {
        DbSession session = new DbSession();
        try (session) {
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                var salt = HashingUtils.createSalt();
                var password = HashingUtils.hashPassword(user.getPassword(), salt);
                if (password.isEmpty()) {
                    return Optional.empty();
                }
                user.setPassword(password.get());
                user.setSalt(Base64.getEncoder().encodeToString(salt));
            }
            UserRepository repository = new UserRepository(session);
            var result = repository.updateUser(user);
            session.commit();
            return Optional.of(result);
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

    public Optional<String> loginUser(User loginDto) {
        DbSession session = new DbSession();
        try (session) {
            UserRepository userRepository = new UserRepository(session);
            var result = userRepository.findUserByUsername(loginDto.getUsername());
            if (result.isPresent()) {
                var user = result.get();
                var salt = Base64.getDecoder().decode(user.getSalt());
                var pass = HashingUtils.hashPassword(loginDto.getPassword(), salt);
                if (pass.filter(s -> s.equals(user.getPassword())).isPresent()) {
                    return Optional.of(TokenStore.INSTANCE.createToken(user));
                }
            }
            return Optional.empty();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception);
        }
    }

    public Optional<User> registerUser(User loginDto) {
        DbSession session = new DbSession();
        try (session) {
            UserRepository userRepository = new UserRepository(session);
            var salt = HashingUtils.createSalt();
            var password = HashingUtils.hashPassword(loginDto.getPassword(), salt);
            if (password.isEmpty()) {
                return Optional.empty();
            }
            User user = new User()
                    .setUsername(loginDto.getUsername())
                    .setPassword(password.get())
                    .setSalt(Base64.getEncoder().encodeToString(salt));
            user = userRepository.insertUser(user);
            session.commit();
            return Optional.of(user);
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception);
        }
    }

}
