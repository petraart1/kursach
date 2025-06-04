package com.example.kursach;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class StatsFragment extends Fragment {
    private HomeViewModel viewModel;
    private TextView tvIncome, tvExpense;
    private PieChart pieChart;
    private LineChart lineChart;
    private LinearLayout legendLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        tvIncome = view.findViewById(R.id.tv_income);
        tvExpense = view.findViewById(R.id.tv_expense);
        pieChart = view.findViewById(R.id.pie_chart);
        lineChart = view.findViewById(R.id.line_chart);
        legendLayout = view.findViewById(R.id.legend_layout);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                updateStats(transactions);
            }
        });
        return view;
    }

    private void updateStats(List<Transaction> transactions) {
        double income = 0, expense = 0;
        Map<String, Double> categorySums = new HashMap<>();
        Map<String, Integer> categoryColors = new HashMap<>();
        Map<String, Double> daySums = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
        for (Transaction t : transactions) {
            if ("income".equals(t.type)) {
                income += t.amount;
            } else if ("expense".equals(t.type)) {
                expense += Math.abs(t.amount);
                // Категории для PieChart
                double sum = categorySums.getOrDefault(t.category, 0.0);
                categorySums.put(t.category, sum + Math.abs(t.amount));
                // По дням для LineChart
                String day = sdf.format(new Date(t.timestamp));
                double daySum = daySums.getOrDefault(day, 0.0);
                daySums.put(day, daySum + Math.abs(t.amount));
            }
        }
        tvIncome.setText("Доходы: "+String.format(Locale.getDefault(),"%.2f ₽", income));
        tvExpense.setText("Расходы: "+String.format(Locale.getDefault(),"%.2f ₽", expense));
        // --- PieChart ---
        List<PieEntry> pieEntries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        int colorIdx = 0;
        for (String cat : categorySums.keySet()) {
            pieEntries.add(new PieEntry(categorySums.get(cat).floatValue(), ""));
            int color = ColorTemplate.MATERIAL_COLORS[colorIdx % ColorTemplate.MATERIAL_COLORS.length];
            colors.add(color);
            categoryColors.put(cat, color);
            colorIdx++;
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setDrawValues(false);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
        // --- Легенда ---
        legendLayout.removeAllViews();
        for (String cat : categorySums.keySet()) {
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            View colorView = new View(getContext());
            int size = (int) (20 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(size, size);
            colorParams.setMargins(0,0,16,0);
            colorView.setLayoutParams(colorParams);
            colorView.setBackgroundColor(categoryColors.get(cat));
            TextView label = new TextView(getContext());
            label.setText(cat + " (" + String.format(Locale.getDefault(),"%.2f ₽", categorySums.get(cat)) + ")");
            label.setTextSize(16);
            row.addView(colorView);
            row.addView(label);
            legendLayout.addView(row);
        }
        // --- LineChart ---
        List<Entry> lineEntries = new ArrayList<>();
        int idx = 0;
        for (String day : daySums.keySet()) {
            lineEntries.add(new Entry(idx, daySums.get(day).floatValue()));
            idx++;
        }
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Расходы по дням");
        lineDataSet.setColor(ColorTemplate.getHoloBlue());
        lineDataSet.setCircleColor(ColorTemplate.getHoloBlue());
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(false);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.invalidate();
    }
} 