package com.junhyeoklee.som.data.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.junhyeoklee.som.R;
import com.junhyeoklee.som.receivers.AlarmReceiver;
import com.junhyeoklee.som.services.SleepReminderService;
import com.junhyeoklee.som.ui.activity.MainActivity;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;

public class AlarmData implements Parcelable {

    private int id;
    public String name;
    public Calendar time;
    public boolean isEnabled = true;
    public boolean[] days = new boolean[7];
    public boolean isVibrate = true;
    public SoundData sound;

    public AlarmData(int id, Calendar time){
    this.id = id;
    this.time = time;
    }

    public AlarmData(int id, Context context){
        this.id = id;
        name = AlarmPreferenceData.ALARM_NAME.getSpecificOverriddenValue(context,getName(context),id);
        time = Calendar.getInstance();
        time.setTimeInMillis((long) AlarmPreferenceData.ALARM_TIME.getSpecificValue(context,id));
        isEnabled = AlarmPreferenceData.ALARM_ENABLED.getSpecificValue(context,id);
        for(int i = 0; i < 7; i++){
            days[i] = AlarmPreferenceData.ALARM_DAY_ENABLED.getSpecificValue(context,id,i);
        }
        isVibrate = AlarmPreferenceData.ALARM_VIBRATE.getSpecificValue(context,id);
        sound = SoundData.fromString(AlarmPreferenceData.ALARM_SOUND.getSpecificOverriddenValue(context, AlarmPreferenceData.DEFAULT_ALARM_RINGTONE.getValue(context, ""), id));

    }

    public void onIdChanged(int id, Context context){
        AlarmPreferenceData.ALARM_NAME.setValue(context,getName(context),id);
        AlarmPreferenceData.ALARM_TIME.setValue(context,time != null ? time.getTimeInMillis() : null, id);
        AlarmPreferenceData.ALARM_ENABLED.setValue(context,isEnabled,id);
        for(int i = 0; i < 7 ; i++){
            AlarmPreferenceData.ALARM_DAY_ENABLED.setValue(context,days[i],id,i);
        }
        AlarmPreferenceData.ALARM_VIBRATE.setValue(context,isVibrate,id);
        AlarmPreferenceData.ALARM_SOUND.setValue(context, sound != null ? sound.toString() : null, id);

        onRemoved(context);
        this.id = id;
        if(isEnabled)
            set(context,(AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
    }

    public void onRemoved(Context context){
        cancel(context,(AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        AlarmPreferenceData.ALARM_NAME.setValue(context,null,id);
        AlarmPreferenceData.ALARM_TIME.setValue(context,null, id);
        AlarmPreferenceData.ALARM_ENABLED.setValue(context,null,id);
        for(int i = 0; i < 7 ; i++){
            AlarmPreferenceData.ALARM_DAY_ENABLED.setValue(context,null,id,i);
        }
        AlarmPreferenceData.ALARM_VIBRATE.setValue(context,null,id);
        AlarmPreferenceData.ALARM_SOUND.setValue(context, null, id);
    }

    public String getName(Context context){
        if(name != null)
            return name;
        else return context.getString(R.string.title_alarm,id+1);
    }

    public boolean isRepeat(){
        for(boolean day : days){
            if(day)
                return true;
        }
        return false;
    }

    public void setName(Context context,String name){
        this.name = name;
        AlarmPreferenceData.ALARM_NAME.setValue(context,name,id);
    }

    public void setTime(Context context,AlarmManager manager, long timeMillis){
        time.setTimeInMillis(timeMillis);
        AlarmPreferenceData.ALARM_TIME.setValue(context,timeMillis,id);
        if(isEnabled)
            set(context,manager);
    }

    public void setEnabled(Context context,AlarmManager manager,boolean isEnabled){
        this.isEnabled = isEnabled;
        AlarmPreferenceData.ALARM_ENABLED.setValue(context,isEnabled,id);
        if(isEnabled)
            set(context,manager);
        else cancel(context,manager);
    }

    public void setDays(Context context,boolean[] days){
        this.days = days;

        for(int i = 0; i < 7; i++){
            AlarmPreferenceData.ALARM_DAY_ENABLED.setValue(context,days[i],id,i);
        }
    }

    public void setVibrate(Context context, boolean isVibrate) {
        this.isVibrate = isVibrate;
        AlarmPreferenceData.ALARM_VIBRATE.setValue(context, isVibrate, id);
    }

    public boolean hasSound() {
        return sound != null;
    }

    @Nullable
    public SoundData getSound() {
        return sound;
    }

    public void setSound(Context context, @Nullable SoundData sound) {
        this.sound = sound;
        AlarmPreferenceData.ALARM_SOUND.setValue(context, sound != null ? sound.toString() : null, id);
    }

    @Nullable
    public Calendar getNext() {
        if (isEnabled) {
            Calendar now = Calendar.getInstance();
            Calendar next = Calendar.getInstance();
            next.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            next.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            next.set(Calendar.SECOND, 0);

            while (now.after(next))
                next.add(Calendar.DATE, 1);

            if (isRepeat()) {
                int nextDay = next.get(Calendar.DAY_OF_WEEK) - 1; // index on 0-6, rather than the 1-7 returned by Calendar

                for (int i = 0; i < 7 && !days[nextDay]; i++) {
                    nextDay++;
                    nextDay %= 7;
                }

                next.set(Calendar.DAY_OF_WEEK, nextDay + 1); // + 1 = back to 1-7 range

                while (now.after(next))
                    next.add(Calendar.DATE, 7);
            }

            return next;
        }

        return null;
    }

    public Date set(Context context, AlarmManager manager) {
        Calendar nextTime = getNext();
        setAlarm(context, manager, nextTime.getTimeInMillis());
        return nextTime.getTime();
    }

    private void setAlarm(Context context, AlarmManager manager, long timeMillis) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(
                            timeMillis,
                            PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0)
                    ),
                    getIntent(context)
            );
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                manager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, getIntent(context));
            else
                manager.set(AlarmManager.RTC_WAKEUP, timeMillis, getIntent(context));

            Intent intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", true);
            context.sendBroadcast(intent);
        }

        manager.set(AlarmManager.RTC_WAKEUP,
                timeMillis - (long) AlarmPreferenceData.SLEEP_REMINDER_TIME.getValue(context),
                PendingIntent.getService(context, 0, new Intent(context, SleepReminderService.class), 0));

        SleepReminderService.refreshSleepTime(context);
    }

    public void cancel(Context context, AlarmManager manager) {
        manager.cancel(getIntent(context));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", false);
            context.sendBroadcast(intent);
        }
    }

    private PendingIntent getIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, id);
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    protected AlarmData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        time = Calendar.getInstance();
        time.setTimeInMillis(in.readLong());
        isEnabled = in.readByte() != 0;
        days = in.createBooleanArray();
        isVibrate = in.readByte() != 0;
        if (in.readByte() == 1)
            sound = SoundData.fromString(in.readString());
    }

    public static final Creator<AlarmData> CREATOR = new Creator<AlarmData>() {
        @Override
        public AlarmData createFromParcel(Parcel in) {
            return new AlarmData(in);
        }

        @Override
        public AlarmData[] newArray(int size) {
            return new AlarmData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeLong(time.getTimeInMillis());
        dest.writeByte((byte) (isEnabled ? 1 : 0));
        dest.writeBooleanArray(days);
        dest.writeByte((byte) (isVibrate ? 1 : 0));
        dest.writeByte((byte) (sound != null ? 1 : 0));
        if (sound != null)
            dest.writeString(sound.toString());
    }
}
