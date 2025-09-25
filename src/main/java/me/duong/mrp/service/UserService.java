package me.duong.mrp.service;

import me.duong.mrp.Logger;
import me.duong.mrp.utils.security.TokenStore;
import me.duong.mrp.model.User;
import me.duong.mrp.repository.DbException;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.UserRepository;
import me.duong.mrp.utils.security.HashingUtils;

import java.util.Base64;
import java.util.Optional;

public class UserService {

    public Optional<String> loginUser(User loginDto) {
        DbSession session = new DbSession();
        try (session) {
            UserRepository userRepository = new UserRepository(session);
            var result = userRepository.findUserByUsername(loginDto.getUsername());
            if (result.isPresent()) {
                var user = result.get();
                var salt = Base64.getDecoder().decode(user.getSalt());
                var pass = HashingUtils.hashPassword(loginDto.getPassword(), salt);
                if (pass.filter(s -> s.equals(user.getPassword())).isPresent()) {
                    return Optional.of(TokenStore.INSTANCE.createToken(user));
                }
            }
            return Optional.empty();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception);
        }
    }

    public Optional<User> registerUser(User loginDto) {
        DbSession session = new DbSession();
        try (session) {
            UserRepository userRepository = new UserRepository(session);
            var salt = HashingUtils.createSalt();
            var password = HashingUtils.hashPassword(loginDto.getPassword(), salt);
            if (password.isEmpty()) {
                return Optional.empty();
            }
            User user = new User()
                    .setUsername(loginDto.getUsername())
                    .setPassword(password.get())
                    .setSalt(Base64.getEncoder().encodeToString(salt));
            user = userRepository.insertUser(user);
            session.commit();
            return Optional.of(user);
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception);
        }
    }

}
