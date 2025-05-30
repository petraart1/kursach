package com.example.financemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financemanager.databinding.FragmentStatisticsBinding;
import com.example.financemanager.demo.DemoDataProvider;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;
import com.example.financemanager.repository.TransactionRepository;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.financemanager.util.PercentFormatter;

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;
    private TransactionRepository transactionRepository;
    private boolean isDemoMode;
    private NumberFormat currencyFormat;

    private class CurrencyValueFormatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return currencyFormat.format(value);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        transactionRepository = new TransactionRepository();
        isDemoMode = ((MainActivity) requireActivity()).isDemoMode();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        
        loadTransactions();
    }

    private void loadTransactions() {
        if (isDemoMode) {
            updateStatistics(DemoDataProvider.getDemoTransactions());
        } else {
            transactionRepository.getUserTransactions()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Transaction> transactions = queryDocumentSnapshots.toObjects(Transaction.class);
                        updateStatistics(transactions);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), 
                                "Ошибка при загрузке данных", 
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateStatistics(List<Transaction> transactions) {
        // Расчет общих сумм
        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        // Обновление текстовых полей
        binding.totalIncomeValue.setText(currencyFormat.format(totalIncome));
        binding.totalExpenseValue.setText(currencyFormat.format(totalExpense));
        binding.balanceValue.setText(currencyFormat.format(balance));

        // Расчет расходов по категориям
        Map<String, Double> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Подготовка данных для диаграммы
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Расходы по категориям");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());

        // Настройка и отображение диаграммы
        binding.expensesPieChart.setData(pieData);
        binding.expensesPieChart.getDescription().setEnabled(false);
        binding.expensesPieChart.setUsePercentValues(true);
        binding.expensesPieChart.setEntryLabelTextSize(12f);
        binding.expensesPieChart.setEntryLabelColor(Color.WHITE);
        binding.expensesPieChart.setCenterText("Расходы");
        binding.expensesPieChart.setCenterTextSize(16f);
        binding.expensesPieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 