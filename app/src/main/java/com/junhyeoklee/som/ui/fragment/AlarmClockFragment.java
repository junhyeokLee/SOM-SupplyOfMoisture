package com.junhyeoklee.som.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junhyeoklee.som.R;
import com.junhyeoklee.som.ui.view.AlarmDigitalClockView;

import java.util.TimeZone;

import androidx.annotation.Nullable;

public class AlarmClockFragment extends AlarmBasePagerFragment {

    public static final String EXTRA_TIME_ZONE = "timezone";

    private AlarmDigitalClockView clockView;
    private TextView timezoneView;

    private String timezone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm_clock, container, false);
        clockView = view.findViewById(R.id.timeView);
        timezoneView = view.findViewById(R.id.timezone);

        if (getArguments() != null && getArguments().containsKey(EXTRA_TIME_ZONE)) {
            timezone = getArguments().getString(EXTRA_TIME_ZONE);
            clockView.setTimezone(timezone);
            if (!timezone.equals(TimeZone.getDefault().getID())) {
                timezoneView.setText(String.format(
                        "%s\n%s",
                        timezone.replaceAll("_", " "),
                        TimeZone.getTimeZone(timezone).getDisplayName()
                ));
            }
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public String getTitle(Context context) {
        return timezone;
    }

}
