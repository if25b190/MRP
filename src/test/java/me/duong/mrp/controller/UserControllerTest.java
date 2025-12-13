package me.duong.mrp.controller;

import com.fasterxml.jackson.databind.ObjectWriter;
import me.duong.mrp.RestServer;
import me.duong.mrp.TestUtils;
import me.duong.mrp.entity.User;
import me.duong.mrp.service.UserService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private static final UserService USER_SERVICE = mock(UserService.class);
    private static final RestServer REST_SERVER = new RestServer(8082);
    private final ObjectWriter testingWriter = testingWriter();
    private final ObjectWriter publicWriter = publicWriter();

    @BeforeAll
    public static void setUp() {
        Injector.INSTANCE.register(UserService.class, USER_SERVICE);
        REST_SERVER.start();
    }

    @Test
    public void testRegisterUser() throws IOException, InterruptedException {
        var user = new User().setUsername("user1").setPassword("pass123");
        lenient().when(USER_SERVICE.registerUser(ArgumentCaptor.forClass(User.class).capture()))
                .thenReturn(Optional.of(user));
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/register"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        }
    }

    @Test
    public void testRegisterUserWithoutPassword() throws IOException, InterruptedException {
        var user = new User().setUsername("user1");
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/register"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testRegisterUserWrongCredentials() throws IOException, InterruptedException {
        var user = new User().setUsername("user1").setPassword("pass123");
        lenient().when(USER_SERVICE.registerUser(ArgumentCaptor.forClass(User.class).capture()))
                .thenReturn(Optional.empty());
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/register"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testLoginUser() throws IOException, InterruptedException {
        var user = new User().setUsername("user1").setPassword("pass123");
        lenient().when(USER_SERVICE.loginUser(ArgumentCaptor.forClass(User.class).capture()))
                .thenReturn(Optional.of("SOME_TOKEN_1"));
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("SOME_TOKEN_1"));
        }
    }

    @Test
    public void testLoginUserWithoutPassword() throws IOException, InterruptedException {
        var user = new User().setUsername("user1");
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testLoginUserWrongCredentials() throws IOException, InterruptedException {
        var user = new User().setUsername("user1").setPassword("pass123");
        lenient().when(USER_SERVICE.registerUser(ArgumentCaptor.forClass(User.class).capture()))
                .thenReturn(Optional.empty());
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testProfileWithoutToken() throws IOException, InterruptedException {
        var user = new User().setUsername("user1").setPassword("pass123");
        lenient().when(USER_SERVICE.getUserById(ArgumentCaptor.forClass(Integer.class).capture()))
                .thenReturn(Optional.of(user));
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/profile"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testProfileWithToken() throws IOException, InterruptedException {
        var user = new User().setId(1).setUsername("user1").setPassword("pass123");
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.getUserById(ArgumentCaptor.forClass(Integer.class).capture()))
                .thenReturn(Optional.of(user));
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/profile"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testProfileWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1).setUsername("user1").setPassword("pass123");
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/notNumber/profile"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testProfileNonExisting() throws IOException, InterruptedException {
        var user = new User().setId(1).setUsername("user1").setPassword("pass123");
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.getUserById(ArgumentCaptor.forClass(Integer.class).capture()))
                .thenReturn(Optional.empty());
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/profile"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        }
    }

    @Test
    public void testUpdateProfileWithoutToken() throws IOException, InterruptedException {
        var user = new User().setId(1)
                .setEmail("user1@example.com")
                .setFavoriteGenre("sci-fi")
                .setPassword("pass123");
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/profile"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testUpdateProfile() throws IOException, InterruptedException {
        var user = new User().setId(1)
                .setEmail("user1@example.com")
                .setFavoriteGenre("sci-fi")
                .setPassword("pass123");
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.updateUser(ArgumentCaptor.forClass(User.class).capture()))
                .thenReturn(Optional.of(user));
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/profile"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertFalse(response.body().contains("pass123"));
        }
    }

    @Test
    public void testUpdateUnknownProfile() throws IOException, InterruptedException {
        var user = new User().setId(1)
                .setEmail("user1@example.com")
                .setFavoriteGenre("sci-fi")
                .setPassword("pass123");
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.updateUser(ArgumentCaptor.forClass(User.class).capture()))
                .thenReturn(Optional.empty());
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString(user);
            var request = HttpRequest.newBuilder(getUri("/api/users/profile"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUpdateProfileWithoutParameters() throws IOException, InterruptedException {
        var user = new User().setId(1)
                .setEmail("user1@example.com")
                .setFavoriteGenre("sci-fi")
                .setPassword("pass123");
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var body = testingWriter.writeValueAsString("");
            var request = HttpRequest.newBuilder(getUri("/api/users/profile"))
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUserRatingsWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/ratings"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testUserRatings() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.getUserRatingHistory(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(List.of());
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/ratings"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testUserRatingsWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/notNumber/ratings"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUserFavoritesWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/favorites"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testUserFavorites() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.getUserFavorites(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(Integer.class).capture()
                ))
                .thenReturn(List.of());
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/favorites"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testUserFavoritesWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/notNumber/favorites"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(400, response.statusCode());
        }
    }

    @Test
    public void testUserRecommendationsWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/recommendations"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testUserRecommendations() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.getUserRecommendations(
                        ArgumentCaptor.forClass(Integer.class).capture(),
                        ArgumentCaptor.forClass(String.class).capture()
                ))
                .thenReturn(List.of());
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/1/recommendations?type=genre"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
        }
    }

    @Test
    public void testUserRecommendationsWithInvalidId() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/users/notNumber/recommendations"))
                    .GET()
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
