package com.junhyeoklee.som.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.afollestad.aesthetic.AestheticActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.junhyeoklee.som.Alarmio;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.ui.fragment.AlarmBaseFragment;
import com.junhyeoklee.som.ui.fragment.AlarmHomeFragment;
import com.junhyeoklee.som.ui.fragment.MainFragment;
import com.junhyeoklee.som.ui.fragment.PreferenceFragment;
import com.junhyeoklee.som.ui.fragment.PreferenceSetting;
import com.junhyeoklee.som.ui.fragment.SplashFragment;
import com.junhyeoklee.som.ui.fragment.WaterGraphFragment;
import com.junhyeoklee.som.ui.fragment.WaterGraphHomeFragment;
import com.junhyeoklee.som.ui.fragment.WaterListFragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AestheticActivity implements FragmentManager.OnBackStackChangedListener, Alarmio.ActivityListener {

    public static final String EXTRA_FRAGMENT = "james.alarmio.MainActivity.EXTRA_FRAGMENT";
    public static final int FRAGMENT_TIMER = 0;
    public static final int FRAGMENT_STOPWATCH = 2;

    private Alarmio alarmio;
    private AlarmBaseFragment fragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override

        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new MainFragment();
                    break;
                case R.id.navigation_notifications:
                    fragment = new AlarmHomeFragment();
                    break;
                case R.id.navigation_dashboard:
                    fragment = new WaterListFragment();
                    break;
                case R.id.navigation_chart:
                    fragment = new WaterGraphHomeFragment();
                    break;

                case R.id.navigation_person:
                    fragment = new PreferenceSetting();
                    break;
            }
            return loadFragment(fragment);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        alarmio = (Alarmio) getApplicationContext();
        alarmio.setListener(this);

        if (savedInstanceState == null) {
            fragment = new SplashFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        } else {
            if (fragment == null)
                fragment = new AlarmHomeFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new MainFragment());

    }
    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(EXTRA_FRAGMENT)) {
            boolean shouldBackStack = fragment instanceof AlarmHomeFragment;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_up_sheet, R.anim.slide_out_up_sheet, R.anim.slide_in_down_sheet, R.anim.slide_out_down_sheet)
                    .replace(R.id.fragment, fragment);

            if (shouldBackStack)
                transaction.addToBackStack(null);

            transaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alarmio != null)
            alarmio.setListener(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackStackChanged() {
        fragment = (AlarmBaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    public void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, 0);
    }

    @Override
    public FragmentManager gettFragmentManager() {
        return getSupportFragmentManager();
    }
}
