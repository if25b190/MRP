package me.duong.mrp.model;

public enum MediaType {
    MOVIE,
    SERIES,
    GAME;

    public static boolean containsMediaType(String mediaType) {
        try  {
            MediaType.valueOf(mediaType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
