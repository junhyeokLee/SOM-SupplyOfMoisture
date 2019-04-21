package com.junhyeoklee.som.ui.fragment;

import com.junhyeoklee.som.data.alarm.SoundData;
import com.junhyeoklee.som.ui.view.SoundChooserListener;


public abstract class AlarmBaseSoundChooserFragment extends AlarmBasePagerFragment implements SoundChooserListener {

    private SoundChooserListener listener;

    public void setListener(SoundChooserListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSoundChosen(SoundData sound) {
        if (listener != null)
            listener.onSoundChosen(sound);
    }
}
