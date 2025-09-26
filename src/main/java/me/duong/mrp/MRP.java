package me.duong.mrp;

import com.sun.net.httpserver.HttpServer;
import me.duong.mrp.presentation.DefaultHttpHandler;
import me.duong.mrp.utils.*;
import me.duong.mrp.presentation.Controller;
import me.duong.mrp.presentation.Mapping;
import me.duong.mrp.presentation.Request;
import me.duong.mrp.presentation.Responders;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;

public class MRP {

    public static void main(String[] args) {
        try {
            initControllers();

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/", new DefaultHttpHandler());
            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            server.start();

            Logger.info("Server running on port 8080...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initControllers() {
        AnnotationScanner.scanAnnotations("me.duong.mrp.controller", Controller.class, method -> {
            var methodType = method.getAnnotation(Controller.class).method();
            var path = method.getAnnotation(Controller.class).path();
            var authRequired = method.getAnnotation(Controller.class).authRequired();
            if (method.getParameterCount() != 1 ||
                    method.getParameterTypes()[0] != Request.class) {
                Logger.error(
                        "Controller with method \"%s\" and path \"%s\" has invalid return or parameter types!",
                        methodType,
                        path
                );
                return;
            }
            ControllerStore.INSTANCE.addController(
                    new Mapping(methodType, path, authRequired),
                    request -> invokeMethod(method, request)
            );
            Logger.info(
                    "Registered controller \"%s\" - \"%s\" for \"%s\"!",
                    methodType,
                    path,
                    method.getName()
            );
        });
    }

    private static void invokeMethod(Method method, Request request) {
        try {
            var access = method.canAccess(null);
            if (!access) {
                method.setAccessible(true);
            }
            method.invoke(null, request);
            if (!access) {
                method.setAccessible(false);
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            Logger.error("%s: %s", method.getName(), exception.getMessage());
            Responders.sendResponse(request, 500);
        }
    }
}
