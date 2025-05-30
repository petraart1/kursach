package com.example.financemanager.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.databinding.ItemTransactionBinding;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionsAdapter extends ListAdapter<Transaction, TransactionsAdapter.ViewHolder> {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

    public TransactionsAdapter() {
        super(new TransactionDiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        ViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Transaction transaction) {
            binding.transactionTitle.setText(transaction.getTitle());
            binding.transactionDescription.setText(transaction.getDescription());
            
            String amount = currencyFormat.format(transaction.getAmount());
            if (transaction.getType() == TransactionType.EXPENSE) {
                binding.transactionAmount.setTextColor(
                        binding.getRoot().getContext().getColor(android.R.color.holo_red_dark));
                amount = "-" + amount;
            } else {
                binding.transactionAmount.setTextColor(
                        binding.getRoot().getContext().getColor(android.R.color.holo_green_dark));
                amount = "+" + amount;
            }
            binding.transactionAmount.setText(amount);
        }
    }

    private static class TransactionDiffCallback extends DiffUtil.ItemCallback<Transaction> {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.equals(newItem);
        }
    }
} 