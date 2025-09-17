package me.duong.mrp.model;

import java.util.List;

public record Media(String title, String description, String mediaType, int releaseYear, List<String> genres,
                    int ageRestriction) {
}
