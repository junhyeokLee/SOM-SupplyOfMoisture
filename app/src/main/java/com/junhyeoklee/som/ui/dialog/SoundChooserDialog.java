package com.junhyeoklee.som.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.material.tabs.TabLayout;
import com.junhyeoklee.som.Alarmio;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.alarm.SoundData;
import com.junhyeoklee.som.ui.adapter.AlarmSimplePagerAdapter;
import com.junhyeoklee.som.ui.fragment.AlarmSoundChooserFragmentAlarm;
import com.junhyeoklee.som.ui.fragment.RadioSoundChooserFragmentAlarm;
import com.junhyeoklee.som.ui.fragment.RingtoneSoundChooserFragmentAlarm;
import com.junhyeoklee.som.ui.view.SoundChooserListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.functions.Consumer;


public class SoundChooserDialog extends DialogFragment implements SoundChooserListener {

    private SoundChooserListener listener;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                if (params != null) {
                    params.windowAnimations = R.style.SlideDialogAnimation;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_sound_chooser, container, false);

        Aesthetic.Companion.get()
                .colorPrimary()
                .take(1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        view.setBackgroundColor(integer);
                    }
                });

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);

        AlarmSoundChooserFragmentAlarm alarmFragment = new AlarmSoundChooserFragmentAlarm();
        RingtoneSoundChooserFragmentAlarm ringtoneFragment = new RingtoneSoundChooserFragmentAlarm();
        RadioSoundChooserFragmentAlarm radioFragment = new RadioSoundChooserFragmentAlarm();

        alarmFragment.setListener(this);
        ringtoneFragment.setListener(this);
        radioFragment.setListener(this);

        viewPager.setAdapter(new AlarmSimplePagerAdapter(getContext(), getChildFragmentManager(), alarmFragment, ringtoneFragment, radioFragment));
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    public void setListener(SoundChooserListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSoundChosen(SoundData sound) {
        if (listener != null)
            listener.onSoundChosen(sound);

        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (view != null)
            ((Alarmio) view.getContext().getApplicationContext()).stopCurrentSound();
    }
}
