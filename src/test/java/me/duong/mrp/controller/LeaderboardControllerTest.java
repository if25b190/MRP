package me.duong.mrp.controller;

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
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class LeaderboardControllerTest {
    private static final UserService USER_SERVICE = mock(UserService.class);
    private static final RestServer REST_SERVER = new RestServer(8081);

    @BeforeAll
    public static void setUp() {
        Injector.INSTANCE.register(UserService.class, USER_SERVICE);
        REST_SERVER.start();
    }

    @Test
    public void testGetLeaderboardWithoutToken() throws IOException, InterruptedException {
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/leaderboard"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(401, response.statusCode());
        }
    }

    @Test
    public void testGetLeaderboard() throws IOException, InterruptedException {
        var user = new User().setId(1);
        var token = TokenStore.INSTANCE.createToken(user);
        lenient().when(USER_SERVICE.getLeaderboard())
                .thenReturn(List.of());
        try (var client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder(getUri("/api/leaderboard"))
                    .GET()
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
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
