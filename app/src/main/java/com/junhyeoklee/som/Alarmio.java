package com.junhyeoklee.som;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;


import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AutoSwitchMode;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.junhyeoklee.som.data.alarm.AlarmData;
import com.junhyeoklee.som.data.alarm.AlarmPreferenceData;
import com.junhyeoklee.som.services.SleepReminderService;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

public class Alarmio extends Application implements Player.EventListener {

    public static final int THEME_DAY_NIGHT = 0;
    public static final int THEME_DAY = 1;
    public static final int THEME_NIGHT = 2;
    public static final int THEME_AMOLED = 3;

    public static final String NOTIFICATION_CHANNEL_STOPWATCH = "stopwatch";

    private SharedPreferences prefs;
    private SunriseSunsetCalculator sunsetCalculator;

    private List<AlarmData> alarms;

    private List<AlarmioListener> listeners;
    private ActivityListener listener;

    private String currentStream;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listeners = new ArrayList<>();
        alarms = new ArrayList<>();


        int alarmLength = AlarmPreferenceData.ALARM_LENGTH.getValue(this);
        for (int id = 0; id < alarmLength; id++) {
            alarms.add(new AlarmData(id, this));
        }

        SleepReminderService.refreshSleepTime(this);
    }

    public List<AlarmData> getAlarms() {
        return alarms;
    }

    /**
     * Create a new alarm, assigning it an unused preference id.
     *
     * @return          The newly instantiated [AlarmData](./data/AlarmData).
     */
    public AlarmData newAlarm() {
        AlarmData alarm = new AlarmData(alarms.size(), Calendar.getInstance());
        alarms.add(alarm);
        onAlarmCountChanged();
        return alarm;
    }

    /**
     * Remove an alarm and all of its its preferences.
     *
     * @param alarm     The alarm to be removed.
     */
    public void removeAlarm(AlarmData alarm) {
        alarm.onRemoved(this);

        int index = alarms.indexOf(alarm);
        alarms.remove(index);
        for (int i = index; i < alarms.size(); i++) {
            alarms.get(i).onIdChanged(i, this);
        }

        onAlarmCountChanged();
        onAlarmsChanged();
    }

    /**
     * Update preferences to show that the alarm count has been changed.
     */
    public void onAlarmCountChanged() {
        AlarmPreferenceData.ALARM_LENGTH.setValue(this, alarms.size());
    }

    /**
     * Notify the application of changes to the current alarms.
     */
    public void onAlarmsChanged() {
        for (AlarmioListener listener : listeners) {
            listener.onAlarmsChanged();
        }
    }

    /**
     * Notify the application of changes to the current timers.
     */
    public void onTimersChanged() {
        for (AlarmioListener listener : listeners) {
            listener.onTimersChanged();
        }
    }
    /**
     * Get an instance of SharedPreferences.
     *
     * @return          The instance of SharedPreferences being used by the application.
     * @see [android.content.SharedPreferences Documentation](https://developer.android.com/reference/android/content/SharedPreferences)
     */
    public SharedPreferences getPrefs() {
        return prefs;
    }


    /**
     * Determine if the sunrise/sunset stuff should occur automatically.
     *
     * @return          True if the day/night stuff is automated.
     */
    public boolean isDayAuto() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && (boolean) AlarmPreferenceData.DAY_AUTO.getValue(this);
    }
    //
//    /**
//     * @return the hour of the start of the day (24h), as specified by the user
//     */
    public int getDayStart() {
        if (isDayAuto() && getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunriseCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return AlarmPreferenceData.DAY_START.getValue(this);
    }
    //
//    /**
//     * @return the hour of the end of the day (24h), as specified by the user
//     */
    public int getDayEnd() {
        if (isDayAuto() && getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunsetCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return AlarmPreferenceData.DAY_END.getValue(this);
    }
    //
//    /**
//     * @return the hour of the calculated sunrise time, or null.
//     */
    @Nullable
    public Integer getSunrise() {
        if (getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunsetCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return null;
    }
    //
//    /**
//     * @return the hour of the calculated sunset time, or null.
//     */
    @Nullable
    public Integer getSunset() {
        if (getSunsetCalculator() != null)
            return getSunsetCalculator().getOfficialSunsetCalendarForDate(Calendar.getInstance()).get(Calendar.HOUR_OF_DAY);
        else return null;
    }

    /**
     * @return the current SunriseSunsetCalculator object, or null if it cannot
     *         be instantiated.
     * @see [SunriseSunsetLib Repo](https://github.com/mikereedell/sunrisesunsetlib-java)
     */
    @Nullable
    private SunriseSunsetCalculator getSunsetCalculator() {
        if (sunsetCalculator == null && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                android.location.Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
                sunsetCalculator = new SunriseSunsetCalculator(new Location(location.getLatitude(), location.getLongitude()), TimeZone.getDefault().getID());
            } catch (NullPointerException ignored) {
            }
        }

        return sunsetCalculator;
    }


    public void addListener(AlarmioListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AlarmioListener listener) {
        listeners.remove(listener);
    }

    public void setListener(ActivityListener listener) {
        this.listener = listener;

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
            case Player.STATE_READY:
                break;
            default:
                currentStream = null;
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        currentStream = null;
        Exception exception;
        switch (error.type) {
            case ExoPlaybackException.TYPE_RENDERER:
                exception = error.getRendererException();
                break;
            case ExoPlaybackException.TYPE_SOURCE:
                exception = error.getSourceException();
                break;
            case ExoPlaybackException.TYPE_UNEXPECTED:
                exception = error.getUnexpectedException();
                break;
            default:
                return;
        }

        exception.printStackTrace();
        Toast.makeText(this, exception.getClass().getName() + ": " + exception.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }

    public void requestPermissions(String... permissions) {
        if (listener != null)
            listener.requestPermissions(permissions);
    }

    public FragmentManager getFragmentManager() {
        if (listener != null)
            return listener.gettFragmentManager();
        else return null;
    }

    public interface AlarmioListener {
        void onAlarmsChanged();

        void onTimersChanged();
    }

    public interface ActivityListener {
        void requestPermissions(String... permissions);

        FragmentManager gettFragmentManager(); //help
    }

}
