package com.junhyeoklee.som.ui.fragment;


import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.junhyeoklee.som.AppExecutors;
import com.junhyeoklee.som.data.factory.MainViewModelFactory;
import com.junhyeoklee.som.ui.view_model.MainViewModel;
import com.junhyeoklee.som.util.DateUtil;
import com.junhyeoklee.som.util.InjectorUtils;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.AddWaterViewModelFactory;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.adapter.WaterListAdapter;
import com.junhyeoklee.som.ui.view_model.AddWaterDateViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.DateFormatDayFormatter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.jfenn.androidutils.DimenUtils;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

public class WaterListFragment extends WaterListBasePagerFragment {
    public static final String TAG = WaterListFragment.class.getSimpleName();
    private WaterDatabase mDb;
    private WaterListAdapter mAdapter;
    private Date calendarDate = new Date();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormatDayFormatter dateFormatDayFormatter = new DateFormatDayFormatter(sdf);
    private String getDateFormatDay;

    @BindView(R.id.recyclerViewWaters)
    RecyclerView mRecyclerView;

    @BindView(R.id.calendarView)
    MaterialCalendarView mMaterialCalendarView;

    //    @BindView(R.id.sliding_layout)
//    SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.empty)
    LinearLayout mEmptyLayout;
    @BindView(R.id.tv_total)
    TextView mTv_total;

    private AddWaterDateViewModel viewModel;
    private MainViewModel mainViewModel;
    private List<WaterEntry> mWaterList;
    private View bottomSheet;
    private BottomSheetBehavior behavior;
    private View view;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_drink, container, false);
        ButterKnife.bind(this, view);
        ((TextView) view.findViewById(R.id.emptyText)).setText(R.string.msg_water_graph_daily_empty);
        bottomSheet = view.findViewById(R.id.bottomSheet);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.title_waters)));
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                private int statusBarHeight = -1;

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                        bottomSheet.setPadding(0, 0, 0, 0);
                    else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        if (statusBarHeight < 0)
                            statusBarHeight = DimenUtils.getStatusBarHeight(getContext());

                        bottomSheet.setPadding(0, statusBarHeight, 0, 0);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    if (statusBarHeight < 0)
                        statusBarHeight = DimenUtils.getStatusBarHeight(getContext());

                    bottomSheet.setPadding(0, (int) (slideOffset * statusBarHeight), 0, 0);
                }
            });
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                behavior.setPeekHeight(view.getMeasuredHeight() / 2);
                view.findViewById(R.id.calendarContainer).setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight() / 2));
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mAdapter = new WaterListAdapter(view.getContext());
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this.getContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

//        slidingUpPanelLayout.setPanelHeight(1020);
//        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View panel, float slideOffset) {
//            }
//
//            @Override
//            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                if(newState.name().toString().equalsIgnoreCase("Collapsed")){
//                    //action when collapsed
//                }else if(newState.name().equalsIgnoreCase("Expanded")){
//                    //action when expanded
//                }
//            }
//        });

        mDb = WaterDatabase.getInstance(this.getContext());

        mMaterialCalendarView.addDecorators(new DateUtil.SundayDecorator(), new DateUtil.SaturdayDecorator(), new DateUtil.todayDecorator());

        String getTime = sdf.format(calendarDate);
        Log.e(TAG, "DATE VALUE = " + " " + getTime);

        // 오늘 날짜의 리스트 보여주기
        AddWaterViewModelFactory factory = InjectorUtils.provideWaterViewModelFactory(getContext());
        viewModel = ViewModelProviders.of(this, factory).get(AddWaterDateViewModel.class);
        viewModel.getWater_date(getTime).observe(this, waterEntries -> {
            mWaterList = waterEntries;
            TotalValutUI(waterEntries);
            mAdapter.setmWaterEntries(mWaterList);
        });

        // 물을 마셨던 모든 날짜들 Decoration 이벤트 주기
        MainViewModelFactory mainFactory = new MainViewModelFactory(mDb);
        mainViewModel = ViewModelProviders.of(this, mainFactory).get(MainViewModel.class);
        mainViewModel.getWaters().observe(this, waterEntries -> {
            String[] result;
            for (int i = 0; i < waterEntries.size(); i++) {
                result = new String[]{waterEntries.get(i).getDate(), waterEntries.get(i).getDate()};
                if (result != null) {
                    new ApiSimulator(result).executeOnExecutor(newSingleThreadExecutor());
                }
                String[] result2 = {"2019-03-01", "2019-03-02", "2019-03-03"};
                Log.e(TAG, "WATER DATE LIST STATIC = " + " " + result2.toString());
                Log.e(TAG, "WATER DATE LIST = " + " " + result.toString());
            }
        });

        // Calendar뷰 의 해당날짜의 마신양의 리스트 출력
        mMaterialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                getDateFormatDay = dateFormatDayFormatter.format(date);
                viewModel.getWater_date(getDateFormatDay).observe(WaterListFragment.this, waterEntries -> {
                    TotalValutUI(waterEntries);
                    mWaterList = waterEntries;
                    mAdapter.setmWaterEntries(mWaterList);
                });
            }
        });

        // 리스트 아이템 선택 스와이프시 삭제
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

    private void TotalValutUI(final List<WaterEntry> waters) {
        int TotalDrinkValue = 0;
        String dispPattern = "0";
        final DecimalFormat form = new DecimalFormat(dispPattern);

        for (int i = 0; i < waters.size(); i++) {
            if (waters != null) {
                TotalDrinkValue += waters.get(i).getAmount();
            }
        }

        if(TotalDrinkValue == 0){
            // 물을 마신데이터가 없다고 알려줌.
            mEmptyLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            mEmptyLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mTv_total.setText(String.valueOf(TotalDrinkValue));
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_waters);
    }

    // Calendar뷰의 날짜 Decoration Event Class
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;

        ApiSimulator(String[] Time_Result) {
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();
            CalendarDay today = CalendarDay.today();
            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for (int i = 0; i < Time_Result.length; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split("-");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);
                dates.add(day);
                dates.remove(today);
                calendar.set(year, month - 1, dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            mMaterialCalendarView.addDecorator(new DateUtil.EventDecorator(Color.BLUE, calendarDays, WaterListFragment.this));
        }
    }

}
