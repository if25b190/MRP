package me.duong.mrp;

import me.duong.mrp.service.UserService;
import me.duong.mrp.service.impl.UserServiceImpl;
import me.duong.mrp.utils.Injector;

public class MRP {
    public static void main(String[] args) {
        Injector.INSTANCE.register(UserService.class, UserServiceImpl.class);
        RestServer.INSTANCE.start();
    }
}
