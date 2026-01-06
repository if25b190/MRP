package me.duong.mrp.service;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import me.duong.mrp.TestDbConnection;
import me.duong.mrp.entity.Media;
import me.duong.mrp.entity.Rating;
import me.duong.mrp.entity.User;
import me.duong.mrp.model.MediaType;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.MediaRepository;
import me.duong.mrp.repository.RatingRepository;
import me.duong.mrp.repository.UserRepository;
import me.duong.mrp.repository.impl.MediaRepositoryImpl;
import me.duong.mrp.repository.impl.RatingRepositoryImpl;
import me.duong.mrp.repository.impl.UserRepositoryImpl;
import me.duong.mrp.service.impl.MediaServiceImpl;
import me.duong.mrp.service.impl.RatingServiceImpl;
import me.duong.mrp.service.impl.UserServiceImpl;
import me.duong.mrp.utils.Injector;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private static EmbeddedPostgres pg;
    private static String schema;

    @BeforeAll
    public static void beforeAll() throws IOException {
        try (var resource = Thread.currentThread().getContextClassLoader().getResourceAsStream("database.sql")) {
            if (resource == null) {
                throw new IllegalStateException("Cannot find database resource");
            }
            schema = IOUtils.toString(resource, StandardCharsets.UTF_8);
        }
        pg = EmbeddedPostgres.builder().start();
        var url = pg.getJdbcUrl("postgres", "postgres");
        Supplier<Connection> testConnection = () -> TestDbConnection.INSTANCE.getConnection(url, "", "");
        Injector.INSTANCE.register(Connection.class, testConnection);
        Injector.INSTANCE.register(DbSession.class, DbSession.class);
        Injector.INSTANCE.register(UserRepository.class, UserRepositoryImpl.class);
        Injector.INSTANCE.register(MediaRepository.class, MediaRepositoryImpl.class);
        Injector.INSTANCE.register(RatingRepository.class, RatingRepositoryImpl.class);
    }

    @BeforeEach
    public void beforeEach() {
        resetDb(Injector.INSTANCE.resolve(Connection.class));
    }

    @Test
    public void testRegisterUser() {
        var userService = new UserServiceImpl();
        userService.registerUser(new User().setUsername("user1").setPassword("pass123"));
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());
        assertNotEquals("pass123", result.get().getPassword());
        assertNotNull(result.get().getSalt());
        assertFalse(result.get().getSalt().isBlank());
    }

    @Test
    public void testFindNonExistingUserById() {
        var userService = new UserServiceImpl();
        var result = userService.getUserById(1);
        assertFalse(result.isPresent());
    }

    @Test
    public void testRecommendationGenre() {
        var userService = new UserServiceImpl();
        var mediaService = new MediaServiceImpl();
        var ratingService = new RatingServiceImpl();

        userService.registerUser(new User().setUsername("user1").setPassword("pass123"));
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());

        var media1 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test")
                .setMediaType(MediaType.MOVIE.name())
                .setReleaseYear(2010)
                .setGenres(List.of("sci-fi", "action"))
                .setAgeRestriction(16));
        var media2 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test 2")
                .setMediaType(MediaType.SERIES.name())
                .setReleaseYear(2010)
                .setGenres(List.of("romance", "school", "sci-fi"))
                .setAgeRestriction(0));

        ratingService.createRating(new Rating()
                .setUserId(result.get().getId())
                .setMediaId(media1.getId())
                .setStars(5)
                .setComment("Amazing movie!")
                .setConfirmed(false));

        var recommendations = userService.getUserRecommendations(result.get().getId(), "genre");
        assertEquals(1, recommendations.size());
        assertEquals(media2, recommendations.getFirst());
    }

    @Test
    public void testRecommendationGenreNoneMatching() {
        var userService = new UserServiceImpl();
        var mediaService = new MediaServiceImpl();
        var ratingService = new RatingServiceImpl();

        userService.registerUser(new User().setUsername("user1").setPassword("pass123"));
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());

        var media1 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test")
                .setMediaType(MediaType.MOVIE.name())
                .setReleaseYear(2010)
                .setGenres(List.of("sci-fi", "action"))
                .setAgeRestriction(16));
        var media2 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test 2")
                .setMediaType(MediaType.SERIES.name())
                .setReleaseYear(2010)
                .setGenres(List.of("romance", "school", "fantasy"))
                .setAgeRestriction(0));

        ratingService.createRating(new Rating()
                .setUserId(result.get().getId())
                .setMediaId(media1.getId())
                .setStars(5)
                .setComment("Amazing movie!")
                .setConfirmed(false));

        var recommendations = userService.getUserRecommendations(result.get().getId(), "genre");
        assertTrue(recommendations.isEmpty());
    }

    @Test
    public void testRecommendationContent() {
        var userService = new UserServiceImpl();
        var mediaService = new MediaServiceImpl();
        var ratingService = new RatingServiceImpl();

        userService.registerUser(new User().setUsername("user1").setPassword("pass123"));
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());

        var media1 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test")
                .setMediaType(MediaType.MOVIE.name())
                .setReleaseYear(2010)
                .setGenres(List.of("sci-fi", "action"))
                .setAgeRestriction(16));
        var media2 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test 2")
                .setMediaType(MediaType.MOVIE.name())
                .setReleaseYear(2010)
                .setGenres(List.of("romance", "school", "fantasy"))
                .setAgeRestriction(16));

        ratingService.createRating(new Rating()
                .setUserId(result.get().getId())
                .setMediaId(media1.getId())
                .setStars(5)
                .setComment("Amazing movie!")
                .setConfirmed(false));

        // matches 2 of 3: age restriction and media type but not genres
        var recommendations = userService.getUserRecommendations(result.get().getId(), "content");
        assertEquals(1, recommendations.size());
        assertEquals(media2, recommendations.getFirst());
    }

    @Test
    public void testRecommendationContentNoneMatching() {
        var userService = new UserServiceImpl();
        var mediaService = new MediaServiceImpl();
        var ratingService = new RatingServiceImpl();

        userService.registerUser(new User().setUsername("user1").setPassword("pass123"));
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());

        var media1 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test")
                .setMediaType(MediaType.MOVIE.name())
                .setReleaseYear(2010)
                .setGenres(List.of("sci-fi", "action"))
                .setAgeRestriction(16));
        var media2 = mediaService.createMedia(new Media()
                .setUserId(result.get().getId())
                .setTitle("Test 2")
                .setMediaType(MediaType.SERIES.name())
                .setReleaseYear(2010)
                .setGenres(List.of("romance", "school", "sci-fi"))
                .setAgeRestriction(0));

        ratingService.createRating(new Rating()
                .setUserId(result.get().getId())
                .setMediaId(media1.getId())
                .setStars(5)
                .setComment("Amazing movie!")
                .setConfirmed(false));

        // only matches 1 of 3: genres but not age restriction and media type
        var recommendations = userService.getUserRecommendations(result.get().getId(), "content");
        assertEquals(0, recommendations.size());
    }

    @AfterAll
    public static void afterAll() throws IOException {
        pg.close();
    }

    private static void resetDb(Connection connection) {
        try (connection) {
            connection.prepareStatement(schema).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
