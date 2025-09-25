package me.duong.mrp.service;

import me.duong.mrp.Logger;
import me.duong.mrp.TokenStore;
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
                    return Optional.of(TokenStore.createToken(user));
                }
            }
            return Optional.empty();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            return Optional.empty();
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
            User user = new User(
                    -1,
                    loginDto.getUsername(),
                    password.get(),
                    Base64.getEncoder().encodeToString(salt),
                    null
            );
            var result = userRepository.insertUser(user);
            if (!result) {
                return Optional.empty();
            }
            session.commit();
            return Optional.of(user);
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getMessage());
            session.rollback();
            throw new DbException(exception.getMessage());
        }
    }

}
