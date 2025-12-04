package me.duong.mrp.controller;

import com.fasterxml.jackson.databind.ObjectWriter;
import me.duong.mrp.RestServer;
import me.duong.mrp.TestUtils;
import me.duong.mrp.entity.Rating;
import me.duong.mrp.entity.User;
import me.duong.mrp.service.RatingService;
import me.duong.mrp.utils.Injector;
import me.duong.mrp.utils.security.TokenStore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static me.duong.mrp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class RatingControllerTest {
    private static final RatingService RATING_SERVICE = mock(RatingService.class);
    private static final RestServer REST_SERVER = new RestServer(8084);
    private final ObjectWriter testingWriter = testingWriter();
    private final ObjectWriter publicWriter = publicWriter();
    private final Rating rating = new Rating().setStars(3).setComment("Just a comment.");

    @BeforeAll
    public static void setUp() {
        Injector.INSTANCE.register(RatingService.class, RATING_SERVICE);
        REST_SERVER.start();
    }

    @Test
    public void testRateMediaWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1/rate"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testRateMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.createRating(ArgumentCaptor.forClass(Rating.class).capture()))
                .thenReturn(Optional.of(rating));
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(rating);
            var request = HttpRequest.newBuilder(getUri("/api/media/1/rate"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        }
    }

    @Test
    public void testRateMediaWithInvalidParameters() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(new Rating().setStars(999));
            var request = HttpRequest.newBuilder(getUri("/api/media/1/rate"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testRateMediaWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(rating);
            var request = HttpRequest.newBuilder(getUri("/api/media/notNumber/rate"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUpdateRatingWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1"))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testUpdateRating() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.updateRating(ArgumentCaptor.forClass(Rating.class).capture()))
                .thenReturn(Optional.of(rating));
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(rating);
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testUpdateRatingWithInvalidParameters() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(new Rating().setStars(999));
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUpdateRatingWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(rating);
            var request = HttpRequest.newBuilder(getUri("/api/ratings/notNumber"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testDeleteRatingWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testDeleteRating() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.deleteRating(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(true);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
        }
    }

    @Test
    public void testDeleteUnknownRating() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.deleteRating(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(false);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void testDeleteRatingWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/notNumber"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testLikeRatingWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1/like"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testLikeRating() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.likeRating(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(true);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1/like"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testLikeRating_alreadyLiked() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.likeRating(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(false);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1/like"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testLikeRatingWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/notNumber/like"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testConfirmRatingWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1/confirm"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testConfirmRating() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.confirmRatingComment(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(true);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1/confirm"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testConfirmRating_alreadyConfirmed() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(RATING_SERVICE.confirmRatingComment(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(false);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/1/confirm"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testConfirmRatingWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/ratings/notNumber/confirm"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @AfterAll
    public static void tearDown() {
        REST_SERVER.stop();
    }

    private static URI getUri(String path) {
        return TestUtils.getUri(REST_SERVER, path);
    }
}
