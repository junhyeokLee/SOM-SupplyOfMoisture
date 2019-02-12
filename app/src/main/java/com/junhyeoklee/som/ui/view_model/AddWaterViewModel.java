package com.junhyeoklee.som.ui.view_model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.model.WaterEntry;

import java.util.List;


public class AddWaterViewModel extends AndroidViewModel {

    private static final String TAG = AddWaterViewModel.class.getSimpleName();

    private LiveData<List<WaterEntry>> water;

    public AddWaterViewModel(@NonNull Application application){
        super(application);
        WaterDatabase database = WaterDatabase.getInstance(this.getApplication());
        water = database.waterDao().loadAllWaters();
    }

    public LiveData<List<WaterEntry>> getWater() {return water;}
}
