package com.junhyeoklee.som.ui.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junhyeoklee.som.AppExecutors;
import com.junhyeoklee.som.util.DateUtil;
import com.junhyeoklee.som.util.InjectorUtils;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.AddWaterViewModelFactory;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.adapter.AddWaterAdapter;
import com.junhyeoklee.som.ui.view_model.AddWaterDateViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatDayFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class AddWaterFragment extends Fragment {
    public static final String TAG = AddWaterFragment.class.getSimpleName();
    private WaterDatabase mDb;
    private AddWaterAdapter mAdapter;
    private Date calendarDate = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormatDayFormatter dateFormatDayFormatter = new DateFormatDayFormatter(sdf);
    private CalendarDay calendarDay = new CalendarDay();
    private String getDateFormatDay;

    @BindView(R.id.recyclerViewWaters)
    RecyclerView mRecyclerView;
    TextView mImgView;
    @BindView(R.id.calendarView)
    MaterialCalendarView mMaterialCalendarView;


    private AddWaterDateViewModel viewModel;

    private List<WaterEntry> mWaterList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_drink, container, false);
        ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mAdapter = new AddWaterAdapter(view.getContext());
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this.getContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        mDb = WaterDatabase.getInstance(this.getContext());

        mMaterialCalendarView.addDecorators(new DateUtil.SundayDecorator(), new DateUtil.SaturdayDecorator(), new DateUtil.todayDecorator());

        String getTime = sdf.format(calendarDate);
        Log.e(TAG, "DATE VALUE = " + " " + getTime);

        AddWaterViewModelFactory factory = InjectorUtils.provideWaterViewModelFactory(getContext());
        viewModel = ViewModelProviders.of(this, factory).get(AddWaterDateViewModel.class);
        viewModel.getWater_date(getTime).observe(this, waterEntries -> {
            mWaterList = waterEntries;
            mAdapter.setmWaterEntries(mWaterList);
        });

        mMaterialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                getDateFormatDay = dateFormatDayFormatter.format(date);
                viewModel.getWater_date(getDateFormatDay).observe(AddWaterFragment.this, waterEntries -> {
                    mWaterList = waterEntries;
                    mAdapter.setmWaterEntries(mWaterList);
                });
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<WaterEntry> tasks = mAdapter.getmWaterEntries();
                        mDb.waterDao().deleteWater(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);
        return view;
    }
}
