package me.duong.mrp.service.impl;

import me.duong.mrp.entity.Media;
import me.duong.mrp.entity.Rating;
import me.duong.mrp.repository.*;
import me.duong.mrp.service.BaseService;
import me.duong.mrp.service.UserService;
import me.duong.mrp.utils.Injector;
import me.duong.mrp.utils.security.TokenStore;
import me.duong.mrp.entity.User;
import me.duong.mrp.utils.security.HashingUtils;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class UserServiceImpl extends BaseService implements UserService {
    @Override
    public Optional<User> getUserById(int id) {
        return super.callDbSession(session -> {
            UserRepository repository = Injector.INSTANCE.resolve(UserRepository.class, session);
            return repository.findUserById(id);
        });
    }

    @Override
    public List<Media> getUserFavorites(int userId, int loggedId) {
        return super.callDbSession(session -> {
            MediaRepository repository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            RatingRepository ratingRepository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            return repository.findAllFavorites(userId).stream().map(media ->
                            media.setRatings(ratingRepository.findAllFilteredRatingsByMediaId(media.getId(), loggedId)))
                    .toList();
        });
    }

    @Override
    public List<Rating> getUserRatingHistory(int userId, int loggedId) {
        return super.callDbSession(session -> {
            RatingRepository repository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            var result = repository.findUserRatings(userId);
            if (userId != loggedId) {
                result = result.stream().peek(rating -> {
                    if (!repository.isCommentAllowed(rating.getId(), userId)) {
                        rating.setComment(null);
                    }
                }).toList();
            }
            return result;
        });
    }

    @Override
    public Optional<User> updateUser(User user) {
        return super.callDbSession(session -> {
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                var salt = HashingUtils.createSalt();
                var password = HashingUtils.hashPassword(user.getPassword(), salt);
                if (password.isEmpty()) {
                    return Optional.empty();
                }
                user.setPassword(password.get());
                user.setSalt(Base64.getEncoder().encodeToString(salt));
            }
            UserRepository repository = Injector.INSTANCE.resolve(UserRepository.class, session);
            return Optional.of(repository.updateUser(user));
        });
    }

    @Override
    public Optional<String> loginUser(User loginDto) {
        return super.callDbSession(session -> {
            UserRepository userRepository = Injector.INSTANCE.resolve(UserRepository.class, session);
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
        });
    }

    @Override
    public Optional<User> registerUser(User loginDto) {
        return super.callDbSession(session -> {
            UserRepository userRepository = Injector.INSTANCE.resolve(UserRepository.class, session);
            if (userRepository.findUserByUsername(loginDto.getUsername()).isPresent()) {
                return Optional.empty();
            }
            var salt = HashingUtils.createSalt();
            var password = HashingUtils.hashPassword(loginDto.getPassword(), salt);
            if (password.isEmpty()) {
                return Optional.empty();
            }
            User user = new User()
                    .setUsername(loginDto.getUsername())
                    .setPassword(password.get())
                    .setSalt(Base64.getEncoder().encodeToString(salt));
            return Optional.of(userRepository.insertUser(user));
        });
    }

}
