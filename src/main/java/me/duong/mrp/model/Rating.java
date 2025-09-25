package me.duong.mrp.model;

public class Rating implements BaseValidator {
    private int stars;
    private String comment;
    private long createdAt;

    public int getStars() {
        return stars;
    }

    public Rating setStars(int stars) {
        this.stars = stars;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Rating setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public Rating setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public boolean validate() {
        return false;
    }
}
