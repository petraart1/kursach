package com.example.financemanager.model;

import java.util.Map;

public class Statistics {
    private final double totalIncome;
    private final double totalExpense;
    private final double balance;
    private final Map<String, Double> expenseByCategory;

    public Statistics(double totalIncome, double totalExpense, double balance, Map<String, Double> expenseByCategory) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.expenseByCategory = expenseByCategory;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public double getBalance() {
        return balance;
    }

    public Map<String, Double> getExpenseByCategory() {
        return expenseByCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Statistics that = (Statistics) o;

        if (Double.compare(that.totalIncome, totalIncome) != 0) return false;
        if (Double.compare(that.totalExpense, totalExpense) != 0) return false;
        if (Double.compare(that.balance, balance) != 0) return false;
        return expenseByCategory != null ? expenseByCategory.equals(that.expenseByCategory) : that.expenseByCategory == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(totalIncome);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(totalExpense);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(balance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (expenseByCategory != null ? expenseByCategory.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "totalIncome=" + totalIncome +
                ", totalExpense=" + totalExpense +
                ", balance=" + balance +
                ", expenseByCategory=" + expenseByCategory +
                '}';
    }
}