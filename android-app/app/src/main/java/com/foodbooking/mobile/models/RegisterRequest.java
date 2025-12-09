package com.foodbooking.mobile.models;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;

    public RegisterRequest(String username, String password, String email,
                          String fullName, String phoneNumber, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getRole() { return role; }
}
