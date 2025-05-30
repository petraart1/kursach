package com.example.financemanager.util;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class PercentFormatter extends ValueFormatter {
    private final DecimalFormat format;

    public PercentFormatter() {
        format = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {
        return format.format(value) + "%";
    }
} 