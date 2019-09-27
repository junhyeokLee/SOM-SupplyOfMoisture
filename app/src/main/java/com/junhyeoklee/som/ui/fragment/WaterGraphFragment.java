package com.junhyeoklee.som.ui.fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaterGraphFragment extends WaterGraphBasePagerFragment {

    @BindView(R.id.chart)
    BarChart barChart;
    @BindView(R.id.tv_date_year)
    TextView mDateYear;
    @BindView(R.id.btn_left_arrow)
    ImageButton mBtn_leftArrow;
    @BindView(R.id.btn_right_arrow)
    ImageButton mBtn_rightArrow;
    @BindView(R.id.empty)
    LinearLayout mEmptyLayout;
    @BindView(R.id.tv_month_max_amout)
    TextView mTv_MonthMaxAmout;
    @BindView(R.id.tv_total_amount)
    TextView mTv_TotalAmout;


    private WaterDatabase mDb;
    private MainViewModel mainViewModel;
    private WaterGraphViewModel viewModel;
    private float mAmount;
    private float TotalValue;

    // DATE
    private Date graphDate = new Date();
    private SimpleDateFormat sdfYears = new SimpleDateFormat("yyyy");
    private String getYears = null;
    private Calendar cal = Calendar.getInstance();
    private String getCurrentYear = null;
    private int max = Integer.MIN_VALUE; //정수형 데이터 중 가장 작은 값으로 초기화

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water_graph, container, false);
        ((TextView) view.findViewById(R.id.emptyText)).setText(R.string.msg_water_graph_year_empty);
        ButterKnife.bind(this, view);
        mDb = WaterDatabase.getInstance(this.getContext());
        initBarChart();

        return view;
    }

    private void initBarChart(){
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.animateY(1000);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraBottomOffset(30f);


        ArrayList<String> xVals = new ArrayList();
        xVals.add("(월)");
        xVals.add("1");xVals.add("2");xVals.add("3");xVals.add("4");
        xVals.add("5");xVals.add("6");xVals.add("7");xVals.add("8");
        xVals.add("9");xVals.add("10");xVals.add("11");xVals.add("12");

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12);
        xAxis.setLabelCount(12);
        xAxis.setYOffset(10);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));

        ValueFormatter custom = new MyValueFormatter(" ml");

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setXOffset(10);
        leftAxis.setTextSize(11);

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
//        mDateYear.setTextColor(getResources().getColor(R.color.date_daccent));
        getCurrentYear = sdfYears.format(graphDate);
        getYears = sdfYears.format(graphDate);
        mDateYear.setText(getYears);

        mBtn_leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.YEAR,-1);
                getYears = sdfYears.format(cal.getTime());
                mDateYear.setText(getYears);

                ArrayList<BarEntry> barEntries = new ArrayList<>();
                WaterGraphViewModelFactory factory = InjectorUtils.provideWaterGraphViewModelFactory(getContext());
                viewModel = ViewModelProviders.of(WaterGraphFragment.this,factory).get(WaterGraphViewModel.class);
                TotalValue = 0;
                max = 0;
                for(int i = 1 ; i < 13 ; i++) {
                    int finalI = i;
                    if( i < 10) {
                        viewModel.getWater_dateMonth(getYears + "-0" + i).observe(WaterGraphFragment.this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                            mAmount = getTotalDrinkValue(waterEntries);
                            if (mAmount != 0) {
                                barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                                TotalValue += mAmount;
                                // 총 섭취량
                                mTv_TotalAmout.setText(""+(int)TotalValue+" ml");
                                // 1주일 최대값
                                if(mAmount > max){
                                    max = (int)mAmount;
                                }
                                mTv_MonthMaxAmout.setText(""+max+" ml");
                            } else {
                                barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                            }
                            initBarDataSet(barEntries);
                        });
                    }
                    else if(i >= 10){
                        viewModel.getWater_dateMonth(getYears + "-" + i).observe(WaterGraphFragment.this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                            mAmount = getTotalDrinkValue(waterEntries);
                            if (mAmount != 0) {
                                barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                                TotalValue += mAmount;
                                // 총 섭취량
                                mTv_TotalAmout.setText(""+(int)TotalValue+" ml");
                                // 1주일 최대값
                                if(mAmount > max){
                                    max = (int)mAmount;
                                }
                                mTv_MonthMaxAmout.setText(""+max+" ml");
                            } else {
                                barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                            }
                            initBarDataSet(barEntries);
                        });
                    }
                }
                getEmptyLayout();
                barChart.animateY(1000);
            }
        });

        mBtn_rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.YEAR,+1);
                getYears = sdfYears.format(cal.getTime());
                mDateYear.setText(getYears);

                ArrayList<BarEntry> barEntries = new ArrayList<>();
                WaterGraphViewModelFactory factory = InjectorUtils.provideWaterGraphViewModelFactory(getContext());
                viewModel = ViewModelProviders.of(WaterGraphFragment.this,factory).get(WaterGraphViewModel.class);
                TotalValue = 0;
                max = 0;
                for(int i = 1 ; i < 13 ; i++) {
                    int finalI = i;
                    if( i < 10) {
                        viewModel.getWater_dateMonth(getYears + "-0" + i).observe(WaterGraphFragment.this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                            mAmount = getTotalDrinkValue(waterEntries);
                            if (mAmount != 0) {
                                barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                                TotalValue += mAmount;
                                // 총 섭취량
                                mTv_TotalAmout.setText(""+(int)TotalValue+" ml");
                                // 1주일 최대값
                                if(mAmount > max){
                                    max = (int)mAmount;
                                }
                                mTv_MonthMaxAmout.setText(""+max+" ml");
                            } else {
                                barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                            }
                            initBarDataSet(barEntries);
                        });
                    }
                    else if(i >= 10){
                        viewModel.getWater_dateMonth(getYears + "-" + i).observe(WaterGraphFragment.this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                            mAmount = getTotalDrinkValue(waterEntries);
                            if (mAmount != 0) {
                                barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                                TotalValue += mAmount;
                                // 총 섭취량
                                mTv_TotalAmout.setText(""+(int)TotalValue+" ml");
                                // 1주일 최대값
                                if(mAmount > max){
                                    max = (int)mAmount;
                                }
                                mTv_MonthMaxAmout.setText(""+max+" ml");
                            } else {
                                barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                            }
                            initBarDataSet(barEntries);
                        });
                    }
                }
                getEmptyLayout();
                barChart.animateY(1000);
            }

        });

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        WaterGraphViewModelFactory factory = InjectorUtils.provideWaterGraphViewModelFactory(getContext());
        viewModel = ViewModelProviders.of(this,factory).get(WaterGraphViewModel.class);
        cal.add(Calendar.YEAR,0);
        getYears = sdfYears.format(cal.getTime());
        TotalValue = 0;
        max = 0;
        for(int i = 1 ; i < 13 ; i++) {
            int finalI = i;
            if( i < 10) {
                viewModel.getWater_dateMonth(getYears + "-0" + i).observe(this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                    mAmount = getTotalDrinkValue(waterEntries);
                    if (mAmount != 0) {
                        barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                        TotalValue += mAmount;
                        // 총 섭취량
                        mTv_TotalAmout.setText(""+(int)TotalValue+" ml");
                        // 1주일 최대값
                        if(mAmount > max){
                            max = (int)mAmount;
                        }
                        mTv_MonthMaxAmout.setText(""+max+" ml");
                    } else {
                        barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    }
                    initBarDataSet(barEntries);
                });
            }
            else if(i >= 10){
                viewModel.getWater_dateMonth(getYears + "-" + i).observe(this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                    mAmount = getTotalDrinkValue(waterEntries);
                    if (mAmount != 0) {
                        barEntries.add(new BarEntry(finalI, mAmount));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                        TotalValue += mAmount;
                        // 총 섭취량
                        mTv_TotalAmout.setText(""+(int)TotalValue+" ml");
                        // 1주일 최대값
                        if(mAmount > max){
                            max = (int)mAmount;
                        }
                        mTv_MonthMaxAmout.setText(""+max+" ml");
                    } else {
                        barEntries.add(new BarEntry(finalI, 0));// 1월에 마신 모든양 y에 넣기 없으면 0 이면 0
                    }
                    initBarDataSet(barEntries);
                });
            }
        }
        getEmptyLayout();
    }

    private float getTotalDrinkValue(final List<WaterEntry> waters){
        float TotalDrinkValue = 0;

        for (int i = 0; i < waters.size(); i++) {
            if(waters != null){
                TotalDrinkValue += waters.get(i).getAmount();
                // water가 null 이 아니면 Empty 레이아웃 사라짐
                mEmptyLayout.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);

            }
            else{
                // water가 null 이면 Empty 레이아웃 보여짐
                mEmptyLayout.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
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
            set1.setValueTextSize(12);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else{
            set1 = new BarDataSet(barEntries, "Data Set");
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);
            set1.setForm(Legend.LegendForm.EMPTY);
            set1.setColor(getResources().getColor(R.color.sky3));
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            ValueFormatter custom = new MyValueFormatter("ml");
            data.setDrawValues(true);
            data.setValueFormatter(custom);
            data.setValueTextSize(10f);
            data.setHighlightEnabled(true);
            data.setBarWidth(0.5f);
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

    private void getEmptyLayout(){
        // 현재년도와 Year 텍스트가 같지 않으면 다음해로 갈 수 없다.
        if(getCurrentYear.equals(mDateYear.getText().toString())){
            mBtn_rightArrow.setVisibility(View.INVISIBLE);
        }else{
            mBtn_rightArrow.setVisibility(View.VISIBLE);
        }
        // 마신 그래프가 없을때 물을 마신데이터가 없다고 알려줌.
        if(mAmount == 0){
            mEmptyLayout.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
        }else{
            mEmptyLayout.setVisibility(View.GONE);
            barChart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_water_graph_month);
    }

    @Override
    public void onMonthChanged() {
//        if(barChart.getData() != null){
//            barChart.notifyDataSetChanged();
//        }
    }

    @Override
    public void onWeekChanged() {
//        if(barChart.getData() != null){
//            barChart.notifyDataSetChanged();
//        }
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
