package com.example.kursach;

public class Transaction {
    public String id;
    public String userId;
    public double amount;
    public String category;
    public String description;
    public long timestamp;
    public String type; // "income" или "expense"

    public Transaction() {}

    public Transaction(String id, String userId, double amount, String category, String description, long timestamp, String type) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.timestamp = timestamp;
        this.type = type;
    }
} 