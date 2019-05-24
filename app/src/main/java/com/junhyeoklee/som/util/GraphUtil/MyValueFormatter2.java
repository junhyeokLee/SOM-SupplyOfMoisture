package com.junhyeoklee.som.util.GraphUtil;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;

import java.text.DecimalFormat;

public class MyValueFormatter2 extends ValueFormatter
{

    private final DecimalFormat mFormat;
    private final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7" };
    public MyValueFormatter2() {
        mFormat = new DecimalFormat("####,###0");
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (axis instanceof XAxis) {
            return mFormat.format(value);
        } else if (value > 0) {
            return mFormat.format(value);
        } else {
            return mFormat.format(value);
        }
    }
}
