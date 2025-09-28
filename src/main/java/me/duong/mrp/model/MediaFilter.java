package me.duong.mrp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record MediaFilter(
        String title,
        String genre,
        String mediaType,
        int releaseYear,
        int ageRestriction,
        float rating,
        String sortBy
) {
    public String buildPreparedStatement() {
        var fields = new ArrayList<String>();
        if (title != null) fields.add("lower(title) LIKE ('%' || lower(?) || '%')");
        if (genre != null) fields.add("lower(genres) LIKE ('%' || lower(?) || '%')");
        if (mediaType != null) fields.add("lower(media_type) LIKE ('%' || lower(?) || '%')");
        if (releaseYear != -1) fields.add("release_year = ?");
        if (ageRestriction != -1) fields.add("age_restriction = ?");
        //if (rating != -1) fields.add("rating = ?");
        var where = String.join(" AND ", fields);
        if (!where.isBlank()) where = "WHERE " + where;
        var order = switch (sortBy) {
            case "title" -> "title";
            case "year" -> "release_year DESC";
            // case "score" -> "rating DESC";
            default -> "";
        };
        if (!order.isBlank()) order = "ORDER BY " + order;
        return String.format("SELECT * FROM media %s %s", where, order);
    }

    public static MediaFilter fromQuery(Map<String, List<String>> queries) {
        var title = getFirstElement(queries, "title", null);
        var genre = getFirstElement(queries, "genre", null);
        var mediaType = getFirstElement(queries, "mediaType", null);
        if (mediaType != null && !MediaType.containsMediaType(mediaType)) {
            mediaType = null;
        }
        var releaseYear = getFirstElement(queries, "releaseYear", "[0-9]+");
        var releaseYearParsed = releaseYear != null ? Integer.parseInt(releaseYear) : -1;
        var ageRestriction = getFirstElement(queries, "ageRestriction", "[0-9]+");
        var ageRestrictionParsed = ageRestriction != null ? Integer.parseInt(ageRestriction) : -1;
        var rating = getFirstElement(queries, "rating", "^([0-9]*[.])?[0-9]+$");
        var ratingParsed = rating != null ? Float.parseFloat(rating) : -1f;
        var sortBy = getFirstElement(queries, "sortBy", null);
        return new MediaFilter(title, genre, mediaType, releaseYearParsed, ageRestrictionParsed, ratingParsed, sortBy);
    }

    private static String getFirstElement(Map<String, List<String>> queries, String key, String checkRegex) {
        return queries.containsKey(key) ? Optional.of(queries.get(key))
                .filter(e -> !e.isEmpty()).map(List::getFirst)
                .filter(e -> checkRegex == null || e.matches(checkRegex))
                .orElse(null) : null;
    }
}
