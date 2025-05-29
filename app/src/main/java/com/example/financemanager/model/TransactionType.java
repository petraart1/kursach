package com.example.financemanager.model;

public enum TransactionType {
    INCOME("Доход"),
    EXPENSE("Расход");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 