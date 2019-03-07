package com.junhyeoklee.som.util;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.style.ForegroundColorSpan;

import com.junhyeoklee.som.ui.fragment.WaterListFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class DateUtil {

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
