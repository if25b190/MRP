package me.duong.mrp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import me.duong.mrp.model.BaseValidator;
import me.duong.mrp.utils.parser.Views;

public class User extends Entity<Integer> implements BaseValidator {
    private String username;
    private String email;
    private String password;
    private String salt;
    private String favoriteGenre;

    @Override
    public User setId(Integer id) {
        super.setId(id);
        return this;
    }

    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    @JsonView(Views.Testing.class)
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    @JsonIgnore
    public String getSalt() {
        return salt;
    }

    @JsonIgnore
    public User setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    @JsonProperty("favoriteGenre")
    public User setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
        return this;
    }

    @Override
    public boolean validate() {
        return username != null && !username.isBlank() && password != null && !password.isBlank();
    }
}
