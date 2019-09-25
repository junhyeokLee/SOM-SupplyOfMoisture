package com.junhyeoklee.som.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.ui.adapter.WaterSimplePagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WaterGraphHomeFragment extends WaterGraphBaseFragment {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_water_graph_home,container,false);
        ButterKnife.bind(this, view);

        WaterSimplePagerAdapter pagerAdapter = new WaterSimplePagerAdapter(getContext(),getChildFragmentManager(),
                new WaterGraphWeekFragment.Instantiator(getContext()),new WaterGraphFragment.Instantiator(getContext()));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

}
