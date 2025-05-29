package com.example.financemanager.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.databinding.ItemTransactionBinding;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TransactionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.bind(transactions.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        TransactionViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Transaction transaction) {
            binding.transactionTitle.setText(transaction.getTitle());
            binding.transactionDescription.setText(transaction.getDescription());
            binding.transactionDate.setText(dateFormat.format(transaction.getDate()));

            // Форматируем сумму и устанавливаем цвет в зависимости от типа транзакции
            String amount = currencyFormat.format(Math.abs(transaction.getAmount()));
            if (transaction.getType() == TransactionType.INCOME) {
                binding.transactionAmount.setTextColor(binding.getRoot().getContext()
                        .getColor(android.R.color.holo_green_dark));
                binding.transactionAmount.setText("+" + amount);
            } else {
                binding.transactionAmount.setTextColor(binding.getRoot().getContext()
                        .getColor(android.R.color.holo_red_dark));
                binding.transactionAmount.setText("-" + amount);
            }
        }
    }
} 