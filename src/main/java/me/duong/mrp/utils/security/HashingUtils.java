package me.duong.mrp.utils.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;

public class HashingUtils {
    public static byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static Optional<String> hashPassword(String password, byte[] salt) {
        byte[] result = pbkdf2Hash(password, salt);
        return result != null ? Optional.of(Base64.getEncoder().encodeToString(result)) : Optional.empty();
    }

    private static byte[] pbkdf2Hash(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 16384, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }
}
