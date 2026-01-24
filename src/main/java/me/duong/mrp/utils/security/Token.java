package me.duong.mrp.utils.security;

public class Token {
    private static final long TOKEN_DURATION_MILLIS = 600_000;

    private final Integer id;
    private long validDuration;

    public Token(Integer id) {
        this.id = id;
        this.validDuration = System.currentTimeMillis() + TOKEN_DURATION_MILLIS;
    }

    public Integer getId() {
        return id;
    }

    public boolean isValid() {
        return id != -1 && getValidDuration() > System.currentTimeMillis();
    }

    public void refreshValidDuration() {
        this.validDuration = System.currentTimeMillis() + TOKEN_DURATION_MILLIS;
    }

    public long getValidDuration() {
        return validDuration;
    }
}
