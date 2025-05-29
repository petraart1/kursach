package com.example.financemanager.demo;

import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DemoDataProvider {
    private static final List<Transaction> transactions = new ArrayList<>();
    private static double totalBalance = 0.0;

    static {
        // Initialize demo transactions
        addDemoTransaction(
            "Зарплата",
            "Ежемесячная зарплата",
            50000.0,
            TransactionType.INCOME,
            "Зарплата"
        );
        addDemoTransaction(
            "Продукты",
            "Покупка продуктов в магазине",
            5000.0,
            TransactionType.EXPENSE,
            "Продукты"
        );
        addDemoTransaction(
            "Аренда",
            "Оплата квартиры",
            20000.0,
            TransactionType.EXPENSE,
            "Жильё"
        );
        addDemoTransaction(
            "Премия",
            "Квартальная премия",
            15000.0,
            TransactionType.INCOME,
            "Зарплата"
        );
        addDemoTransaction(
            "Транспорт",
            "Проездной на месяц",
            2000.0,
            TransactionType.EXPENSE,
            "Транспорт"
        );
    }

    private static void addDemoTransaction(String title, String description, double amount, 
                                         TransactionType type, String category) {
        Transaction transaction = new Transaction(
            "demo_user",
            title,
            description,
            amount,
            type,
            category,
            new Date()
        );
        transactions.add(transaction);
        totalBalance += type == TransactionType.INCOME ? amount : -amount;
    }

    public static List<Transaction> getDemoTransactions() {
        return new ArrayList<>(transactions);
    }

    public static double getDemoTotalBalance() {
        return totalBalance;
    }

    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        totalBalance += transaction.getType() == TransactionType.INCOME ? 
            transaction.getAmount() : -transaction.getAmount();
    }
} 