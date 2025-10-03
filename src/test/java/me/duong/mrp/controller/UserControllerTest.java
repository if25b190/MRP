package me.duong.mrp.controller;

import com.fasterxml.jackson.databind.ObjectWriter;
import me.duong.mrp.RestServer;
import me.duong.mrp.entity.User;
import me.duong.mrp.service.UserService;
import me.duong.mrp.utils.Injector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static me.duong.mrp.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

public class UserControllerTest {
    private static final UserService USER_SERVICE = mock(UserService.class);
    private final ObjectWriter testingWriter = testingWriter();
    private final ObjectWriter publicWriter = publicWriter();

    @BeforeAll
    public static void setUp() {
        Injector.INSTANCE.register(UserService.class, USER_SERVICE);
        RestServer.INSTANCE.start();
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

    @AfterAll
    public static void tearDown() {
        RestServer.INSTANCE.stop();
    }
}
