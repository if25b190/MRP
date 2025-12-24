package me.duong.mrp.repository;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import me.duong.mrp.TestDbConnection;
import me.duong.mrp.entity.User;
import me.duong.mrp.repository.impl.MediaRepositoryImpl;
import me.duong.mrp.repository.impl.RatingRepositoryImpl;
import me.duong.mrp.repository.impl.UserRepositoryImpl;
import me.duong.mrp.service.impl.UserServiceImpl;
import me.duong.mrp.utils.Injector;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
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
    public void testRegisterUser2() {
        var userService = new UserServiceImpl();
        userService.registerUser(new User().setUsername("user2").setPassword("pass123"));
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user2", result.get().getUsername());
        assertNotEquals("pass123", result.get().getPassword());
        assertNotNull(result.get().getSalt());
        assertFalse(result.get().getSalt().isBlank());
    }

    @Test
    public void testRegisterUser3() {
        var userService = new UserServiceImpl();
        userService.registerUser(new User().setUsername("user3").setPassword("pass123"));
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user3", result.get().getUsername());
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
