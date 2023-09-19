package com.example.loansystem.dto;

public class UserDTO {
    private String userEmail;
    private String password;

    public UserDTO() {
    }

    public UserDTO(String userEmail, String password) {
        this.userEmail = userEmail;
        this.password = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String username) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
