package com.junhyeoklee.som.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.aesthetic.AestheticActivity;
import com.junhyeoklee.som.Alarmio;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.alarm.AlarmData;
import com.junhyeoklee.som.data.alarm.AlarmPreferenceData;
import com.junhyeoklee.som.util.AlarmFormatUtils;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import io.reactivex.disposables.Disposable;
import me.jfenn.slideactionview.SlideActionListener;
import me.jfenn.slideactionview.SlideActionView;

public class AlarmActivity extends AestheticActivity implements SlideActionListener {

    public static final String EXTRA_ALARM = "james.alarmio.AlarmActivity.EXTRA_ALARM";

    private View overlay;
    private TextView date;
    private SlideActionView actionView;

    private Alarmio alarmio;
    private Vibrator vibrator;

    private boolean isAlarm;
    private long triggerMillis;
    private AlarmData alarm;
    private boolean isVibrate;

    private boolean isSlowWake;
    private long slowWakeMillis;

    private Handler handler;
    private Runnable runnable;

    private Disposable textColorPrimaryInverseSubscription;
    private Disposable isDarkSubscription;

    private boolean isDark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        alarmio = (Alarmio) getApplicationContext();

        overlay = findViewById(R.id.overlay);
        date = findViewById(R.id.date);
        actionView = findViewById(R.id.slideView);

        actionView.setLeftIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_close, getTheme()));
        actionView.setRightIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_check, getTheme()));
        actionView.setListener(this);

        isSlowWake = AlarmPreferenceData.SLOW_WAKE_UP.getValue(this);
        slowWakeMillis = AlarmPreferenceData.SLOW_WAKE_UP_TIME.getValue(this);

        isAlarm = getIntent().hasExtra(EXTRA_ALARM);
        if (isAlarm) {
            alarm = getIntent().getParcelableExtra(EXTRA_ALARM);
            isVibrate = alarm.isVibrate;
        }
        else finish();

        date.setText(AlarmFormatUtils.format(new Date(), AlarmFormatUtils.FORMAT_DATE + ", " + AlarmFormatUtils.getShortFormat(this)));

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        triggerMillis = System.currentTimeMillis();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                if (isVibrate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    else vibrator.vibrate(500);
                }

//                if (alarm != null && isSlowWake) {
//                    WindowManager.LayoutParams params = getWindow().getAttributes();
//                    params.screenBrightness = Math.max(0.01f, Math.min(1f, (float) elapsedMillis / slowWakeMillis));
//                    getWindow().setAttributes(params);
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAGS_CHANGED);
//                }

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);

//        com.junhyeoklee.som.services.SleepReminderService.refreshSleepTime(alarmio);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textColorPrimaryInverseSubscription != null && isDarkSubscription != null) {
            textColorPrimaryInverseSubscription.dispose();
            isDarkSubscription.dispose();
        }

        stopAnnoyingness();
    }
    private void stopAnnoyingness() {
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        finish();
        startActivity(new Intent(intent));
    }

    @Override
    public void onSlideLeft() {
        overlay.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        finish();
    }

    @Override
    public void onSlideRight() {
        overlay.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        Intent lIntetn = new Intent(AlarmActivity.this,MainActivity.class);

        startActivity(lIntetn);
        finish();
    }
}
