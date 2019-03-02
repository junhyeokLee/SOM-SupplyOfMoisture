package com.junhyeoklee.som.util;

import android.content.Context;

import com.junhyeoklee.som.AppExecutors;
import com.junhyeoklee.som.WaterNetworkRoot;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.AddWaterViewModelFactory;
import com.junhyeoklee.som.WaterRepository;

public class InjectorUtils {

    private static WaterRepository provideRepository(Context context) {
        WaterDatabase database = WaterDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        WaterNetworkRoot networkDataSource =
                WaterNetworkRoot.getInstance(context.getApplicationContext(), executors);
        return WaterRepository.getInstance(database.waterDao(), networkDataSource, executors);
    }

    // for services and jobs (external access)
    public static WaterNetworkRoot provideNetworkDataSource(Context context) {
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return WaterNetworkRoot.getInstance(context.getApplicationContext(), executors);
    }

    public static AddWaterViewModelFactory provideWaterViewModelFactory(Context context) {
        WaterRepository repository = provideRepository(context.getApplicationContext());
        return new AddWaterViewModelFactory(repository);
    }
}
