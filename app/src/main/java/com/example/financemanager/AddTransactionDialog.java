package com.example.financemanager;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.financemanager.databinding.DialogAddTransactionBinding;
import com.example.financemanager.demo.DemoDataProvider;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;
import com.example.financemanager.repository.TransactionRepository;
import com.google.firebase.Timestamp;

import java.util.Date;

public class AddTransactionDialog extends DialogFragment {
    private DialogAddTransactionBinding binding;
    private TransactionRepository transactionRepository;
    private ArrayAdapter<String> categoryAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogAddTransactionBinding.inflate(getLayoutInflater());
        transactionRepository = new TransactionRepository();

        setupCategorySpinner();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.add_transaction)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.add, (dialog, which) -> addTransaction())
                .setNegativeButton(R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                if (validateInput()) {
                    addTransaction();
                    dismiss();
                }
            });
        });

        return dialog;
    }

    private void setupCategorySpinner() {
        String[] categories = getResources().getStringArray(R.array.transaction_categories);
        categoryAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
    }

    private boolean validateInput() {
        String title = binding.titleInput.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            binding.titleInputLayout.setError("Обязательное поле");
            return false;
        } else {
            binding.titleInputLayout.setError(null);
        }

        String amountStr = binding.amountInput.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            binding.amountInputLayout.setError("Обязательное поле");
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                binding.amountInputLayout.setError("Сумма должна быть больше 0");
                return false;
            } else {
                binding.amountInputLayout.setError(null);
            }
        } catch (NumberFormatException e) {
            binding.amountInputLayout.setError("Некорректное число");
            return false;
        }

        return true;
    }

    private void addTransaction() {
        String title = binding.titleInput.getText().toString().trim();
        String description = binding.descriptionInput.getText().toString().trim();
        String amountStr = binding.amountInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            binding.titleInputLayout.setError("Обязательное поле");
            return;
        } else {
            binding.titleInputLayout.setError(null);
        }

        if (TextUtils.isEmpty(amountStr)) {
            binding.amountInputLayout.setError("Обязательное поле");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                binding.amountInputLayout.setError("Сумма должна быть больше 0");
                return;
            } else {
                binding.amountInputLayout.setError(null);
            }
        } catch (NumberFormatException e) {
            binding.amountInputLayout.setError("Некорректное число");
            return;
        }

        // Определяем тип транзакции
        TransactionType type = binding.expenseRadio.isChecked() ? 
            TransactionType.EXPENSE : TransactionType.INCOME;

        // Если это расход, делаем сумму отрицательной
        if (type == TransactionType.EXPENSE) {
            amount = -amount;
        }

        String category = binding.categorySpinner.getSelectedItem().toString();

        Transaction transaction = new Transaction(
            "current_user", // TODO: Replace with actual user ID
            title,
            description,
            Math.abs(amount),
            type,
            category,
            new Date()
        );

        if (((MainActivity) requireActivity()).isDemoMode()) {
            DemoDataProvider.addTransaction(transaction);
            ((MainActivity) requireActivity()).refreshTransactions();
        } else {
            transactionRepository.addTransaction(transaction)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), R.string.transaction_added, Toast.LENGTH_SHORT).show();
                    ((MainActivity) requireActivity()).refreshTransactions();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), R.string.error_adding_transaction, Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 