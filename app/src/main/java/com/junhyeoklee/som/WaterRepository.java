package com.junhyeoklee.som;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.util.Log;

import com.junhyeoklee.som.data.model.WaterDao;
import com.junhyeoklee.som.data.model.WaterEntry;

import java.util.List;

public class WaterRepository {

    private static final Object LOCK = new Object();
    private static WaterRepository sInstance;

    private final WaterDao mWaterDao;
    private final WaterNetworkRoot mWaterNetworkRoot;
    private final AppExecutors mExcutors;
    private boolean mInitialized = false;


    private WaterRepository(WaterDao waterDao,WaterNetworkRoot waterNetworkRoot,
                            AppExecutors executors){
        mWaterDao = waterDao;
        mWaterNetworkRoot = waterNetworkRoot;
        mExcutors = executors;
    }

    public synchronized static WaterRepository getInstance(WaterDao waterDao,WaterNetworkRoot waterNetworkRoot,
                                                           AppExecutors executors){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = new WaterRepository(waterDao,waterNetworkRoot,executors);
            }
        }
        return sInstance;
    }

    public LiveData<List<WaterEntry>> getWaterDate(String date){

        LiveData<List<WaterEntry>> getLoadWaters = mWaterNetworkRoot.getLoadWaterList();
        getLoadWaters.observeForever(newWatersFromNetwork -> mExcutors.diskIO().execute(() ->{
            if(newWatersFromNetwork != null){
                mWaterDao.loadWaterBydate(date);
            }
            else{
                Log.e("No Response","No Response from network");
            }
        }));

        initializedDate();
        return mWaterDao.loadWaterBydate(date);
    }
    public LiveData<List<WaterEntry>> getWaterDateMonth(String dateMonth){

        LiveData<List<WaterEntry>> getLoadWaters = mWaterNetworkRoot.getLoadWaterList();
        getLoadWaters.observeForever(newWatersFromNetwork -> mExcutors.diskIO().execute(() ->{
            if(newWatersFromNetwork != null){
                mWaterDao.loadWaterBydateMonth(dateMonth);
            }
            else{
                Log.e("No Response","No Response from network");
            }
        }));

        initializedDate();
        return mWaterDao.loadWaterBydateMonth(dateMonth);
    }

    private synchronized void initializedDate(){
        if(mInitialized) return;
        mInitialized = true;

        mExcutors.diskIO().execute(mWaterNetworkRoot::startWaterFetchService);
    }
}
