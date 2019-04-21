package com.junhyeoklee.som.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.alarm.SoundData;
import com.junhyeoklee.som.ui.adapter.AlarmSoundsAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RingtoneSoundChooserFragmentAlarm extends AlarmBaseSoundChooserFragment {

    private AlarmSoundsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_chooser_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<SoundData> sounds = new ArrayList<>();
        RingtoneManager manager = new RingtoneManager(getContext());
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        int count = cursor.getCount();
        if (count > 0 && cursor.moveToFirst()) {
            do {
                sounds.add(new SoundData(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX), cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX)));
            } while (cursor.moveToNext());
        }

        adapter = new AlarmSoundsAdapter(getAlarmio(), sounds);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_ringtones);
    }

}