package com.example.financemanager.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.databinding.DialogAddTransactionBinding;
import com.example.financemanager.demo.DemoDataProvider;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;
import com.example.financemanager.repository.TransactionRepository;

import java.util.Date;

public class AddTransactionDialog extends DialogFragment {
    private DialogAddTransactionBinding binding;
    private TransactionRepository transactionRepository;
    private OnTransactionAddedListener listener;

    public interface OnTransactionAddedListener {
        void onTransactionAdded(Transaction transaction);
    }

    public static AddTransactionDialog newInstance() {
        return new AddTransactionDialog();
    }

    public void setOnTransactionAddedListener(OnTransactionAddedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogAddTransactionBinding.inflate(getLayoutInflater());
        transactionRepository = new TransactionRepository();

        setupCategorySpinner();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.add_transaction)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.add, null)
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
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
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
            binding.titleInputLayout.setError(getString(R.string.error_required));
            return false;
        }

        String amountStr = binding.amountInput.getText().toString().trim();
        if (TextUtils.isEmpty(amountStr)) {
            binding.amountInputLayout.setError(getString(R.string.error_required));
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                binding.amountInputLayout.setError(getString(R.string.error_positive_amount));
                return false;
            }
        } catch (NumberFormatException e) {
            binding.amountInputLayout.setError(getString(R.string.error_invalid_amount));
            return false;
        }

        return true;
    }

    private void addTransaction() {
        String title = binding.titleInput.getText().toString().trim();
        String description = binding.descriptionInput.getText().toString().trim();
        double amount = Double.parseDouble(binding.amountInput.getText().toString().trim());
        TransactionType type = binding.expenseRadio.isChecked() ? 
            TransactionType.EXPENSE : TransactionType.INCOME;
        String category = binding.categorySpinner.getSelectedItem().toString();

        Transaction transaction = new Transaction(
            "demo_user", // TODO: Replace with actual user ID
            title,
            description,
            amount,
            type,
            category,
            new Date()
        );

        if (((MainActivity) requireActivity()).isDemoMode()) {
            DemoDataProvider.addTransaction(transaction);
            if (listener != null) {
                listener.onTransactionAdded(transaction);
            }
        } else {
            transactionRepository.addTransaction(transaction)
                .addOnSuccessListener(documentReference -> {
                    if (listener != null) {
                        listener.onTransactionAdded(transaction);
                    }
                    Toast.makeText(getContext(), R.string.transaction_added, Toast.LENGTH_SHORT).show();
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