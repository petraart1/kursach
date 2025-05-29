package com.example.financemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.financemanager.databinding.FragmentStatisticsBinding;
import com.example.financemanager.demo.DemoDataProvider;
import com.example.financemanager.model.Transaction;
import com.example.financemanager.model.TransactionType;
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

public class StatisticsFragment extends Fragment {
    private FragmentStatisticsBinding binding;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
    private List<Transaction> transactions;

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
        loadData();
        updateUI();
    }

    private void loadData() {
        if (getMainActivity().isDemoMode()) {
            transactions = DemoDataProvider.getDemoTransactions();
        } else {
            // TODO: Загрузка реальных данных из Firebase
            transactions = DemoDataProvider.getDemoTransactions();
        }
    }

    private void updateUI() {
        updateSummary();
        setupPieChart();
        setupLineChart();
    }

    private void updateSummary() {
        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = Math.abs(transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum());

        double balance = totalIncome - totalExpense;

        binding.totalIncomeText.setText(getString(R.string.total_income) + ": " + currencyFormat.format(totalIncome));
        binding.totalExpenseText.setText(getString(R.string.total_expense) + ": " + currencyFormat.format(totalExpense));
        binding.balanceText.setText(getString(R.string.balance) + ": " + currencyFormat.format(balance));
    }

    private void setupPieChart() {
        // Группируем расходы по категориям
        Map<String, Double> expensesByCategory = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getTitle,
                        Collectors.summingDouble(t -> Math.abs(t.getAmount()))
                ));

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.expense_structure));
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new CurrencyValueFormatter());

        binding.expensesPieChart.setData(data);
        binding.expensesPieChart.getDescription().setEnabled(false);
        binding.expensesPieChart.setEntryLabelColor(Color.WHITE);
        binding.expensesPieChart.animate();
    }

    private void setupLineChart() {
        // Сортируем транзакции по дате
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        Map<String, Double> balanceByDate = new HashMap<>();
        double runningBalance = 0;

        for (Transaction transaction : transactions) {
            String date = dateFormat.format(transaction.getDate());
            runningBalance += transaction.getAmount();
            balanceByDate.put(date, runningBalance);
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Double> entry : balanceByDate.entrySet()) {
            entries.add(new Entry(index, entry.getValue().floatValue()));
            labels.add(entry.getKey());
            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, getString(R.string.balance));
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(12f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setFillAlpha(30);

        LineData lineData = new LineData(dataSet);
        lineData.setValueFormatter(new CurrencyValueFormatter());

        binding.balanceLineChart.setData(lineData);
        binding.balanceLineChart.getDescription().setEnabled(false);
        binding.balanceLineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.balanceLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.balanceLineChart.getXAxis().setGranularity(1f);
        binding.balanceLineChart.getXAxis().setLabelRotationAngle(45);
        binding.balanceLineChart.animate();
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