package com.example.financemanager.model;

public enum UserRole {
    USER("пользователь"),
    ADMIN("администратор");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 