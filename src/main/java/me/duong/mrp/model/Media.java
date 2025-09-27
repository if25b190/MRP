package me.duong.mrp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Media extends Entity<Integer> implements BaseValidator {
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private List<String> genres;
    private int ageRestriction;

    @Override
    public Media setId(Integer id) {
        super.setId(id);
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

    @Override
    public boolean validate() {
        return title != null && !title.isBlank() &&
                description != null &&
                mediaType != null && MediaType.containsMediaType(mediaType) &&
                genres != null && ageRestriction >= 0;
    }
}
