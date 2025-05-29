package com.example.financemanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financemanager.demo.DemoDataProvider;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.repository.UserRepository;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private UserRepository userRepository;
    private TextView balanceText;
    private TextView recentTransactionsText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        userRepository = new UserRepository();
        balanceText = view.findViewById(R.id.balanceText);
        recentTransactionsText = view.findViewById(R.id.recentTransactionsText);

        boolean isDemoMode = getMainActivity().isDemoMode();
        if (isDemoMode) {
            loadDemoData();
        } else {
            loadUserData();
        }
    }

    private void loadDemoData() {
        // Загружаем демо-данные
        double balance = DemoDataProvider.getDemoTotalBalance();
        List<Transaction> transactions = DemoDataProvider.getDemoTransactions();
        
        updateUI(balance, transactions);
    }

    private void loadUserData() {
        // TODO: Загрузка реальных данных пользователя
        // Пока показываем заглушку
        updateUI(0.0, null);
    }

    private void updateUI(double balance, List<Transaction> transactions) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        balanceText.setText(format.format(balance));

        if (transactions != null && !transactions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Transaction transaction : transactions) {
                sb.append(format.format(transaction.getAmount()))
                  .append(" - ")
                  .append(transaction.getDescription())
                  .append("\n");
            }
            recentTransactionsText.setText(sb.toString());
        } else {
            recentTransactionsText.setText("Нет недавних транзакций");
        }
    }

    private MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }
} 