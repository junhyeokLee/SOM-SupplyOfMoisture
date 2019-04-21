package com.junhyeoklee.som.ui.adapter;

import android.content.Context;

import com.junhyeoklee.som.ui.fragment.AlarmBasePagerFragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class AlarmSimplePagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private AlarmBasePagerFragment[] fragments;

    public AlarmSimplePagerAdapter(Context context, FragmentManager fm, AlarmBasePagerFragment... fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public AlarmBasePagerFragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments[position].getTitle(context);
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
