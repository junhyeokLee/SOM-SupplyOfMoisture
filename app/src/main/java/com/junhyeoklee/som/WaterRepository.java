package com.junhyeoklee.som;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
    private final MutableLiveData<List<WaterEntry>> mLoadWaterList;
    private final MutableLiveData<String> mWaterGetDate;

    private WaterRepository(WaterDao waterDao,WaterNetworkRoot waterNetworkRoot,
                            AppExecutors executors){
        mWaterDao = waterDao;
        mWaterNetworkRoot = waterNetworkRoot;
        mExcutors = executors;
        mLoadWaterList = new MutableLiveData<>();
        mWaterGetDate = new MutableLiveData<>();

        LiveData<List<WaterEntry>> getLoadWaters = mWaterNetworkRoot.getLoadWaterList();
        getLoadWaters.observeForever(newWatersFromNetwork -> mExcutors.diskIO().execute(() ->{
            String getDate = null;
            if(newWatersFromNetwork != null){
//                mWaterGetDate.postValue(getDate);
                mWaterDao.loadWaterBydate(getDate);
            }
            else{
                Log.e("No Response","No Response from network");
            }
        }));

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
        MutableLiveData<String> mLivedate = new MutableLiveData<>();
        mLivedate.setValue(date);
        return mWaterDao.loadWaterBydate(date);
    }

    private synchronized void initializedDate(){
        if(mInitialized) return;
        mInitialized = true;

        mExcutors.diskIO().execute(mWaterNetworkRoot::startWaterFetchService);
    }
}
