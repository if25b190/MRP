package me.duong.mrp;

import me.duong.mrp.repository.*;
import me.duong.mrp.repository.impl.MediaRepositoryImpl;
import me.duong.mrp.repository.impl.RatingRepositoryImpl;
import me.duong.mrp.repository.impl.UserRepositoryImpl;
import me.duong.mrp.service.MediaService;
import me.duong.mrp.service.RatingService;
import me.duong.mrp.service.UserService;
import me.duong.mrp.service.impl.MediaServiceImpl;
import me.duong.mrp.service.impl.RatingServiceImpl;
import me.duong.mrp.service.impl.UserServiceImpl;
import me.duong.mrp.utils.Injector;

import java.sql.Connection;
import java.util.function.Supplier;

public class MRP {
    public static void main(String[] args) {
        Supplier<Connection> connection = DbConnection.INSTANCE::getConnection;
        Injector.INSTANCE.register(Connection.class, connection);
        Injector.INSTANCE.register(DbSession.class, DbSession.class);
        Injector.INSTANCE.register(UserRepository.class, UserRepositoryImpl.class);
        Injector.INSTANCE.register(MediaRepository.class, MediaRepositoryImpl.class);
        Injector.INSTANCE.register(RatingRepository.class, RatingRepositoryImpl.class);
        Injector.INSTANCE.register(UserService.class, UserServiceImpl.class);
        Injector.INSTANCE.register(MediaService.class, MediaServiceImpl.class);
        Injector.INSTANCE.register(RatingService.class, RatingServiceImpl.class);
        RestServer.INSTANCE.start();
    }
}
