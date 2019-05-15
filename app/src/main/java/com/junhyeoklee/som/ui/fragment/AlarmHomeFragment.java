package com.junhyeoklee.som.ui.fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.junhyeoklee.som.Alarmio;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.alarm.AlarmData;
import com.junhyeoklee.som.data.alarm.AlarmPreferenceData;
import com.junhyeoklee.som.ui.adapter.AlarmSimplePagerAdapter;
import com.junhyeoklee.som.ui.adapter.AlarmsAdapter;
import com.junhyeoklee.som.ui.view.AestheticTimeSheetPickerDialog;
import com.junhyeoklee.som.ui.view.AlarmPageIndicatorView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;
import me.jfenn.androidutils.DimenUtils;
import me.jfenn.timedatepickers.dialogs.PickerDialog;
import me.jfenn.timedatepickers.views.LinearTimePickerView;

public class AlarmHomeFragment extends AlarmBaseFragment {

    private View view;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPager timePager;
    private AlarmPageIndicatorView timeIndicator;
    private View bottomSheet;
    private ImageView background;
    private View overlay;
    private FloatingActionButton menu;
    private TitleFAB alarmFab;


    private AlarmSimplePagerAdapter pagerAdapter;
    private AlarmSimplePagerAdapter timeAdapter;

    private BottomSheetBehavior behavior;
    private boolean shouldCollapseBack;

    private Disposable colorPrimarySubscription;
    private Disposable colorAccentSubscription;
    private Disposable textColorPrimarySubscription;
    private Disposable textColorPrimaryInverseSubscription;
    private List<AlarmData> alarms;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm_home, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        timePager = view.findViewById(R.id.timePager);
        bottomSheet = view.findViewById(R.id.bottomSheet);
        timeIndicator = view.findViewById(R.id.pageIndicator);
        background = view.findViewById(R.id.background);
        overlay = view.findViewById(R.id.overlay);
        menu = view.findViewById(R.id.fabsMenu);


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

        pagerAdapter = new AlarmSimplePagerAdapter(getContext(), getChildFragmentManager(), new AlarmsFragment());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() > 0) {
                    shouldCollapseBack = behavior.getState() != BottomSheetBehavior.STATE_EXPANDED;
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    menu.hide();
                } else {
                    setClockFragments();
                    menu.show();
                    if (shouldCollapseBack) {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        shouldCollapseBack = false;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setClockFragments();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                behavior.setPeekHeight(view.getMeasuredHeight() / 2);
                view.findViewById(R.id.timeContainer).setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight() / 2));
            }
        });

        colorPrimarySubscription = Aesthetic.Companion.get()
                .colorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        bottomSheet.setBackgroundColor(integer);
                        overlay.setBackgroundColor(integer);
                    }
                });

        colorAccentSubscription = Aesthetic.Companion.get()
                .colorAccent()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
//                        menu.setMenuButtonColor(integer);
                    }
                });

        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                    }
                });

        textColorPrimaryInverseSubscription = Aesthetic.Companion.get()
                .textColorPrimaryInverse()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                    }
                });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE}, 0);
                    else  new AestheticTimeSheetPickerDialog(view.getContext())
                            .setListener(new PickerDialog.OnSelectedListener<LinearTimePickerView>() {
                                @Override
                                public void onSelect(PickerDialog<LinearTimePickerView> dialog, LinearTimePickerView view) {
                                    AlarmManager manager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
                                    AlarmData alarm = getAlarmio().newAlarm();
                                    alarm.time.set(Calendar.HOUR_OF_DAY, view.getHourOfDay());
                                    alarm.time.set(Calendar.MINUTE, view.getMinute());
                                    alarm.setTime(getAlarmio(), manager, alarm.time.getTimeInMillis());
                                    alarm.setEnabled(getContext(), manager, true);

                                    getAlarmio().onAlarmsChanged();
                                }

                                @Override
                                public void onCancel(PickerDialog<LinearTimePickerView> dialog) {
                                }
                            })
                            .show();

//                    menu.collapse();
                }
            }
        });

//        menu.setMenuListener(new FABsMenuListener() {
//            @Override
//            public void onMenuExpanded(FABsMenu fabsMenu) {
//
//            }
//        });

        return view;
    }

    /**
     * Update the time zones displayed in the clock fragments pager.
     */
    private void setClockFragments() {
        if (timePager != null && timeIndicator != null) {
            List<AlarmClockFragment> fragments = new ArrayList<>();

            AlarmClockFragment fragment = new AlarmClockFragment();
            fragments.add(fragment);

            for (String id : TimeZone.getAvailableIDs()) {
                if (AlarmPreferenceData.TIME_ZONE_ENABLED.getSpecificValue(getContext(), id)) {
                    Bundle args = new Bundle();
                    args.putString(AlarmClockFragment.EXTRA_TIME_ZONE, id);
                    fragment = new AlarmClockFragment();
                    fragment.setArguments(args);
                    fragments.add(fragment);
                }
            }

            timeAdapter = new AlarmSimplePagerAdapter(getContext(), getChildFragmentManager(), fragments.toArray(new AlarmClockFragment[0]));
            timePager.setAdapter(timeAdapter);
            timeIndicator.setViewPager(timePager);
            timeIndicator.setVisibility(fragments.size() > 1 ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        colorPrimarySubscription.dispose();
        colorAccentSubscription.dispose();
        textColorPrimarySubscription.dispose();
        textColorPrimaryInverseSubscription.dispose();
        super.onDestroyView();
    }


}
