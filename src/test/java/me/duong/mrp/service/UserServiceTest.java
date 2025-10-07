package me.duong.mrp.service;

import me.duong.mrp.entity.User;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.MediaRepository;
import me.duong.mrp.repository.RatingRepository;
import me.duong.mrp.repository.UserRepository;
import me.duong.mrp.service.impl.UserServiceImpl;
import me.duong.mrp.utils.Injector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final UserRepository USER_REPOSITORY = mock(UserRepository.class);
    private static final MediaRepository MEDIA_REPOSITORY = mock(MediaRepository.class);
    private static final RatingRepository RATING_REPOSITORY = mock(RatingRepository.class);
    private static final DbSession DB_SESSION = mock(DbSession.class);

    @BeforeAll
    public static void beforeAll() {
        Injector.INSTANCE.register(UserRepository.class, USER_REPOSITORY);
        Injector.INSTANCE.register(MediaRepository.class, MEDIA_REPOSITORY);
        Injector.INSTANCE.register(RatingRepository.class, RATING_REPOSITORY);
        Injector.INSTANCE.register(DbSession.class, DB_SESSION);
    }

    @Test
    public void testGetUserById() {
        lenient().when(USER_REPOSITORY.findUserById(ArgumentCaptor.forClass(Integer.class).capture()))
                .thenReturn(Optional.of(new User().setId(1).setUsername("user1")));
        var userService = new UserServiceImpl();
        var result = userService.getUserById(1);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("user1", result.get().getUsername());
    }

    @Test
    public void testGetNonExistingUserById() {
        lenient().when(USER_REPOSITORY.findUserById(ArgumentCaptor.forClass(Integer.class).capture()))
                .thenReturn(Optional.empty());
        var userService = new UserServiceImpl();
        var result = userService.getUserById(1);
        assertFalse(result.isPresent());
    }

}
