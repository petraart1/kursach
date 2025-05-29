package com.example.financemanager.model;

public class User {
    private String id;
    private String email;
    private String displayName;
    private UserRole role;
    private long createdAt;
    private boolean isDemoUser;

    public User() {
        // Required empty constructor for Firestore
        this.createdAt = System.currentTimeMillis();
        this.role = UserRole.USER; // По умолчанию обычный пользователь
        this.isDemoUser = false;
    }

    public User(String email, String displayName, UserRole role) {
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
        this.isDemoUser = false;
    }

    public static User createDemoUser() {
        User demoUser = new User(
            "demo@example.com",
            "Demo User",
            UserRole.USER
        );
        demoUser.setDemoUser(true);
        return demoUser;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDemoUser() {
        return isDemoUser;
    }

    public void setDemoUser(boolean demoUser) {
        isDemoUser = demoUser;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
} 