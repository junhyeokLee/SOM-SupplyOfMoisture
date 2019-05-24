package com.junhyeoklee.som.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.MainViewModelFactory;
import com.junhyeoklee.som.data.factory.WaterGraphViewModelFactory;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.adapter.ContextFragmentInstantiator;
import com.junhyeoklee.som.ui.view_model.MainViewModel;
import com.junhyeoklee.som.ui.view_model.WaterGraphViewModel;
import com.junhyeoklee.som.util.GraphUtil.MyValueFormatter;
import com.junhyeoklee.som.util.GraphUtil.MyValueFormatter2;
import com.junhyeoklee.som.util.GraphUtil.ValueFormatter;
import com.junhyeoklee.som.util.InjectorUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaterGraphWeekFragment extends WaterGraphBasePagerFragment implements
        OnChartGestureListener, OnChartValueSelectedListener {
    @BindView(R.id.tv_date_month_end)
    TextView mDateMonthEnd;
    @BindView(R.id.tv_date_month_start)
    TextView mDateMonthStart;
    @BindView(R.id.btn_left_arrow)
    ImageButton mBtn_leftArrow;
    @BindView(R.id.btn_right_arrow)
    ImageButton mBtn_rightArrow;

    private WaterDatabase mDb;
    private MainViewModel mainViewModel;
    private WaterGraphViewModel viewModel;
    private float mAmount;

    private LineChart mChart;

    private Date graphDate = new Date();
    private SimpleDateFormat sdfWeek = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat sdfyear = new SimpleDateFormat("yyyy");

    private Calendar cal = Calendar.getInstance();
    private DateFormat format = new SimpleDateFormat("yyyyMMdd");

    private int dateCount = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water_week_graph,container,false);
        mChart = (LineChart) view.findViewById(R.id.linechart);
        ButterKnife.bind(this, view);
        mDb = WaterDatabase.getInstance(this.getContext());
        initLineChart();

        return view;
    }

    private void initLineChart(){

//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("일");
//        labels.add("월");
//        labels.add("화");
//        labels.add("수");
//        labels.add("목");
//        labels.add("금");
//        labels.add("토");

        mChart.getDescription().setEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);
        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.animateY(1000);
        mChart.getLegend().setEnabled(false);

        ValueFormatter custom = new MyValueFormatter2();
        final String[] quarters = new String[] { "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7" };
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("일");
        labels.add("월");
        labels.add("화");
        labels.add("수");
        labels.add("목");
        labels.add("금");
        labels.add("토");


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(10);
        xAxis.setLabelCount(7);

        ValueFormatter custom2 = new MyValueFormatter(" ml");

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setValueFormatter(custom2);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setValueFormatter(custom2);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        MainViewModelFactory mainFactory = new MainViewModelFactory(mDb);
        mainViewModel = ViewModelProviders.of(this, mainFactory).get(MainViewModel.class);
        mainViewModel.getWaters().observe(this,waterEntries -> {
            setData(waterEntries);
        });
    }

    private void setData(final List<WaterEntry> waters) {

        mDateMonthStart.setTextColor(getResources().getColor(R.color.date_daccent));
        mDateMonthEnd.setTextColor(getResources().getColor(R.color.date_daccent));

        // 첫째주
        cal.set(Calendar.DAY_OF_WEEK, 1);
        int year1 = cal.get(Calendar.YEAR);
        int month1 = cal.get(Calendar.MONTH)+1;
        int day1 = cal.get(Calendar.DAY_OF_MONTH);
//        cal.add(cal.DATE, -7); // 7일(일주일)을 뺀다
        String StartDate = ""+year1+month1+day1;
        String StartDate2 = format.format(cal.getTime());

        mDateMonthStart.setText(StartDate2);

        // 마지막주
        cal.set(Calendar.DAY_OF_WEEK, 7);
        int year7 = cal.get(Calendar.YEAR);
        int month7 = cal.get(Calendar.MONTH)+1;
        int day7 = cal.get(Calendar.DAY_OF_MONTH);
        String getWeeksTime = ""+year7+month7+day7;
        String getWeeksTime2 = format.format(cal.getTime());
        Log.e("이번주 마지막 날자 = ",getWeeksTime2);
//        String getWeeksTime = sdfWeek.format(graphDate);

        mDateMonthEnd.setText(getWeeksTime2);

        // 올해 년도
        String getYearTime = sdfyear.format(graphDate);

//        mBtn_leftArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String getWeeksTime = format.format(cal.getTime());
//                mDateMonthEnd.setText(getWeeksTime);
//
//                cal.add(cal.DATE, -7); // 7일(이주일)을 뺀다
//                String StartDate = format.format(cal.getTime());
//                mDateMonthStart.setText(StartDate);
//
//            }
//        });
//
//        mBtn_rightArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String StartDate = format.format(cal.getTime());
//                mDateMonthStart.setText(StartDate);
//
//                cal.add(cal.DATE, +7); // 7일(이주일)을 더한다
//                String getWeeksTime = format.format(cal.getTime());
//                mDateMonthEnd.setText(getWeeksTime);
//            }
//        });
        int endDt = Integer.parseInt(getWeeksTime2);
        int startYear = Integer.parseInt(StartDate2.substring(0, 4));
        int startMonth = Integer.parseInt(StartDate2.substring(4, 6));
        int startDate = Integer.parseInt(StartDate2.substring(6, 8));

        Calendar cal = Calendar.getInstance();
        // Calendar의 Month는 0부터 시작하므로 -1 해준다.
        // Calendar의 기본 날짜를 startDt로 셋팅해준다.
        cal.set(startYear, startMonth - 1, startDate);

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        WaterGraphViewModelFactory factory = InjectorUtils.provideWaterGraphViewModelFactory(getContext());
        viewModel = ViewModelProviders.of(this, factory).get(WaterGraphViewModel.class);

            while (true) {
                // 날짜 출력
                System.out.println(getDateByString(cal.getTime()));
                Log.e("DATE WEEKS TIME = ", getDateByString(cal.getTime()));
                String date = getDateByString(cal.getTime());

                viewModel.getWater_dateWeek(getDateByString(cal.getTime()).replaceAll("-","")).observe(this, waterEntries -> {
//                mWaterEntryList = waterEntries;
                    mAmount = getTotalDrinkValue(waterEntries);
                    if (mAmount != 0) {
                    yVals.add(new Entry(Integer.parseInt(date.replace("-","").replace(getYearTime,"")), mAmount));
//                    yVals.add(new Entry(finalI, mAmount));
                    } else {
                    yVals.add(new Entry(Integer.parseInt(date.replace("-","").replace(getYearTime,"")), 0));
                    }
//                    yVals.add(new Entry(Integer.parseInt(date.replaceAll("-","")), mAmount));
//                    yVals.add(new Entry(Integer.parseInt(date.replaceAll("-","")), mAmount));
                    initLineDataSet(yVals);
                });
                // Calendar의 날짜를 하루씩 증가한다.
                cal.add(Calendar.DATE, 1); // one day increment

                // 현재 날짜가 종료일자보다 크면 종료
                if (getDateByInteger(cal.getTime()) > endDt) break;

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

    private void initLineDataSet(ArrayList<Entry> yVals ){
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("일");
        labels.add("월");
        labels.add("화");
        labels.add("수");
        labels.add("목");
        labels.add("금");
        labels.add("토");

        LineDataSet set1;
        if(mChart.getData() != null &&
        mChart.getData().getDataSetCount() > 0){
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        }
        else{
            set1 = new LineDataSet(yVals,"data Set");
            set1.setDrawValues(false);
            set1.setForm(Legend.LegendForm.EMPTY);
            set1.setFillAlpha(50);
            set1.setFillColor(getResources().getColor(R.color.sky2));
            set1.setDrawFilled(true);
            set1.setColor(getResources().getColor(R.color.sky));
            set1.setCircleColor(getResources().getColor(R.color.sky));
            set1.setLineWidth(1.5f);
            set1.setCircleRadius(5f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
//            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1);

            LineData data = new LineData(set1);
            ValueFormatter custom = new MyValueFormatter("ml");
            data.setDrawValues(true);
            data.setValueFormatter(custom);
            data.setValueTextSize(10f);
            data.setHighlightEnabled(true);
            mChart.setDrawGridBackground(false);
            mChart.setData(data);
        }
        mChart.invalidate();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_water_graph_week);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public static class Instantiator extends ContextFragmentInstantiator {

        public Instantiator(Context context) {
            super(context);
        }

        @Override
        public String getTitle(Context context, int position) {
            return context.getString(R.string.title_water_graph_week);
        }

        @Nullable
        @Override
        public WaterGraphBasePagerFragment newInstance(int position) {
            return new WaterGraphWeekFragment();
        }
    }

    public static int getDateByInteger(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(sdf.format(date));
    }

    public static String getDateByString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

}
