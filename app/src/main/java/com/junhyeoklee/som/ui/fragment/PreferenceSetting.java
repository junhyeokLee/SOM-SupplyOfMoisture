package com.junhyeoklee.som.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.junhyeoklee.som.R;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceSetting extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private View view;
    SharedPreferences prefs;

    EditTextPreference keywordScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.registerOnSharedPreferenceChangeListener(this);

        Preference exercisesPref = findPreference("keyword_screen");
        SharedPreferences pref = this.getActivity().getSharedPreferences("TotalAmount", MODE_PRIVATE);
        String value = pref.getString("totalAmout", "");
        if (value == "" || value == null) {
            exercisesPref.setSummary("1500"+" ml");
        } else {
            exercisesPref.setSummary(value+" ml");
        }
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }


    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("keyword_screen")) {
            Preference exercisesPref = findPreference(s);
            if (exercisesPref.getSummary() == "") {
                exercisesPref.setSummary("목표량을 설정해 주세요.");
            }
            exercisesPref.setSummary(sharedPreferences.getString(s, "")+" ml");
            SharedPreferences prefs = this.getActivity().getSharedPreferences("TotalAmount", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("totalAmout", sharedPreferences.getString(s, ""));
            editor.commit();
        }
    }
}
