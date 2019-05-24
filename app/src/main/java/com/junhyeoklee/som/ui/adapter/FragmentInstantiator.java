package com.junhyeoklee.som.ui.adapter;

import androidx.annotation.Nullable;

import com.junhyeoklee.som.ui.fragment.WaterGraphBasePagerFragment;

public interface FragmentInstantiator {
    @Nullable
    WaterGraphBasePagerFragment newInstance(int position);
    @Nullable
    String getTitle(int position);
}
