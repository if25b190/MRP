package me.duong.mrp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User extends BaseValidator {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String favoriteGenre;

    public User(int id, String username, String password, String salt, String favoriteGenre) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.favoriteGenre = favoriteGenre;
    }

    public int getId() {
        return id;
    }

    @JsonIgnore
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getSalt() {
        return salt;
    }

    @JsonIgnore
    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    @JsonProperty("favoriteGenre")
    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    @Override
    public boolean validate() {
        return username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty();
    }
}
