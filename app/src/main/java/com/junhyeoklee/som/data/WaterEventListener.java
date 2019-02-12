package com.junhyeoklee.som.data;

import android.view.View;

public interface WaterEventListener {

    void updateWaterCount();

    void updateWaterProgrees();

    void updatewaterAmount();

    void incrementWater(View view);

    void onDisplayBoard(String aText);


}
