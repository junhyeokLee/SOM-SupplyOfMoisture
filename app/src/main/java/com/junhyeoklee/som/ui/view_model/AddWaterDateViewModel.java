package com.junhyeoklee.som.ui.view_model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.junhyeoklee.som.WaterRepository;
import com.junhyeoklee.som.data.model.WaterEntry;

import java.util.List;


public class AddWaterDateViewModel extends ViewModel {

    private static final String TAG = AddWaterDateViewModel.class.getSimpleName();
    private final WaterRepository mRepository;
    private LiveData<List<WaterEntry>> water_date = new MutableLiveData<>();

    public AddWaterDateViewModel(WaterRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<List<WaterEntry>> getWater_date(String dateid) {
        return mRepository.getWaterDate(dateid);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
