package me.duong.mrp.service;

import me.duong.mrp.Logger;
import me.duong.mrp.TokenStore;
import me.duong.mrp.dto.LoginDto;
import me.duong.mrp.dto.UserDto;
import me.duong.mrp.model.User;
import me.duong.mrp.repository.DbSession;
import me.duong.mrp.repository.UserRepository;
import me.duong.mrp.utils.security.HashingUtils;

import java.util.Base64;
import java.util.Optional;

public class UserService {

    public Optional<String> loginUser(LoginDto loginDto) {
        DbSession session = new DbSession();
        try (session) {
            UserRepository userRepository = new UserRepository(session);
            var result = userRepository.findUserByUsername(loginDto.getUsername());
            if (result.isPresent()) {
                var user = result.get();
                var salt = Base64.getDecoder().decode(user.salt());
                var pass = HashingUtils.hashPassword(loginDto.getPassword(), salt);
                if (pass.filter(s -> s.equals(user.password())).isPresent()) {
                    return Optional.of(TokenStore.createToken(user));
                }
            }
            return Optional.empty();
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getLocalizedMessage());
            session.rollback();
            return Optional.empty();
        }
    }

    public Optional<UserDto> registerUser(LoginDto loginDto) {
        DbSession session = new DbSession();
        try (session) {
            // add check if username is taken
            var salt = HashingUtils.createSalt();
            var password = HashingUtils.hashPassword(loginDto.getPassword(), salt);
            if (password.isEmpty()) {
                return Optional.empty();
            }
            UserRepository userRepository = new UserRepository(session);
            User user = new User(
                    -1,
                    loginDto.getUsername(),
                    password.get(),
                    Base64.getEncoder().encodeToString(salt)
            );
            var result = userRepository.insertUser(user);
            if (!result) {
                return Optional.empty();
            }
            session.commit();
            return Optional.of(new UserDto(user.username()));
        } catch (Exception exception) {
            Logger.error("Session failed to execute: %s", exception.getLocalizedMessage());
            session.rollback();
            return Optional.empty();
        }
    }

}
