package com.junhyeoklee.som.data.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.junhyeoklee.som.WaterRepository;
import com.junhyeoklee.som.ui.view_model.AddWaterDateViewModel;


public class AddWaterViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final WaterRepository mRepository;

    public AddWaterViewModelFactory(WaterRepository repository){
        mRepository = repository;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddWaterDateViewModel(mRepository);
    }
}
