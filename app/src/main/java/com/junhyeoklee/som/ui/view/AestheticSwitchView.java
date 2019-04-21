package com.junhyeoklee.som.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.SwitchCompat;
import io.reactivex.disposables.Disposable;

public class AestheticSwitchView extends SwitchCompat {

    private Disposable colorAccentSubscription;
    private Disposable textColorPrimarySubscription;

    public AestheticSwitchView(Context context) {
        super(context);
    }

    public AestheticSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
