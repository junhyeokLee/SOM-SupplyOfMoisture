package com.junhyeoklee.som.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import me.jfenn.slideactionview.SlideActionView;

public class AestheticSlideActionView extends SlideActionView {

    public AestheticSlideActionView(Context context) {
        super(context);
    }

    public AestheticSlideActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AestheticSlideActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AestheticSlideActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

}
