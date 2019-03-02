package com.junhyeoklee.som;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;

import com.junhyeoklee.som.data.model.WaterEntry;

import java.util.List;


public class WaterNetworkRoot {

    // Singleton instantiation
    private static final Object LOCK = new Object();
    @SuppressLint("StaticFieldLeak")
    private static WaterNetworkRoot sInstance;

    // MutableLiveData with expected return type to notify all observers with postValue
    private final MutableLiveData<List<WaterEntry>> mWaterLoadList;

    private final AppExecutors mExecutors;
    private final Context mContext;

    private WaterNetworkRoot(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mWaterLoadList = new MutableLiveData<>();
    }

    // Get singleton for this class
    public static WaterNetworkRoot getInstance(Context context, AppExecutors executors) {
//        Timber.d("Getting the network data source");
        // Only one instance of this class can be created
        if (sInstance == null) {
            // and only one thread can access this method at a time for data consistency
            synchronized (LOCK) {
                sInstance = new WaterNetworkRoot(context.getApplicationContext(), executors);
            }
        }
        return sInstance;
    }

    public LiveData<List<WaterEntry>> getLoadWaterList() {
        mExecutors.networkIO().execute(() ->{
            try {
                List<WaterEntry> waterEntries = null;
                mWaterLoadList.setValue(waterEntries);
            }catch (Exception e){

            }
        });
        return mWaterLoadList;
    }

    // calls fetchRecipes from service (before GUI shows up, in BG)
    public void startWaterFetchService() {
        Intent intentToFetch = new Intent(mContext, WaterNetworkRoot.class);
        mContext.startService(intentToFetch);
    }

}
