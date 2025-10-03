package me.duong.mrp.repository;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import me.duong.mrp.entity.User;
import me.duong.mrp.repository.impl.UserRepositoryImpl;
import me.duong.mrp.utils.Injector;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {
    private static EmbeddedPostgres pg;
    private static Connection connection;
    private static Savepoint savepoint;

    @BeforeAll
    public static void beforeAll() throws IOException, SQLException {
        pg = EmbeddedPostgres.builder().start();
        connection = pg.getPostgresDatabase().getConnection();
        try (var resource = Thread.currentThread().getContextClassLoader().getResourceAsStream("database.sql")) {
            if (resource == null) {
                throw new IllegalStateException("Cannot find database resource");
            }
            var schema = IOUtils.toString(resource, StandardCharsets.UTF_8);
            connection.prepareStatement(schema).executeUpdate();
        }
        Injector.INSTANCE.register(Connection.class, connection);
        Injector.INSTANCE.register(DbSession.class, DbSession.class);
    }

    @BeforeEach
    public void beforeEach() throws SQLException {
        connection.setAutoCommit(false);
        savepoint = connection.setSavepoint();
    }

    @Test
    public void testFindUserById() {
        UserRepository repository = new UserRepositoryImpl(Injector.INSTANCE.resolve(DbSession.class));
        repository.insertUser(new User().setUsername("user1").setPassword("pass123").setSalt("test"));
        var result = repository.findUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());
        assertEquals("pass123", result.get().getPassword());
        assertEquals("test", result.get().getSalt());
    }

    @Test
    public void testFindNonExistingUserById() {
        UserRepository repository = new UserRepositoryImpl(Injector.INSTANCE.resolve(DbSession.class));
        var result = repository.findUserById(1);
        assertFalse(result.isPresent());
    }

    @AfterEach
    public void afterEach() throws SQLException {
        connection.rollback(savepoint);
    }

    @AfterAll
    public static void afterAll() throws IOException, SQLException {
        if (connection != null) connection.close();
        pg.close();
    }
}
