package me.duong.mrp.dto;

public class LoginDto extends BaseDto {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean validate() {
        return username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty();
    }
}
