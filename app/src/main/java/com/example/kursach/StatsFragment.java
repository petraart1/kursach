package com.example.kursach;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import android.content.res.TypedArray;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import android.widget.Toast;

public class StatsFragment extends Fragment {
    private HomeViewModel viewModel;
    private TextView tvIncome, tvExpense;
    private LineChart lineChartExpense;
    private LineChart lineChartIncome;
    private LinearLayout legendLayout;
    private PieChart eazePieChartExpense;
    private PieChart eazePieChartIncome;
    private int selectedLegendIndex = -1;
    private List<PieModel> pieModels = new ArrayList<>();
    private float totalPieValue = 0f;
    private int outlineColor = Color.TRANSPARENT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        tvIncome = view.findViewById(R.id.tv_income);
        tvExpense = view.findViewById(R.id.tv_expense);
        lineChartExpense = view.findViewById(R.id.line_chart_expense);
        lineChartIncome = view.findViewById(R.id.line_chart_income);
        legendLayout = view.findViewById(R.id.legend_layout);
        eazePieChartExpense = view.findViewById(R.id.eaze_pie_chart_expense);
        eazePieChartIncome = view.findViewById(R.id.eaze_pie_chart_income);

        // Отключаем вращение (на всякий случай)
        // eazePieChart.setRotationEnabled(false);
        // Устанавливаем цвет "дыры" (surface)
        int surfaceColor = getSurfaceColor();
        eazePieChartExpense.setInnerValueString(""); // убираем текст
        eazePieChartExpense.setInnerPadding(80); // делаем кольцо ещё тоньше
        // eazePieChart.setInnerBackgroundColor(surfaceColor);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                updateStats(transactions);
            }
        });

        return view;
    }

    private Drawable getCategoryIcon(String category) {
        if (category.equals("Продукты")) return ContextCompat.getDrawable(requireContext(), R.drawable.ic_category_food);
        if (category.equals("Транспорт")) return ContextCompat.getDrawable(requireContext(), R.drawable.ic_category_transport);
        if (category.equals("Развлечения")) return ContextCompat.getDrawable(requireContext(), R.drawable.ic_category_entertainment);
        if (category.equals("Здоровье")) return ContextCompat.getDrawable(requireContext(), R.drawable.ic_category_health);
        if (category.equals("Подарки")) return ContextCompat.getDrawable(requireContext(), R.drawable.ic_category_gift);
        if (category.equals("Инвестиции")) return ContextCompat.getDrawable(requireContext(), R.drawable.ic_category_investment);
        return ContextCompat.getDrawable(requireContext(), R.drawable.ic_category_other);
    }

    private int getSurfaceColor() {
        TypedArray a = requireContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.colorBackground });
        int color = a.getColor(0, Color.WHITE);
        a.recycle();
        return color;
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
        // --- PieChart: расходы ---
        eazePieChartExpense.clearChart();
        pieModels.clear();
        int colorIdx = 0;
        for (String cat : categorySums.keySet()) {
            int color = ColorTemplate.MATERIAL_COLORS[colorIdx % ColorTemplate.MATERIAL_COLORS.length];
            PieModel model = new PieModel(cat, categorySums.get(cat).floatValue(), color);
            pieModels.add(model);
            eazePieChartExpense.addPieSlice(model);
            colorIdx++;
        }
        eazePieChartExpense.startAnimation();
        selectedLegendIndex = -1;

        totalPieValue = 0f;
        for (PieModel model : pieModels) {
            totalPieValue += model.getValue();
        }

        // --- PieChart: доходы ---
        eazePieChartIncome.clearChart();
        Map<String, Double> incomeCategorySums = new HashMap<>();
        for (Transaction t : transactions) {
            if ("income".equals(t.type)) {
                double sum = incomeCategorySums.getOrDefault(t.category, 0.0);
                incomeCategorySums.put(t.category, sum + t.amount);
            }
        }
        colorIdx = 0;
        for (String cat : incomeCategorySums.keySet()) {
            int color = ColorTemplate.MATERIAL_COLORS[colorIdx % ColorTemplate.MATERIAL_COLORS.length];
            PieModel model = new PieModel(cat, incomeCategorySums.get(cat).floatValue(), color);
            eazePieChartIncome.addPieSlice(model);
            colorIdx++;
        }
        eazePieChartIncome.startAnimation();

        // --- Легенда ---
        legendLayout.removeAllViews();
        int legendColumns = 2;
        int size = (int) (24 * getResources().getDisplayMetrics().density);
        int pad = (int) (6 * getResources().getDisplayMetrics().density);
        LinearLayout row = null;
        int n = pieModels.size();
        for (int i = 0; i < n; i += legendColumns) {
            row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            for (int j = 0; j < legendColumns; j++) {
                int idx = i + j;
                LinearLayout cell = new LinearLayout(getContext());
                cell.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams cellParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                cellParams.setMargins(pad, pad, pad, pad);
                cell.setLayoutParams(cellParams);
                cell.setPadding(pad, pad, pad, pad);
                if (idx < n) {
                    PieModel model = pieModels.get(idx);
                    String cat = model.getLegendLabel();
                    cell.setBackgroundResource(selectedLegendIndex == idx ? R.drawable.bg_legend_icon_selected : R.drawable.bg_legend_icon);
                    ImageView iconView = new ImageView(getContext());
                    iconView.setImageDrawable(getCategoryIcon(cat));
                    int iconBgColor = applyAlpha(model.getColor(), 40);
                    Drawable iconBg = ContextCompat.getDrawable(getContext(), R.drawable.bg_legend_icon_circle);
                    iconBg.setTint(iconBgColor);
                    iconView.setBackground(iconBg);
                    iconView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                    TextView label = new TextView(getContext());
                    label.setText(cat);
                    label.setTextSize(15);
                    label.setPadding(8,0,0,0);
                    label.setTextColor(getColorOnSurface());
                    TextView sumView = new TextView(getContext());
                    sumView.setText(String.format(Locale.getDefault(),"%.0f ₽", model.getValue()));
                    sumView.setTextSize(15);
                    sumView.setPadding(8,0,0,0);
                    sumView.setTextColor(getColorOnSurface());
                    cell.addView(iconView);
                    cell.addView(label);
                    cell.addView(sumView);
                    int finalI = idx;
                    cell.setOnClickListener(v -> {
                        selectPieCategory(finalI);
                        updateLegendSelection();
                    });
                    cell.setTag(idx);
                } else {
                    // Пустая капсула для симметрии
                    cell.setBackgroundColor(Color.TRANSPARENT);
                }
                row.addView(cell);
            }
            legendLayout.addView(row);
        }
        // --- Обработка нажатия на PieChart ---
        eazePieChartExpense.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                int w = v.getWidth();
                int h = v.getHeight();
                float cx = w / 2f;
                float cy = h / 2f;
                float x = event.getX() - cx;
                float y = event.getY() - cy;
                double angle = Math.toDegrees(Math.atan2(y, x));
                if (angle < 0) angle += 360;
                float total = 0;
                for (int i = 0; i < pieModels.size(); i++) {
                    float sweep = pieModels.get(i).getValue();
                    if (totalPieValue > 0 && angle >= total && angle < total + sweep * 360f / totalPieValue) {
                        selectPieCategory(i);
                        break;
                    }
                    total += sweep * 360f / totalPieValue;
                }
            }
            return false;
        });
        // --- Сброс выделения при клике вне легенды ---
        eazePieChartExpense.setOnClickListener(v -> {
            selectPieCategory(-1);
        });
        // --- LineChart: расходы по дням ---
        List<Entry> expenseEntries = new ArrayList<>();
        int idx = 0;
        Map<String, Double> expenseDaySums = new TreeMap<>();
        for (Transaction t : transactions) {
            if ("expense".equals(t.type)) {
                String day = sdf.format(new Date(t.timestamp));
                double daySum = expenseDaySums.getOrDefault(day, 0.0);
                expenseDaySums.put(day, daySum + Math.abs(t.amount));
            }
        }
        idx = 0;
        for (String day : expenseDaySums.keySet()) {
            expenseEntries.add(new Entry(idx, expenseDaySums.get(day).floatValue()));
            idx++;
        }
        LineDataSet expenseDataSet = new LineDataSet(expenseEntries, "Расходы по дням");
        expenseDataSet.setColor(ColorTemplate.rgb("#FF7043"));
        expenseDataSet.setCircleColor(ColorTemplate.rgb("#FF7043"));
        expenseDataSet.setLineWidth(2f);
        expenseDataSet.setCircleRadius(4f);
        expenseDataSet.setDrawValues(false);
        LineData expenseData = new LineData(expenseDataSet);
        lineChartExpense.setData(expenseData);
        lineChartExpense.getDescription().setEnabled(false);
        lineChartExpense.getLegend().setEnabled(false);
        lineChartExpense.getXAxis().setDrawLabels(false);
        lineChartExpense.invalidate();

        // --- LineChart: доходы по дням ---
        List<Entry> incomeEntries = new ArrayList<>();
        idx = 0;
        Map<String, Double> incomeDaySums = new TreeMap<>();
        for (Transaction t : transactions) {
            if ("income".equals(t.type)) {
                String day = sdf.format(new Date(t.timestamp));
                double daySum = incomeDaySums.getOrDefault(day, 0.0);
                incomeDaySums.put(day, daySum + t.amount);
            }
        }
        idx = 0;
        for (String day : incomeDaySums.keySet()) {
            incomeEntries.add(new Entry(idx, incomeDaySums.get(day).floatValue()));
            idx++;
        }
        LineDataSet incomeDataSet = new LineDataSet(incomeEntries, "Доходы по дням");
        incomeDataSet.setColor(ColorTemplate.getHoloBlue());
        incomeDataSet.setCircleColor(ColorTemplate.getHoloBlue());
        incomeDataSet.setLineWidth(2f);
        incomeDataSet.setCircleRadius(4f);
        incomeDataSet.setDrawValues(false);
        LineData incomeData = new LineData(incomeDataSet);
        lineChartIncome.setData(incomeData);
        lineChartIncome.getDescription().setEnabled(false);
        lineChartIncome.getLegend().setEnabled(false);
        lineChartIncome.getXAxis().setDrawLabels(false);
        lineChartIncome.invalidate();
    }

    // --- Выделение категории ---
    private void selectPieCategory(int index) {
        selectedLegendIndex = index;
        // Легенда
        updateLegendSelection();
        // PieChart: выделение сегмента (альфа)
        for (int i = 0; i < pieModels.size(); i++) {
            PieModel model = pieModels.get(i);
            if (i == index) {
                // Попытка сделать сегмент толще (если поддерживается)
                // model.setThickness(40); // если такого метода нет, просто делаем альфу 1
                model.setColor(applyAlpha(model.getColor(), 255));
            } else {
                // model.setThickness(20); // если поддерживается
                model.setColor(applyAlpha(model.getColor(), 80));
            }
        }
        eazePieChartExpense.update();
        eazePieChartIncome.update();
    }

    private int applyAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    // --- Обновление выделения в легенде ---
    private void updateLegendSelection() {
        int legendColumns = 2;
        int cellIdx = 0;
        for (int i = 0; i < legendLayout.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) legendLayout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                View cell = row.getChildAt(j);
                cell.setBackgroundResource(selectedLegendIndex == cellIdx ? R.drawable.bg_legend_icon_selected : R.drawable.bg_legend_icon);
                cellIdx++;
            }
        }
    }

    private String formatAmount(double value) {
        if (value >= 1000) return String.format(Locale.getDefault(), "%,.0f", value).replace(',', ' ');
        return String.format(Locale.getDefault(), "%.0f", value);
    }

    private int getColorOnSurface() {
        TypedArray a = requireContext().getTheme().obtainStyledAttributes(new int[] { android.R.attr.textColorPrimary });
        int color = a.getColor(0, Color.BLACK);
        a.recycle();
        return color;
    }
} 