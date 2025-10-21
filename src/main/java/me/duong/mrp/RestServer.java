package me.duong.mrp;

import com.sun.net.httpserver.HttpServer;
import me.duong.mrp.presentation.*;
import me.duong.mrp.utils.AnnotationScanner;
import me.duong.mrp.utils.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public enum RestServer {
    INSTANCE;
    private final Map<Mapping, Consumer<Request>> controllers = new HashMap<>();
    private final HttpServer server;

    RestServer() {
        try {
            initControllers();

            server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/", new DefaultHttpHandler());
            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        if (server != null) {
            server.start();
            Logger.info("Server running on port 8080...");
        }
    }

    public void stop() {
        if (server != null) {
            Logger.info("Server shutting down...");
            server.stop(0);
        }
    }

    public String pathToUrl(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return String.format("http://localhost:%d%s", server.getAddress().getPort(), path);
    }

    public Map<Mapping, Consumer<Request>> getControllers() {
        return Collections.unmodifiableMap(controllers);
    }

    private void addController(Mapping mapping, Consumer<Request> controller) {
        controllers.put(mapping, controller);
    }

    private void initControllers() {
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
            addController(
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

    private void invokeMethod(Method method, Request request) {
        try {
            var access = method.canAccess(null);
            if (!access) {
                Logger.error("Method \"%s\" not accessible!", method.getName());
                return;
            }
            method.invoke(null, request);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
            Logger.error("%s: %s", method.getName(), exception.getMessage());
            Responders.sendResponse(request, 500);
        }
    }
}
