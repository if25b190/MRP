package me.duong.mrp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.duong.mrp.model.BaseValidator;

import java.time.OffsetDateTime;

public class Rating extends Entity<Integer> implements BaseValidator {
    private int userId;
    private int mediaId;
    private int stars;
    private String comment;
    private boolean isConfirmed;
    private OffsetDateTime createdAt;
    private int likes;

    @Override
    public Rating setId(Integer id) {
        super.setId(id);
        return this;
    }

    @JsonIgnore
    public int getUserId() {
        return userId;
    }

    public Rating setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    @JsonIgnore
    public int getMediaId() {
        return mediaId;
    }

    public Rating setMediaId(int mediaId) {
        this.mediaId = mediaId;
        return this;
    }

    public int getStars() {
        return stars;
    }

    @JsonProperty("stars")
    public Rating setStars(int stars) {
        this.stars = stars;
        return this;
    }

    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public Rating setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public Rating setConfirmed(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
        return this;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Rating setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @JsonProperty("likes")
    public int getLikes() {
        return likes;
    }

    @JsonIgnore
    public Rating setLikes(int likes) {
        this.likes = likes;
        return this;
    }

    @Override
    public boolean validate() {
        return stars >= 1 && stars <= 5;
    }
}
