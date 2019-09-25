package com.junhyeoklee.som.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.junhyeoklee.som.R;

public class PreferenceFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

         return view;
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if(view!=null){
//            ViewGroup parent = (ViewGroup)view.getParent();
//            if(parent!=null){
//                parent.removeView(view);
//            }
//        }
//    }
}
