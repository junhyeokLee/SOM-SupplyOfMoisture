package com.junhyeoklee.som.ui.fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.MainViewModelFactory;
import com.junhyeoklee.som.data.factory.WaterGraphViewModelFactory;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.adapter.ContextFragmentInstantiator;
import com.junhyeoklee.som.ui.view.XYMarkerView;
import com.junhyeoklee.som.ui.view_model.AddWaterDateViewModel;
import com.junhyeoklee.som.ui.view_model.MainViewModel;
import com.junhyeoklee.som.ui.view_model.WaterGraphViewModel;
import com.junhyeoklee.som.util.DateUtil;
import com.junhyeoklee.som.util.GraphUtil.DayAxisValueFormatter;
import com.junhyeoklee.som.util.InjectorUtils;
import com.junhyeoklee.som.util.GraphUtil.MyValueFormatter;
import com.junhyeoklee.som.util.GraphUtil.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaterGraphFragment extends WaterGraphBasePagerFragment {

    @BindView(R.id.chart)
    BarChart barChart;
    @BindView(R.id.tv_date_year)
    TextView mDateYear;


    private WaterDatabase mDb;
    private MainViewModel mainViewModel;
    private WaterGraphViewModel viewModel;
    private float mAmount;
    private View empty;

    // DATE
    private Date graphDate = new Date();
    private SimpleDateFormat sdfYears = new SimpleDateFormat("yyyy");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water_graph, container, false);
        empty = view.findViewById(R.id.empty);
        ((TextView) view.findViewById(R.id.emptyText)).setText(R.string.msg_water_graph_month_empty);
        ButterKnife.bind(this, view);
        mDb = WaterDatabase.getInstance(this.getContext());
        initBarChart();

        return view;
    }

    private void getDateCount(){
        DateUtil dateUtil = new DateUtil();
        int dateCount = -dateUtil.getDateDay(dateUtil.getFarDay(0),dateUtil.dateFormat);

    }

    private void initBarChart(){
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.animateY(1000);
        barChart.getLegend().setEnabled(false);

        ArrayList<String> xVals = new ArrayList();
        xVals.add("");
        xVals.add("1월");xVals.add("2월");xVals.add("3월");xVals.add("4월");
        xVals.add("5월");xVals.add("6월");xVals.add("7월");xVals.add("8월");
        xVals.add("9월");xVals.add("10월");xVals.add("11월");xVals.add("12월");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10);
        xAxis.setLabelCount(13);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        ValueFormatter custom = new MyValueFormatter(" ml");

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        MainViewModelFactory mainFactory = new MainViewModelFactory(mDb);
        mainViewModel = ViewModelProviders.of(this, mainFactory).get(MainViewModel.class);
        mainViewModel.getWaters().observe(this,waterEntries -> {
            TotalWaterAmout(waterEntries);
        });
    }

    private void TotalWaterAmout(final List<WaterEntry> waters) {
        float TotalDrinkValue = 0;
        mDateYear.setTextColor(getResources().getColor(R.color.date_daccent));
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        String getYearsTime = sdfYears.format(graphDate);
        mDateYear.setText(getYearsTime);
        WaterGraphViewModelFactory factory = InjectorUtils.provideWaterGraphViewModelFactory(getContext());
        viewModel = ViewModelProviders.of(this,factory).get(WaterGraphViewModel.class);
        for(int i = 0 ; i < 13 ; i++) {
            int finalI = i;
            if( i < 10) {
                viewModel.getWater_dateMonth(getYearsTime + "-0" + i).observe(this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                    mAmount = getTotalDrinkValue(waterEntries);
                    if (mAmount != 0) {
                        barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    } else {
                        barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    }
                    initBarDataSet(barEntries);
                });
            }
            else if(i >= 10){
                viewModel.getWater_dateMonth(getYearsTime + "-" + i).observe(this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                    mAmount = getTotalDrinkValue(waterEntries);
                    if (mAmount != 0) {
                        barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    } else {
                        barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    }
                    initBarDataSet(barEntries);
                });
            }
        }
    }

    private float getTotalDrinkValue(final List<WaterEntry> waters){
        float TotalDrinkValue = 0;

        for (int i = 0; i < waters.size(); i++) {
            if(waters != null){
                TotalDrinkValue += waters.get(i).getAmount();
            }
        }
        return TotalDrinkValue;
    }

    private void initBarDataSet(ArrayList<BarEntry> barEntries){
        // BarDataSet 생성
        BarDataSet set1;

        if(barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0){
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(barEntries);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else{
            set1 = new BarDataSet(barEntries, "Data Set");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);
            set1.setForm(Legend.LegendForm.EMPTY);

            set1.setColor(getResources().getColor(R.color.lightsky));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            ValueFormatter custom = new MyValueFormatter("ml");
            data.setDrawValues(true);
            data.setValueFormatter(custom);
            data.setValueTextSize(10f);
            data.setHighlightEnabled(true);
            data.setBarWidth(1f);
            barChart.setTouchEnabled(true);
            barChart.setData(data);
            barChart.setFitBars(true);

            ValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);
            XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter);
            mv.setChartView(barChart); // For bounds control
            barChart.setMarker(mv); // Set the marker to the chart

        }
        barChart.invalidate();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_water_graph_month);
    }

    @Override
    public void onMonthChanged() {
        if(barChart.getData() != null){
            barChart.notifyDataSetChanged();
            onChanged();
        }
    }

    @Override
    public void onWeekChanged() {
        if(barChart.getData() != null){
            barChart.notifyDataSetChanged();
            onChanged();
        }
    }

    // 그래프가 비었을때
    private void onChanged() {
        if (empty != null && barChart.getData() != null)
            empty.setVisibility(View.GONE);

        else empty.setVisibility(View.VISIBLE);
    }

    public static class Instantiator extends ContextFragmentInstantiator {

        public Instantiator(Context context) {
            super(context);
        }

        @Override
        public String getTitle(Context context, int position) {
            return context.getString(R.string.title_water_graph_month);
        }

        @Nullable
        @Override
        public WaterGraphBasePagerFragment newInstance(int position) {
            return new WaterGraphFragment();
        }
    }

}
