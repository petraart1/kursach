package com.example.financemanager.data;

import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DemoDataProvider {
    private static final List<Transaction> demoTransactions = new ArrayList<>();
    private static double demoTotalBalance = 0.0;

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
        demoTransactions.add(transaction);
        demoTotalBalance += type == TransactionType.INCOME ? amount : -amount;
    }

    public static List<Transaction> getDemoTransactions() {
        return new ArrayList<>(demoTransactions);
    }

    public static double getDemoTotalBalance() {
        return demoTotalBalance;
    }

    public static void addTransaction(Transaction transaction) {
        demoTransactions.add(transaction);
        demoTotalBalance += transaction.getType() == TransactionType.INCOME ? 
            transaction.getAmount() : -transaction.getAmount();
    }
} 