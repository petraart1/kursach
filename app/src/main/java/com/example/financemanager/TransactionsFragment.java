package com.example.financemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financemanager.adapter.TransactionsAdapter;
import com.example.financemanager.databinding.FragmentTransactionsBinding;
import com.example.financemanager.demo.DemoDataProvider;
import com.example.financemanager.dialog.AddTransactionDialog;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;
import com.google.android.material.chip.Chip;
import com.example.financemanager.repository.TransactionRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionsFragment extends Fragment implements AddTransactionDialog.OnTransactionAddedListener {
    private FragmentTransactionsBinding binding;
    private TransactionsAdapter adapter;
    private List<Transaction> allTransactions;
    private TransactionRepository transactionRepository;
    private boolean isDemoMode;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        
        isDemoMode = ((MainActivity) requireActivity()).isDemoMode();
        transactionRepository = new TransactionRepository();
        setupRecyclerView();
        setupFilterChips();
        setupAddButton();
        refreshTransactions();
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupRecyclerView() {
        adapter = new TransactionsAdapter();
        binding.transactionsRecyclerView.setAdapter(adapter);
        binding.transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupFilterChips() {
        binding.typeFilterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.allTypeChip) {
                adapter.submitList(allTransactions);
            } else if (checkedId == R.id.incomeChip) {
                List<Transaction> filtered = allTransactions.stream()
                        .filter(t -> t.getType() == TransactionType.INCOME)
                        .collect(Collectors.toList());
                adapter.submitList(filtered);
            } else if (checkedId == R.id.expenseChip) {
                List<Transaction> filtered = allTransactions.stream()
                        .filter(t -> t.getType() == TransactionType.EXPENSE)
                        .collect(Collectors.toList());
                adapter.submitList(filtered);
            }
        });
    }

    private void setupAddButton() {
        binding.addTransactionFab.setOnClickListener(v -> {
            AddTransactionDialog dialog = AddTransactionDialog.newInstance();
            dialog.setOnTransactionAddedListener(this);
            dialog.show(getChildFragmentManager(), "AddTransactionDialog");
        });
    }

    public void refreshTransactions() {
        if (isDemoMode) {
            allTransactions = DemoDataProvider.getDemoTransactions();
            adapter.submitList(allTransactions);
        } else {
            transactionRepository.getUserTransactions()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        allTransactions = queryDocumentSnapshots.toObjects(Transaction.class);
                        adapter.submitList(allTransactions);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), 
                                "Ошибка при загрузке транзакций", 
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    public void onTransactionAdded(Transaction transaction) {
        refreshTransactions();
    }

    private MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 