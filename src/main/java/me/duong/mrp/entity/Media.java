package me.duong.mrp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.duong.mrp.model.BaseValidator;
import me.duong.mrp.model.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Media extends Entity<Integer> implements BaseValidator {
    private int userId;
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private List<String> genres;
    private int ageRestriction;
    private List<Rating> ratings = new ArrayList<>();
    private float score;

    @Override
    public Media setId(Integer id) {
        super.setId(id);
        return this;
    }

    @JsonIgnore
    public int getUserId() {
        return userId;
    }

    public Media setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public Media setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public Media setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    @JsonProperty("mediaType")
    public Media setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    @JsonProperty("releaseYear")
    public Media setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public List<String> getGenres() {
        return genres;
    }

    @JsonProperty("genres")
    public Media setGenres(List<String> genres) {
        this.genres = genres;
        return this;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    @JsonProperty("ageRestriction")
    public Media setAgeRestriction(int ageRestriction) {
        this.ageRestriction = ageRestriction;
        return this;
    }

    @JsonProperty("ratings")
    public List<Rating> getRatings() {
        return ratings;
    }

    @JsonIgnore
    public Media setRatings(List<Rating> ratings) {
        this.ratings = ratings;
        return this;
    }

    @JsonProperty("score")
    public float getScore() {
        return score;
    }

    @JsonIgnore
    public Media setScore(float score) {
        this.score = score;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Media media && Objects.equals(media.getId(), this.getId());
    }

    @Override
    public boolean validate() {
        return title != null && !title.isBlank() &&
                description != null &&
                mediaType != null && MediaType.containsMediaType(mediaType) &&
                genres != null && ageRestriction >= 0;
    }
}
