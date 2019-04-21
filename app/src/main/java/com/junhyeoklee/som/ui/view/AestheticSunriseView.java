package com.junhyeoklee.som.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;
import me.jfenn.sunrisesunsetview.SunriseSunsetView;

public class AestheticSunriseView extends SunriseSunsetView {

    private Disposable colorAccentSubscription;
    private Disposable textColorPrimarySubscription;

    public AestheticSunriseView(Context context) {
        super(context);
        setClickable(false);
        setFocusable(false);
    }

    public AestheticSunriseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        setFocusable(false);
    }

    public AestheticSunriseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(false);
        setFocusable(false);
    }
}
