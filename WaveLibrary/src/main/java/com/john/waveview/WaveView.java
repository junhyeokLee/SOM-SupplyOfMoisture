package com.john.waveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by John on 2014/10/15.
 */
public class WaveView extends LinearLayout {
    protected static final int LARGE = 1;
    protected static final int MIDDLE = 2;
    protected static final int LITTLE = 3;

    private int mAboveWaveColor;
    private int mBlowWaveColor;
    private int mProgress;
    private int mWaveHeight;
    private int mWaveMultiple;
    private int mWaveHz;

    private int mWaveToTop;
    private float mToTop;

    private Wave mWave;
    private Solid mSolid;

    private final int DEFAULT_ABOVE_WAVE_COLOR = Color.rgb(102,102,102);
    private final int DEFAULT_ABOVE_WAVE_COLOR2 = getResources().getColor(R.color.sky);
    private final int DEFAULT_BLOW_WAVE_COLOR = Color.rgb(102,102,102);
    private final int DEFAULT_BLOW_WAVE_COLOR2 = getResources().getColor(R.color.sky);

    private final int DEFAULT_PROGRESS = 50;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        //load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WaveView, R.attr.waveViewStyle, 0);
        mAboveWaveColor = attributes.getColor(R.styleable.WaveView_above_wave_color, DEFAULT_ABOVE_WAVE_COLOR2);
        mBlowWaveColor = attributes.getColor(R.styleable.WaveView_blow_wave_color, DEFAULT_BLOW_WAVE_COLOR2);
        mProgress = attributes.getInt(R.styleable.WaveView_progress, DEFAULT_PROGRESS);
        mWaveHeight = attributes.getInt(R.styleable.WaveView_wave_height, MIDDLE);
        mWaveMultiple = attributes.getInt(R.styleable.WaveView_wave_length, LARGE);
        mWaveHz = attributes.getInt(R.styleable.WaveView_wave_hz, MIDDLE);
        attributes.recycle();

        mWave = new Wave(context, null);
        mWave.initializeWaveSize(mWaveMultiple, mWaveHeight, mWaveHz);
        mWave.setAboveWaveColor(mAboveWaveColor);
        mWave.setBlowWaveColor(mBlowWaveColor);
        mWave.initializePainters();

        mSolid = new Solid(context, null);
        mSolid.setAboveWavePaint(mWave.getAboveWavePaint());
        mSolid.setBlowWavePaint(mWave.getBlowWavePaint());

        addView(mWave);
        addView(mSolid);

        setProgress(mProgress,mToTop);
    }

    public void setProgress(int progress,float mToTop) {
        this.mProgress = progress > (int)mToTop ? (int)mToTop : progress;
        //computeWaveToTop();
        setWaveToTop(mToTop);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            computeWaveToTop();
        }
    }

    public void computeWaveToTop() {
        mWaveToTop = (int) (getHeight() * (1f - mProgress / 1000f));
        ViewGroup.LayoutParams params = mWave.getLayoutParams();
        if (params != null) {
            ((LayoutParams) params).topMargin = mWaveToTop;
        }
        mWave.setLayoutParams(params);
    }

    public void setWaveToTop(float mToTop){
        mWaveToTop = (int) (getHeight() * (1f - mProgress / mToTop));
        ViewGroup.LayoutParams params = mWave.getLayoutParams();
        ((LayoutParams) params).topMargin = mWaveToTop;
        mWave.setLayoutParams(params);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = mProgress;
        ss.toTop = mToTop;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setProgress(ss.progress,ss.toTop);
    }


    private static class SavedState extends BaseSavedState {
        int progress;
        float toTop;

        /**
         * Constructor called from {@link android.widget.ProgressBar#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
            toTop = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
