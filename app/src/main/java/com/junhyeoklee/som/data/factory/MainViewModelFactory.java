package com.junhyeoklee.som.data.factory;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.ui.view_model.AddWaterViewModel;
import com.junhyeoklee.som.ui.view_model.MainViewModel;

@SuppressWarnings("unchecked")
public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final WaterDatabase mDb;

    public MainViewModelFactory(WaterDatabase database){
        mDb = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(mDb);
    }
}
