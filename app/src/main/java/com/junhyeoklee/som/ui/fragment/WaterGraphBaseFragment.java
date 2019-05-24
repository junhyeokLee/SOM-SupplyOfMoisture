package com.junhyeoklee.som.ui.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.junhyeoklee.som.listener.WaterGraphListener;

public abstract class WaterGraphBaseFragment extends Fragment implements WaterGraphListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void notifyDataSetChanged() {
    }


    @Override
    public void onMonthChanged() {

    }

    @Override
    public void onWeekChanged() {

    }
}
