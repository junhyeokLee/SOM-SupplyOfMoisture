package com.junhyeoklee.som.data.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.junhyeoklee.som.receivers.TimerReceiver;

import androidx.annotation.Nullable;

public class TimerData implements Parcelable {

    private int id;
    private long duration = 600000;
    private long endTime;
    public boolean isVibrate = true;

    public TimerData(int id) {
        this.id = id;
    }

    public TimerData(int id, Context context) {
        this.id = id;
        try {
            duration = AlarmPreferenceData.TIMER_DURATION.getSpecificValue(context, id);
        } catch (ClassCastException e) {
            duration = (int) AlarmPreferenceData.TIMER_DURATION.getSpecificValue(context, id);
        }
        try {
            endTime = AlarmPreferenceData.TIMER_END_TIME.getSpecificValue(context, id);
        } catch (ClassCastException e) {
            endTime = (int) AlarmPreferenceData.TIMER_END_TIME.getSpecificValue(context, id);
        }

        isVibrate = AlarmPreferenceData.TIMER_VIBRATE.getSpecificValue(context, id);

    }

    /**
     * Moves this TimerData's preferences to another "id".
     *
     * @param id            The new id to be assigned
     * @param context       An active context instance.
     */
    public void onIdChanged(int id, Context context) {
        AlarmPreferenceData.TIMER_DURATION.setValue(context, duration, id);
        AlarmPreferenceData.TIMER_END_TIME.setValue(context, endTime, id);
        AlarmPreferenceData.TIMER_VIBRATE.setValue(context, isVibrate, id);
        onRemoved(context);
        this.id = id;
        if (isSet())
            set(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
    }

    /**
     * Removes this TimerData's preferences.
     *
     * @param context       An active context instance.
     */
    public void onRemoved(Context context) {
        cancel(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        AlarmPreferenceData.TIMER_DURATION.setValue(context, null, id);
        AlarmPreferenceData.TIMER_END_TIME.setValue(context, null, id);
        AlarmPreferenceData.TIMER_VIBRATE.setValue(context, null, id);
        AlarmPreferenceData.TIMER_SOUND.setValue(context, null, id);
    }

    /**
     * Decides if the Timer has been set or should be ignored.
     *
     * @return              True if the timer should go off at some time in the future.
     */
    public boolean isSet() {
        return endTime > System.currentTimeMillis();
    }

    /**
     * Get the remaining amount of milliseconds before the timer should go off. This
     * may return a negative number.
     *
     * @return              The amount of milliseconds before the timer should go off.
     */
    public long getRemainingMillis() {
        return Math.max(endTime - System.currentTimeMillis(), 0);
    }

    /**
     * The total length of the timer.
     *
     * @return              The total length of the timer, in milliseconds.
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Set the duration of the timer.
     *
     * @param duration      The total length of the timer, in milliseconds.
     * @param context       An active Context instance.
     */
    public void setDuration(long duration, Context context) {
        this.duration = duration;
        AlarmPreferenceData.TIMER_DURATION.setValue(context, duration, id);
    }

    /**
     * Set whether the timer should vibrate when it goes off.
     *
     * @param context       An active Context instance.
     * @param isVibrate     Whether the timer should vibrate.
     */
    public void setVibrate(Context context, boolean isVibrate) {
        this.isVibrate = isVibrate;
        AlarmPreferenceData.TIMER_VIBRATE.setValue(context, isVibrate, id);
    }

    /**
     * Return whether the timer has a sound or not.
     *
     * @return              A boolean defining whether a sound has been set
     *                      for the timer.
     */


    /**
     * Get the [SoundData](./SoundData) sound specified for the timer.
     *
     * @return              An instance of SoundData describing the sound that
     *                      the timer should make (or null).
     */


    /**
     * Set the sound that the timer should make.
     *
     * @param context       An active context instance.
     * @param sound         A [SoundData](./SoundData) defining the sound that
     *                      the timer should make.
     */


    /**
     * Set the next time for the timer to ring.
     *
     * @param context       An active context instance.
     * @param manager       The AlarmManager to schedule the timer on.
     */
    public void set(Context context, AlarmManager manager) {
        endTime = System.currentTimeMillis() + duration;
        setAlarm(context, manager);

        AlarmPreferenceData.TIMER_END_TIME.setValue(context, endTime, id);
    }

    /**
     * Schedule a time for the alert to ring at.
     *
     * @param context       An active context instance.
     * @param manager       The AlarmManager to schedule the alert on.
     */
    public void setAlarm(Context context, AlarmManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            manager.setExact(AlarmManager.RTC_WAKEUP, endTime, getIntent(context));
        else manager.set(AlarmManager.RTC_WAKEUP, endTime, getIntent(context));
    }

    /**
     * Cancel the pending alert.
     *
     * @param context       An active context instance.
     * @param manager       The AlarmManager that the alert was scheduled on.
     */
    public void cancel(Context context, AlarmManager manager) {
        endTime = 0;
        manager.cancel(getIntent(context));

        AlarmPreferenceData.TIMER_END_TIME.setValue(context, endTime, id);
    }

    /**
     * The intent to fire when the alert should ring.
     *
     * @param context       An active context instance.
     * @return              A PendingIntent that will open the alert screen.
     */
    private PendingIntent getIntent(Context context) {
        Intent intent = new Intent(context, TimerReceiver.class);
        intent.putExtra(TimerReceiver.EXTRA_TIMER_ID, id);
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeLong(duration);
        parcel.writeLong(endTime);
        parcel.writeByte((byte) (isVibrate ? 1 : 0));


    }

    protected TimerData(Parcel in) {
        id = in.readInt();
        duration = in.readLong();
        endTime = in.readLong();
        isVibrate = in.readByte() != 0;

    }

    public static final Creator<TimerData> CREATOR = new Creator<TimerData>() {
        @Override
        public TimerData createFromParcel(Parcel in) {
            return new TimerData(in);
        }

        @Override
        public TimerData[] newArray(int size) {
            return new TimerData[size];
        }
    };
}
