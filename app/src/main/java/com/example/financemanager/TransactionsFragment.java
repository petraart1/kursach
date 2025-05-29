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
        binding.typeFilterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // Если ни один чип не выбран, выбираем "Все"
                binding.allTypeChip.setChecked(true);
                return;
            }

            Chip checkedChip = group.findViewById(checkedIds.get(0));
            if (checkedChip == null) return;

            if (checkedChip.getId() == R.id.allTypeChip) {
                adapter.setTransactions(allTransactions);
            } else if (checkedChip.getId() == R.id.incomeChip) {
                filterTransactions(TransactionType.INCOME);
            } else if (checkedChip.getId() == R.id.expenseChip) {
                filterTransactions(TransactionType.EXPENSE);
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
            allTransactions = new ArrayList<>(DemoDataProvider.getDemoTransactions());
            updateTransactionsList();
        } else {
            transactionRepository.getTransactions()
                .addOnSuccessListener(querySnapshot -> {
                    allTransactions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Transaction transaction = document.toObject(Transaction.class);
                        transaction.setId(document.getId());
                        allTransactions.add(transaction);
                    }
                    updateTransactionsList();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), 
                        R.string.error_loading_transactions, Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void filterTransactions(TransactionType type) {
        List<Transaction> filtered = allTransactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .collect(Collectors.toList());
        adapter.setTransactions(filtered);
    }

    private void updateTransactionsList() {
        // Обновляем список в соответствии с текущим фильтром
        Chip checkedChip = binding.typeFilterChipGroup.findViewById(
                binding.typeFilterChipGroup.getCheckedChipId());
        
        if (checkedChip == null || checkedChip.getId() == R.id.allTypeChip) {
            adapter.setTransactions(allTransactions);
        } else if (checkedChip.getId() == R.id.incomeChip) {
            filterTransactions(TransactionType.INCOME);
        } else if (checkedChip.getId() == R.id.expenseChip) {
            filterTransactions(TransactionType.EXPENSE);
        }
    }

    @Override
    public void onTransactionAdded(Transaction transaction) {
        if (!isDemoMode) {
            // TODO: Сохранение транзакции в Firebase
            Toast.makeText(requireContext(), "Функция добавления пока не реализована", Toast.LENGTH_SHORT).show();
            return;
        }

        // Добавляем транзакцию в начало списка
        allTransactions.add(0, transaction);
        updateTransactionsList();
        Toast.makeText(requireContext(), "Транзакция добавлена", Toast.LENGTH_SHORT).show();
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