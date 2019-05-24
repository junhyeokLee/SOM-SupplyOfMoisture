package com.junhyeoklee.som.ui.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.junhyeoklee.som.ui.fragment.WaterGraphBasePagerFragment;

public class WaterSimplePagerAdapter extends FragmentStatePagerAdapter {

    private FragmentInstantiator[] fragments;

    public WaterSimplePagerAdapter(Context context, FragmentManager fm, FragmentInstantiator... fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public WaterGraphBasePagerFragment getItem(int position) {
        return fragments[position].newInstance(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getTitle(position);
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
