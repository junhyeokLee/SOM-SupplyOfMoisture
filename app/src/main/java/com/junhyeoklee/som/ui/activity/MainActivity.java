package com.junhyeoklee.som.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.john.waveview.WaveView;
import com.junhyeoklee.som.AppExecutors;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.MainViewModelFactory;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.view_model.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_Drinking)
    Button btn_Drinking;
    @BindView(R.id.tv_Drink)
    TextView tv_Drink;
    @BindView(R.id.tv_DrinkAmount)
    TextView mTv_DrinkAmount;
    @BindView(R.id.tv_Percentage)
    TextView tv_Percentage;
    @BindView(R.id.tv_TotalAmount)
    TextView tv_TotalAmout;
    @BindView(R.id.sb_DrinkBar)
    SeekBar sb_DrinkBar;
    @BindView(R.id.btn_init)
    Button btn_init;
    @BindView(R.id.wave_view)
    WaveView waveView;

    private WaterDatabase mDb;
    private WaterEntry waterEntry;
    private static final String DATE_FORMAT = "yyy/MM/dd";
    private static final String TAG = MainActivity.class.getSimpleName();
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mDb = WaterDatabase.getInstance(getApplicationContext());
        inintLayout();
        setUpView();
    }



    private void inintLayout(){
        sb_DrinkBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBar.setMax(100);
                printSelect(progress * 10);
//                waterEntry.setWater_progress_ml(progress);
                //   mWater.setWater_progress_ml(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btn_Drinking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrinkButtonClicked();
            }
        });

    }

    public void printSelect(int value){
//        waterEntry.setWater_progress_ml(value);
        tv_Drink.setText(String.valueOf(value));
//          mWater.setWater_progress_ml(value);
//          mWater.setWater_ml(value);

    }

    private void onDrinkButtonClicked(){
        int DrinkAmount = Integer.parseInt(mTv_DrinkAmount.getText().toString());
        int TotalAmount = DrinkAmount + Integer.parseInt(tv_Drink.getText().toString());
        mTv_DrinkAmount.setText(String.valueOf(TotalAmount));
        String date= "";

        final WaterEntry water = new WaterEntry(DrinkAmount,TotalAmount,date);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                    Log.e(TAG, water.getAmount() + " = Insert");
                    mDb.waterDao().insertWater(water);
//                    waveView.setProgress(water.getAmount(), 2000);

//                waveView.setProgress(water.getAmount(), 2000);2000

            }
        });
    }

    private void setUpView() {
        if (mDb != null) {
            MainViewModelFactory factory = new MainViewModelFactory(mDb);
            final MainViewModel viewModel
                    = ViewModelProviders.of(this, factory).get(MainViewModel.class);
            viewModel.getWaters().observe(this, new Observer<List<WaterEntry>>() {
                @Override
                public void onChanged(@Nullable List<WaterEntry> waters) {
                    if (waters != null) {
                        viewModel.getWaters().removeObserver(this);
                        populateUI(waters);
                    }
                }
            });
        }
    }

    private void populateUI(final List<WaterEntry> waters){


        if(waters == null){
            return;
        }

        for(int i=0;i < waters.size(); i++) {
            mTv_DrinkAmount.setText(String.valueOf(waters.get(i).getTotalAmount()));
            tv_Drink.setText(String.valueOf(waters.get(i).getAmount()));
            waveView.setProgress(waters.get(i).getTotalAmount(), 2000);
        }

        // 초기화 - water 전체삭제
        btn_init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.waterDao().deleteAllWaters(waters);
                    }
                });
            }
        });
    }
}