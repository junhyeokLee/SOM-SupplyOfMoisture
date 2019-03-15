package com.junhyeoklee.som.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.style.ForegroundColorSpan;

import com.junhyeoklee.som.ui.fragment.WaterListFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DateUtil {

    public final String FORMAT_DATE = "yyyy-MM-dd";


    public String getFarDay(int far){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,far);
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.this.FORMAT_DATE);
        String currentDateAndTime = sdf.format(calendar.getTime());
        return currentDateAndTime;
    }

    public int getDateDay(String date,String dateType){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
            Date nDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nDate);
            return calendar.get(Calendar.DAY_OF_WEEK) -1;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return -1;
    }

    public int getDayofWeek(String data , String dateType){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateType);
            Date nDate = dateFormat.parse(data);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nDate);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek;
        }catch (ParseException e){
            e.printStackTrace();
        }

        return -1;
    }

    public String getDayNameList(String days){
      StringBuilder builder = new StringBuilder();
      if(days.contains("0")) builder.append("일");
        if(days.contains("1")) builder.append("월");
        if(days.contains("2")) builder.append("화");
        if(days.contains("3")) builder.append("수");
        if(days.contains("4")) builder.append("목");
        if(days.contains("5")) builder.append("금");
        if(days.contains("6")) builder.append("토");

      return builder.toString();
    }

    public String getIndexOfDayName(int index){
        String dName;
        switch (index){
            case 1 : dName = "월요일";
            case 2 : dName = "화요일";
            case 3 : dName = "수요일";
            case 4 : dName = "목요일";
            case 5 : dName = "금요일";
            case 6 : dName = "토요일";
            default: dName = "일요일";
        }
        return dName;
    }

    public String getIndexOfDayNameHead(int index){
        String dayName = " (일)";
        switch (index){
            case 1 : dayName = " (일)";
            case 2 : dayName = " (월)";
            case 3 : dayName = " (화)";
            case 4 : dayName = " (수)";
            case 5 : dayName = " (목)";
            case 6 : dayName = " (금)";
            case 7 : dayName = " (토)";
        }
        return dayName;
    }


    /* 캘린더 뷰 관련 DateUtil ( WaterListFragment )*/
    // 캘린더뷰 일요일 빨갠색으로 표시
    public static class SundayDecorator implements DayViewDecorator {
        private final Calendar calendar = Calendar.getInstance();
        public SundayDecorator(){
        }
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SUNDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.rgb(255,0,0)));
        }
    }
    // 캘린더뷰 토요일 파란색으로 표시
    public static class SaturdayDecorator implements DayViewDecorator {
        private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator(){
        }
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            day.copyTo(calendar);
            int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
            return weekDay == Calendar.SATURDAY;
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.rgb(255,0,0)));
        }
    }
    // 캘린더뷰 오늘날짜 색 설정
    public static class todayDecorator implements DayViewDecorator {
        private CalendarDay date;
        public todayDecorator(){
            date = CalendarDay.today();
        }
        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.rgb(0,216,255)));
            view.addSpan(new DotSpan(10,Color.rgb(0,216,255)));
        }

        public void setDate(Date date){
            this.date = CalendarDay.from(date);
        }
    }

    public static class ClickDecorator implements DayViewDecorator {

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return false;
        }

        @Override
        public void decorate(DayViewFacade view) {
        }
    }

    public static class EventDecorator implements DayViewDecorator {
        private int color;
        private HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates, WaterListFragment WaterListFragment){
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(10,color));
        }

    }


}
