package com.junhyeoklee.som.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import com.junhyeoklee.som.util.AlarmFormatUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;

public class AlarmDigitalClockView extends View implements ViewTreeObserver.OnGlobalLayoutListener {

    private Paint paint;

    private TimeZone timezone;

    private Disposable textColorPrimarySubscription;

    public AlarmDigitalClockView(Context context) {
        this(context, null, 0);
    }

    public AlarmDigitalClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmDigitalClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        timezone = TimeZone.getDefault();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);

        new UpdateThread(this).start();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void setTimezone(String timezone) {
        this.timezone = TimeZone.getTimeZone(timezone);
    }


    @Override
    public void onGlobalLayout() {
        paint.setTextSize(48f);
        Rect bounds = new Rect();
        paint.getTextBounds("00:00:00", 0, 8, bounds);
        paint.setTextSize((48f * getMeasuredWidth()) / (bounds.width() * 2));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TimeZone defaultZone = TimeZone.getDefault();
        TimeZone.setDefault(timezone);
        canvas.drawText(AlarmFormatUtils.format(getContext(), Calendar.getInstance().getTime()), canvas.getWidth() / 2, (canvas.getHeight() - paint.ascent()) / 2, paint);
        TimeZone.setDefault(defaultZone);
    }

    private static class UpdateThread extends Thread {

        private WeakReference<AlarmDigitalClockView> viewReference;

        private UpdateThread(AlarmDigitalClockView view) {
            viewReference = new WeakReference<>(view);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        AlarmDigitalClockView view = viewReference.get();
                        if (view != null)
                            view.invalidate();
                    }
                });
            }
        }
    }
}
