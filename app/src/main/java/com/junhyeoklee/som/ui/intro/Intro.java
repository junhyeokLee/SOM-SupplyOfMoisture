package com.junhyeoklee.som.ui.intro;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.junhyeoklee.som.ui.activity.MainActivity;

public class Intro extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Thread.sleep(1000);
            startActivity(new Intent(Intro.this, MainActivity.class));
            finish();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
