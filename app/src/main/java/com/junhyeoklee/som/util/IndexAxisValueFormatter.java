package com.junhyeoklee.som.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class IndexAxisValueFormatter implements IAxisValueFormatter {

    private String[] mValues;

    public IndexAxisValueFormatter(String[] mValues) {
        this.mValues = mValues;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mValues[(int)value];
    }
}
