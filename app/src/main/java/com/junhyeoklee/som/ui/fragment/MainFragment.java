package com.junhyeoklee.som.ui.fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.john.waveview.WaveView;
import com.junhyeoklee.som.AppExecutors;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.factory.AddWaterViewModelFactory;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.view_model.AddWaterDateViewModel;
import com.junhyeoklee.som.util.InjectorUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment {
    @BindView(R.id.btn_Drinking)
    Button btn_Drinking;
    @BindView(R.id.tv_Drink)
    TextView tv_Drink;
    @BindView(R.id.tv_DrinkAmount)
    TextView tv_DrinkAmount;
    @BindView(R.id.tv_Percentage)
    TextView tv_Percentage;
    @BindView(R.id.tv_TotalAmount)
    TextView tv_TotalAmout;
    @BindView(R.id.sb_DrinkBar)
    SeekBar sb_DrinkBar;
    @BindView(R.id.wave_view)
    WaveView waveView;
    //    private static int mTotalWaveValue = 0;
    private final Handler handler = new Handler();
    private int addAnimationWaveValue = 0;

    private Date calendarDate = new Date();
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM");
    private SimpleDateFormat sdfWeek = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("kk시mm분");

    private String getTime = sdf2.format(calendarDate);
    private String getTiemMonth = sdfMonth.format(calendarDate);
    private String getTiemWeek = sdfWeek.format(calendarDate);
    private String getDateTime = sdfTime.format(calendarDate);


    private WaterDatabase mDb;
    public static final String DATE_FORMAT = "yyy/MM/dd";
    private static final String TAG = MainFragment.class.getSimpleName();
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private AddWaterDateViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mDb = WaterDatabase.getInstance(container.getContext());
        setUpView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpView();
    }

    @Override
    public void onPause() {
        super.onPause();
        setUpView();
    }

    private void inintLayout() {

        btn_Drinking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrinkButtonClicked();
            }
        });

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

    }
    public void printSelect(int value) {
        tv_Drink.setText(String.valueOf(value));
    }

    private void setUpView() {

        AddWaterViewModelFactory factory = InjectorUtils.provideWaterViewModelFactory(getContext());
        viewModel = ViewModelProviders.of(this, factory).get(AddWaterDateViewModel.class);
        viewModel.getWater_date(getTime).observe(this, waterEntries -> {
            populateUI(waterEntries);

        });

    }

    private void populateUI(final List<WaterEntry> waters) {


        int TotalDrinkValue = 0;
        String dispPattern = "0";
        SharedPreferences pref = this.getActivity().getSharedPreferences("TotalAmount", MODE_PRIVATE);
        String TotalAmout = pref.getString("totalAmout", "");
        if(TotalAmout == "" || TotalAmout == null){
            TotalAmout = "1500";
        }

        final DecimalFormat form = new DecimalFormat(dispPattern);

        for (int i = 0; i < waters.size(); i++) {
            if(waters != null){
                TotalDrinkValue += waters.get(i).getAmount();
            }
        }
        final double PercentValue = (double) ((double) TotalDrinkValue / Integer.parseInt(TotalAmout)) * 100;
        Log.e(TAG, "DrinkAmountValue =" + "" + TotalDrinkValue);

        tv_DrinkAmount.setText(String.valueOf(TotalDrinkValue));
        tv_TotalAmout.setText(TotalAmout);
        tv_Percentage.setText(form.format(PercentValue));

        final int DrinkAmount = Integer.parseInt(tv_Drink.getText().toString());
        final int DrinkSumAmount = DrinkAmount + Integer.parseInt(tv_DrinkAmount.getText().toString());
        final int TotalAmount = Integer.parseInt(tv_TotalAmout.getText().toString());
//        waveView.setProgress(TotalDrinkValue,TotalAmount);
        int finalTotalDrinkValue = TotalDrinkValue;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (addAnimationWaveValue < finalTotalDrinkValue) {
                    addAnimationWaveValue += 5;
                    SystemClock.sleep(20L);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 100% 넘었을때 100 프로 유지
                            if (Integer.parseInt(form.format(PercentValue).toString()) >= 100) {
                                tv_Percentage.setText("100");
                            } else {
                                tv_Percentage.setText(String.valueOf(form.format(PercentValue)));
                            }

                            waveView.setProgress(addAnimationWaveValue, TotalAmount);
                            waveView.bringToFront();
//                            waveView.setProgress(TotalDrinkValue, Integer.parseInt(TotalAmout));
                        }
                    });
                }
            }
        }).start();

        inintLayout();
    }

    private void onDrinkButtonClicked() {
        final int DrinkAmount = Integer.parseInt(tv_Drink.getText().toString());
        final int DrinkSumAmount = DrinkAmount + Integer.parseInt(tv_DrinkAmount.getText().toString());
        final int TotalAmount = Integer.parseInt(tv_TotalAmout.getText().toString());

        final WaterEntry water = new WaterEntry(DrinkAmount, getTime,getTiemMonth,getTiemWeek,getDateTime);
        Log.e(TAG, "Date Insert = " + " " + getTime);
        Log.e(TAG,"Date Month Insert = " + " "+ getTiemMonth);
        Log.e(TAG,"Date Week Insert = " + " "+ getTiemWeek);
        Log.e(TAG,"Date Time Insert = " + " "+ getDateTime);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(water.getAmount() == 0){
                    return;
                }
                mDb.waterDao().insertWater(water);
            }
        });
    }

}