package me.duong.mrp;

import com.sun.net.httpserver.HttpServer;
import me.duong.mrp.controller.DefaultHttpHandler;
import me.duong.mrp.utils.AnnotationScanner;
import me.duong.mrp.utils.Controller;
import me.duong.mrp.utils.Mapping;
import me.duong.mrp.utils.Request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MRP {
    public static final Map<Mapping, Consumer<Request>> controllers = new HashMap<>();

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
            if (method.getParameterCount() != 1 ||
                    method.getParameterTypes()[0] != Request.class) {
                Logger.error(
                        "Controller with method \"%s\" and path \"%s\" has invalid return or parameter types!",
                        methodType,
                        path
                );
                return;
            }
            controllers.put(new Mapping(methodType, path), request -> invokeMethod(method, request));
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
            Logger.error("%s: %s", method.getName(), exception.getLocalizedMessage());
        }
    }
}
