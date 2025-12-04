package me.duong.mrp.controller;

import com.fasterxml.jackson.databind.ObjectWriter;
import me.duong.mrp.RestServer;
import me.duong.mrp.TestUtils;
import me.duong.mrp.entity.Media;
import me.duong.mrp.entity.User;
import me.duong.mrp.model.MediaFilter;
import me.duong.mrp.model.MediaType;
import me.duong.mrp.service.MediaService;
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
import java.util.List;
import java.util.Optional;

import static me.duong.mrp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class MediaControllerTest {
    private static final MediaService MEDIA_SERVICE = mock(MediaService.class);
    private static final RestServer REST_SERVER = new RestServer(8083);
    private final ObjectWriter testingWriter = testingWriter();
    private final ObjectWriter publicWriter = publicWriter();
    private final Media media = new Media().setId(1)
            .setTitle("Test")
            .setDescription("")
            .setMediaType(MediaType.MOVIE.name())
            .setGenres(List.of("sci-fi"))
            .setAgeRestriction(12);

    @BeforeAll
    public static void setUp() {
        Injector.INSTANCE.register(MediaService.class, MEDIA_SERVICE);
        REST_SERVER.start();
    }

    @Test
    public void testGetAllMediaWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testGetAllMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.getAllMedia(
                        ArgumentCaptor.forClass(MediaFilter.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(List.of());
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testGetMediaByIdWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testGetMediaById() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.getMediaById(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(Optional.of(new Media().setId(1)));
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testGetUnknownMediaById() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.getMediaById(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(Optional.empty());
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void testGetMediaByInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/notNumber"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testCreateMediaWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testCreateMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.createMedia(ArgumentCaptor.forClass(Media.class).capture()))
                .thenReturn(media);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(media);
            var request = HttpRequest.newBuilder(getUri("/api/media"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        }
    }

    @Test
    public void testCreateMediaWithInvalidParameters() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(media.cloneMedia().setAgeRestriction(-5));
            var request = HttpRequest.newBuilder(getUri("/api/media"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUpdateMediaWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testUpdateMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.updateMedia(ArgumentCaptor.forClass(Media.class).capture()))
                .thenReturn(Optional.of(media));
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(media);
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testUpdateUnknownMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.updateMedia(ArgumentCaptor.forClass(Media.class).capture()))
                .thenReturn(Optional.empty());
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(media);
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void testUpdateMediaWithInvalidParameters() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(media.cloneMedia().setAgeRestriction(-5));
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testDeleteMediaWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testDeleteMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.deleteMedia(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(true);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(204, response.statusCode());
        }
    }

    @Test
    public void testDeleteUnknownMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.deleteMedia(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(false);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void testDeleteMediaWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/notNumber"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testMarkFavoriteMediaWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1/favorite"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testMarkFavoriteMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.markMediaAsFavorite(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(true);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1/favorite"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testMarkFavoriteMedia_alreadyMarked() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.markMediaAsFavorite(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(false);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1/favorite"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testMarkFavoriteMediaWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/notNumber/favorite"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUnmarkFavoriteMediaWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1/favorite"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testUnmarkFavoriteMedia() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.unmarkMediaAsFavorite(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(true);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1/favorite"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode()); // TODO
        }
    }

    @Test
    public void testUnmarkFavoriteMedia_alreadyUnmarked() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(MEDIA_SERVICE.unmarkMediaAsFavorite(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(false);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/1/favorite"))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUnmarkFavoriteMediaWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/media/notNumber/favorite"))
                    .DELETE()
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
