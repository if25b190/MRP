package me.duong.mrp.model;

public final class Rating extends BaseValidator {
    private final int stars;
    private final String comment;
    private final long createdAt;

    public Rating(int stars, String comment, long createdAt) {
        this.stars = stars;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int stars() {
        return stars;
    }

    public String comment() {
        return comment;
    }

    public long createdAt() {
        return createdAt;
    }

    @Override
    public boolean validate() {
        return false;
    }
}
