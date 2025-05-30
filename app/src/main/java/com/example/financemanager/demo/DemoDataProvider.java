package com.example.financemanager.demo;

import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DemoDataProvider {
    private static List<Transaction> demoTransactions;
    private static double totalBalance = 0.0;

    public static void initializeDemoData() {
        demoTransactions = new ArrayList<>();
        totalBalance = 0.0;
        
        // Добавляем демо-транзакции
        addDemoTransaction("Зарплата", "Ежемесячная зарплата", 50000, TransactionType.INCOME, "Доход");
        addDemoTransaction("Продуктовый магазин", "Еженедельная закупка", 5000, TransactionType.EXPENSE, "Продукты");
        addDemoTransaction("Кафе", "Обед с коллегами", 1200, TransactionType.EXPENSE, "Рестораны");
        addDemoTransaction("Подработка", "Фриланс проект", 15000, TransactionType.INCOME, "Доход");
        addDemoTransaction("Коммунальные услуги", "Счет за месяц", 4500, TransactionType.EXPENSE, "ЖКХ");
        addDemoTransaction("Развлечения", "Кино с друзьями", 800, TransactionType.EXPENSE, "Развлечения");
        addDemoTransaction("Транспорт", "Проездной на месяц", 2000, TransactionType.EXPENSE, "Транспорт");
        addDemoTransaction("Одежда", "Новая куртка", 3500, TransactionType.EXPENSE, "Одежда");
    }

    private static void addDemoTransaction(String title, String description, double amount, 
                                         TransactionType type, String category) {
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                title,
                description,
                amount,
                type,
                category,
                new Date()
        );
        demoTransactions.add(transaction);
        updateBalance(transaction);
    }

    public static List<Transaction> getDemoTransactions() {
        if (demoTransactions == null) {
            initializeDemoData();
        }
        return new ArrayList<>(demoTransactions);
    }

    public static void addTransaction(Transaction transaction) {
        if (demoTransactions == null) {
            initializeDemoData();
        }
        demoTransactions.add(transaction);
        updateBalance(transaction);
    }

    public static double getDemoTotalBalance() {
        if (demoTransactions == null) {
            initializeDemoData();
        }
        return totalBalance;
    }

    private static void updateBalance(Transaction transaction) {
        if (transaction.getType() == TransactionType.INCOME) {
            totalBalance += transaction.getAmount();
        } else {
            totalBalance -= transaction.getAmount();
        }
    }
} 