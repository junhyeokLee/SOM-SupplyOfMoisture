package com.junhyeoklee.som.data.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.ui.view_model.MainViewModel;

@SuppressWarnings("unchecked")
public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final WaterDatabase mDb;

    public MainViewModelFactory(WaterDatabase database){
        this.mDb = database;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(mDb);
    }
}
