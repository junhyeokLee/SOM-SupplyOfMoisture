package com.junhyeoklee.som.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.AddWaterViewModelFactory;
import com.junhyeoklee.som.data.factory.MainViewModelFactory;
import com.junhyeoklee.som.data.factory.WaterGraphViewModelFactory;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.MyMarkerView;
import com.junhyeoklee.som.ui.view_model.AddWaterDateViewModel;
import com.junhyeoklee.som.ui.view_model.MainViewModel;
import com.junhyeoklee.som.ui.view_model.WaterGraphViewModel;
import com.junhyeoklee.som.util.DateUtil;
import com.junhyeoklee.som.util.InjectorUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class WaterGraphFragment extends Fragment {

    @BindView(R.id.chart)
    BarChart barChart;
    @BindView(R.id.tv_date_month)
    TextView mDateMonth;
    private List<WaterEntry> mWaterEntryList;
    private WaterDatabase mDb;
    private MainViewModel mainViewModel;
    private AddWaterDateViewModel viewModel2;
    private WaterGraphViewModel viewModel;
    private float mAmount;
    private Date graphDate = new Date();
    private SimpleDateFormat sdfYears = new SimpleDateFormat("yyyy");

    private final static String MONTH_1 = "-01";
    private final static String MONTH_2 = "-02";
    private final static String MONTH_3 = "-03";
    private final static String MONTH_4 = "-04";
    private final static String MONTH_5 = "-05";
    private final static String MONTH_6 = "-06";
    private final static String MONTH_7 = "-07";
    private final static String MONTH_8 = "-08";
    private final static String MONTH_9 = "-09";
    private final static String MONTH_10 = "-10";
    private final static String MONTH_11 = "-11";
    private final static String MONTH_12 = "-12";

    private final int dateCount = 0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water_graph, container, false);
        ButterKnife.bind(this, view);
        mDb = WaterDatabase.getInstance(this.getContext());

//        initTest();
//        setChart(mWaterEntryList);
        initBarChart();
        return view;
    }

    private void getDateCount(){
        DateUtil dateUtil = new DateUtil();
        int dateCount = -dateUtil.getDateDay(dateUtil.getFarDay(0),dateUtil.FORMAT_DATE);

    }

    private void initBarChart(){

        barChart.getDescription().setEnabled(false);

        barChart.setMaxVisibleValueCount(60);

        barChart.setPinchZoom(false);

        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setDrawGridLines(false);

        barChart.animateY(1500);

        barChart.getLegend().setEnabled(false);


        // 물을 마셨던 모든 날짜들 Decoration 이벤트 주기
        MainViewModelFactory mainFactory = new MainViewModelFactory(mDb);
        mainViewModel = ViewModelProviders.of(this, mainFactory).get(MainViewModel.class);
        mainViewModel.getWaters().observe(this,waterEntries -> {
            TotalWaterAmout(waterEntries);
        });


        // BarEntry 생성
//        ArrayList<BarEntry> barEntries = new ArrayList<>();
//        String getYearsTime = sdfYears.format(graphDate);
//
//        mDateMonth.setText(getYearsTime);
//        WaterGraphViewModelFactory factory = InjectorUtils.provideWaterGraphViewModelFactory(getContext());
//        viewModel = ViewModelProviders.of(this,factory).get(WaterGraphViewModel.class);
//        viewModel.getWater_dateMonth(getYearsTime+MONTH_3).observe(this,waterEntries -> {
//            for(int i = 0;i<waterEntries.size();i++) {
//                mWaterEntryList = waterEntries;
//                mAmount = TotalWaterAmout(mWaterEntryList);
//                if(mAmount != 0) {
//                    barEntries.add(new BarEntry(3, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
//                }
//                else{
//                    barEntries.add(new BarEntry(3, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
//                }
//                initBarDataSet(barEntries);
//            }
//        });
//        barEntries.add(new BarEntry(1, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 null이면 0
//        barEntries.add(new BarEntry(1, 4444));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
//        barEntries.add(new BarEntry(2, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
//        barEntries.add(new BarEntry(4,10000));
//        barEntries.add(new BarEntry(5,4000));
//        barEntries.add(new BarEntry(6,1500));
//        barEntries.add(new BarEntry(7,0));
//        barEntries.add(new BarEntry(8,0));
//        barEntries.add(new BarEntry(9,11000));
//        barEntries.add(new BarEntry(10,1000));
//        barEntries.add(new BarEntry(11,500));
//        barEntries.add(new BarEntry(12,22000));
//        initBarDataSet(barEntries);

}

    private void TotalWaterAmout(final List<WaterEntry> waters) {
        float TotalDrinkValue = 0;

        // 2019 - 03 - 14 목요일 수정본
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        String getYearsTime = sdfYears.format(graphDate);
        mDateMonth.setText(getYearsTime);
        WaterGraphViewModelFactory factory = InjectorUtils.provideWaterGraphViewModelFactory(getContext());
        viewModel = ViewModelProviders.of(this,factory).get(WaterGraphViewModel.class);
        for(int i = 0 ; i< waters.size() ; i++) {
            int finalI = i;
            viewModel.getWater_dateMonth(getYearsTime+"-0"+i).observe(this, waterEntries -> {
                    mWaterEntryList = waterEntries;
                    mAmount = getTotalDrinkValue(mWaterEntryList);
                    if (mAmount != 0) {
                        barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    } else {
                        barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    }
                    initBarDataSet(barEntries);
            });
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
//    private int TotalWaterDate(List<WaterEntry> waters){
//        String ToatalDateValue = "";
//        mDateStr = Integer.parseInt(mStr.substring(mStr.lastIndexOf("-")+1));
//        String str = "";
//        int dateStr = 0;
//        for(int i = 0 ; i < waters.size(); i++){
//             str = waters.get(i).getDate();
//             dateStr = Integer.parseInt(str.substring(str.lastIndexOf("-")+1));
////            ToatalDateValue = waters.get(i).getDate();
//        }
//        return dateStr;
//    }

    private void initBarDataSet(ArrayList<BarEntry> barEntries){
        // BarDataSet 생성
        BarDataSet barDataSet;

        ArrayList<String> labels = new ArrayList<>();
        labels.add("일");
        labels.add("월");
        labels.add("화");
        labels.add("수");
        labels.add("목");
        labels.add("금");
        labels.add("토");

        if(barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0){
            barDataSet = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            barDataSet.setValues(barEntries);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else{
            barDataSet = new BarDataSet(barEntries,"마신 물의 양");
            barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(barDataSet);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
            barChart.setFitBars(true);
        }
        for (IDataSet set : barChart.getData().getDataSets())
            set.setDrawValues(!set.isDrawValuesEnabled());

        barChart.setPinchZoom(false);
        barChart.invalidate();
    }

    private void initializeChart(int dayCount) {
        float TotalAmout = 0f;
        float Max = 0f;
        float sumCount = 0f;
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i = 0; i < dayCount;i++ ){
        }
    }
}
