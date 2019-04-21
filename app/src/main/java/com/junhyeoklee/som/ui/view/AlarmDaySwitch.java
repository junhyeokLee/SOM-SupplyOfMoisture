package com.junhyeoklee.som.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.junhyeoklee.som.R;

import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;
import me.jfenn.androidutils.DimenUtils;

public class AlarmDaySwitch extends View implements View.OnClickListener {

    private Paint accentPaint;
    private Paint textPaint;
    private Paint clippedTextPaint;

    private Disposable colorAccentSubscription;
    private Disposable textColorPrimarySubscription;
    private Disposable textColorPrimaryInverseSubscription;

    private float checked;
    private boolean isChecked;
    private int textColorPrimary;
    private int textColorPrimaryInverse;
    private String text;
    private OnCheckedChangeListener listener;

    public AlarmDaySwitch(Context context) {
        this(context, null);
    }

    public AlarmDaySwitch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlarmDaySwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);

        accentPaint = new Paint();
        accentPaint.setColor(getResources().getColor(R.color.lightsky));
        accentPaint.setStyle(Paint.Style.FILL);
        accentPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(DimenUtils.dpToPx(18));
        textPaint.setTextAlign(Paint.Align.CENTER);

        clippedTextPaint = new Paint();
        clippedTextPaint.setAntiAlias(true);
        clippedTextPaint.setTextSize(DimenUtils.dpToPx(18));
        clippedTextPaint.setTextAlign(Paint.Align.CENTER);

    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setChecked(boolean isChecked) {
        if (isChecked != this.isChecked) {
            this.isChecked = isChecked;
            textPaint.setColor(isChecked ? textColorPrimaryInverse : textColorPrimary);

            ValueAnimator animator = ValueAnimator.ofFloat(isChecked ? 0 : 1, isChecked ? 1 : 0);
            if (isChecked)
                animator.setInterpolator(new DecelerateInterpolator());
            else animator.setInterpolator(new AnticipateOvershootInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    checked = (float) valueAnimator.getAnimatedValue();
                    accentPaint = new Paint();
                    accentPaint.setColor(getResources().getColor(R.color.lightsky));
                    accentPaint.setStyle(Paint.Style.FILL);
                    accentPaint.setAntiAlias(true);

                    textPaint = new Paint();
                    textPaint.setAntiAlias(true);
                    textPaint.setTextSize(DimenUtils.dpToPx(18));
                    textPaint.setTextAlign(Paint.Align.CENTER);

                    clippedTextPaint = new Paint();
                    clippedTextPaint.setAntiAlias(true);
                    clippedTextPaint.setTextSize(DimenUtils.dpToPx(18));
                    clippedTextPaint.setTextAlign(Paint.Align.CENTER);
                    invalidate();
                }
            });
            animator.start();
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = DimenUtils.dpToPx(18);

        if (text != null)
            canvas.drawText(text, canvas.getWidth() / 2, ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)), textPaint);

        Path circlePath = new Path();
        circlePath.addCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, checked * size, Path.Direction.CW);
        circlePath.close();

        canvas.drawPath(circlePath, accentPaint);

        if (text != null) {
            canvas.drawText(text, canvas.getWidth() / 2, ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)), textPaint);
            canvas.clipPath(circlePath);
            canvas.drawText(text, canvas.getWidth() / 2, ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)), clippedTextPaint);
        }
    }

    @Override
    public void onClick(View view) {
        setChecked(!isChecked);
        if (listener != null)
            listener.onCheckedChanged(this, isChecked);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(AlarmDaySwitch alarmDaySwitch, boolean b);
    }
}
