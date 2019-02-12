package com.junhyeoklee.som.data;

import android.content.Context;

import com.junhyeoklee.som.data.model.WaterEntry;

public class WaterManager {

    private Context mContext = null;

    public WaterEventListener getmWaterEventListener() {
        return mWaterEventListener;
    }

    public void setmWaterEventListener(WaterEventListener mWaterEventListener) {
        this.mWaterEventListener = mWaterEventListener;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public WaterEntry getmWater() {
        return mWater;
    }

    public void setmWater(WaterEntry mWater) {
        this.mWater = mWater;
    }

    private WaterEventListener mWaterEventListener = null;

    private WaterEntry mWater;

    private static WaterManager gHpWaterManager;

    public static WaterManager get() {
        if (null == gHpWaterManager) {
            gHpWaterManager = new WaterManager();
        }
        return gHpWaterManager;
    }

    private WaterManager() {
    }


}
