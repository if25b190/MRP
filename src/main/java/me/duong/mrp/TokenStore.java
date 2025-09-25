package me.duong.mrp;

import me.duong.mrp.model.User;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TokenStore {
    private static final Map<String, Integer> tokens = new HashMap<>();

    public static String createToken(User user) {
        var token = Base64.getEncoder()
                .encodeToString(String.format(
                        "%s-%d",
                        user.getId(),
                        System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8)
                );
        tokens.put(token, user.getId());
        return token;
    }

    public static int verifyToken(String token) {
        if (token == null) return -1;
        var temp = token.contains("Bearer ") ?  token.split(" ")[1] : token;
        return tokens.getOrDefault(temp, -1);
    }
}
