package com.junhyeoklee.som.data.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.junhyeoklee.som.WaterRepository;
import com.junhyeoklee.som.ui.view_model.WaterGraphViewModel;


public class WaterGraphViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final WaterRepository mRepository;

    public WaterGraphViewModelFactory(WaterRepository repository){
        mRepository = repository;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WaterGraphViewModel(mRepository);
    }
}
