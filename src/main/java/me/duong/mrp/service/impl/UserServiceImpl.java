package me.duong.mrp.service.impl;

import me.duong.mrp.entity.Media;
import me.duong.mrp.entity.Rating;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.repository.*;
import me.duong.mrp.service.BaseService;
import me.duong.mrp.service.UserService;
import me.duong.mrp.utils.Injector;
import me.duong.mrp.utils.security.TokenStore;
import me.duong.mrp.entity.User;
import me.duong.mrp.utils.security.HashingUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserServiceImpl extends BaseService implements UserService {
    @Override
    public Optional<User> getUserById(int id) {
        return super.callDbSession(session -> {
            UserRepository repository = Injector.INSTANCE.resolve(UserRepository.class, session);
            var result = repository.findUserById(id);
            if (result.isPresent()) {
                RatingRepository ratingRepository = Injector.INSTANCE.resolve(RatingRepository.class, session);
                var ratingHistory = ratingRepository.findUserRatings(id);
                result.get().setTotalRatings(ratingHistory.size());
                result.get().setAverageScore((float) ratingHistory.stream()
                        .map(Rating::getStars)
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0f)
                );
            }
            return result;
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

    @Override
    public List<Media> getUserRecommendations(int userId, String type) {
        var ratingHistory = getUserRatingHistory(userId, userId);
        var userFavorites = getUserFavorites(userId, userId);
        return super.callDbSession(session -> {
            var mediaRepository = Injector.INSTANCE.resolve(MediaRepository.class, session);
            var data = mediaRepository.findAllMedia(MediaFilter.fromQuery(Map.of()));
            return switch (type) {
                case "genre" -> {
                    var highlyRated = ratingHistory
                            .stream()
                            .filter(rating -> rating.getStars() >= 3)
                            .sorted(Comparator.comparingInt(Rating::getStars).reversed())
                            .limit(10)
                            .map(Rating::getMediaId)
                            .toList();
                    var targetGenres = data.stream()
                            .filter(media -> highlyRated.contains(media.getId()))
                            .flatMap(media -> media.getGenres().stream().map(String::toLowerCase))
                            .collect(Collectors.toSet());
                    yield data.stream()
                            .filter(media ->
                                    !isAlreadyWatchedByUser(media, userFavorites, ratingHistory) &&
                                            (double) media.getGenres()
                                                    .stream()
                                                    .filter(genre ->
                                                            targetGenres.contains(genre.toLowerCase()))
                                                    .count()
                                                    / Math.min(media.getGenres().size(), targetGenres.size())
                                                    >= 0.5)
                            .toList();
                }
                case "content" -> {
                    var userMediaList = data.stream()
                            .filter(media -> isAlreadyWatchedByUser(media, userFavorites, ratingHistory))
                            .toList();
                    yield data.stream()
                            .filter(media -> !isAlreadyWatchedByUser(media, userFavorites, ratingHistory) &&
                                    userMediaList.stream().anyMatch(userMedia -> {
                                        var similarAgeRes = userMedia.getAgeRestriction() == media.getAgeRestriction();
                                        var similarMediaType = userMedia.getMediaType().equals(media.getMediaType());
                                        var similarGenres = (double) userMedia.getGenres()
                                                .stream()
                                                .filter(genre ->
                                                        media.getGenres().contains(genre.toLowerCase()))
                                                .count()
                                                / Math.min(userMedia.getGenres().size(), media.getGenres().size())
                                                >= 0.5;
                                        return Stream.of(similarAgeRes, similarMediaType, similarGenres)
                                                .filter(s -> s)
                                                .count() >= 2;
                                    }))
                            .toList();
                }
                default -> List.of();
            };
        });
    }

    public List<User> getLeaderboard() {
        return super.callDbSession(session -> {
            UserRepository repository = Injector.INSTANCE.resolve(UserRepository.class, session);
            RatingRepository ratingRepository = Injector.INSTANCE.resolve(RatingRepository.class, session);
            return repository.findLeaderboard().stream().peek(user -> {
                var ratingHistory = ratingRepository.findUserRatings(user.getId());
                user.setTotalRatings(ratingHistory.size());
                user.setAverageScore((float) ratingHistory.stream()
                        .map(Rating::getStars)
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0f)
                );
            }).toList();
        });
    }

    private boolean isAlreadyWatchedByUser(Media media, List<Media> mediaList, List<Rating> ratings) {
        return mediaList.contains(media) ||
                ratings.stream().anyMatch(rating -> rating.getMediaId() == media.getId());
    }

}
