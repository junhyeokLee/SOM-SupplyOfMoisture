package com.junhyeoklee.som;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class WaterApp  extends Application implements HasActivityInjector {

    private static WaterApp instance;

    static {
        instance = null;
    }
    public WaterApp() {
        instance = this;
    }

    public static final Context getAppContext() {
        return instance.getApplicationContext();
    }

    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingAndroidInjector;

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return activityDispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
