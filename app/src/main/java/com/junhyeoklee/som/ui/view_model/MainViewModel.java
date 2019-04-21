package com.junhyeoklee.som.ui.view_model;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.model.WaterEntry;

import java.util.List;

public class MainViewModel extends ViewModel{

    private LiveData<List<WaterEntry>> waters;

    public MainViewModel(WaterDatabase database) {
        if (database != null) {
            this.waters = database.waterDao().loadAllWaters();
        }
    }
    public LiveData<List<WaterEntry>> getWaters(){return waters;}

}
