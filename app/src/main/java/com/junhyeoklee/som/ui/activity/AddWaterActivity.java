package com.junhyeoklee.som.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.junhyeoklee.som.AppExecutors;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.adapter.AddWaterAdapter;
import com.junhyeoklee.som.ui.view_model.AddWaterViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class AddWaterActivity extends AppCompatActivity {

    public static final String TAG = AddWaterActivity.class.getSimpleName();
    private WaterDatabase mDb;

    @BindView(R.id.recyclerViewWaters)
    RecyclerView mRecyclerView;

    private AddWaterAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AddWaterAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<WaterEntry> tasks = mAdapter.getmWaterEntries();
                        mDb.waterDao().deleteWater(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);
        mDb = WaterDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        AddWaterViewModel viewModel = ViewModelProviders.of(this).get(AddWaterViewModel.class);
        viewModel.getWater().observe(this, new Observer<List<WaterEntry>>() {
            @Override
            public void onChanged(@Nullable List<WaterEntry> waterEntries) {
                Log.d(TAG, "Updating list of waters from LiveData in ViewModel");
                mAdapter.setmWaterEntries(waterEntries);
            }
        });
    }

}
