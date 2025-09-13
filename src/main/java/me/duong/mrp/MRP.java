package me.duong.mrp;

import com.sun.net.httpserver.HttpServer;
import me.duong.mrp.controller.UserController;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MRP {
    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            server.createContext("/", new UserController());

            server.setExecutor(null);
            server.start();

            System.out.println("Server running on port 8080...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
