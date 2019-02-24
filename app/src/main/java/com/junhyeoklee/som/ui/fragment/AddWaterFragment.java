package com.junhyeoklee.som.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.junhyeoklee.som.AppExecutors;
import com.junhyeoklee.som.R;
import com.junhyeoklee.som.data.database.WaterDatabase;
import com.junhyeoklee.som.data.model.WaterEntry;
import com.junhyeoklee.som.ui.adapter.AddWaterAdapter;
import com.junhyeoklee.som.ui.view_model.AddWaterViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class AddWaterFragment extends Fragment {

    public static final String TAG = AddWaterFragment.class.getSimpleName();
    private WaterDatabase mDb;

    @BindView(R.id.recyclerViewWaters)
    RecyclerView mRecyclerView;

    private AddWaterAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_drink,container,false);
        ButterKnife.bind(this,view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mAdapter = new AddWaterAdapter(view.getContext());
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(this.getContext(), VERTICAL);
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
        mDb = WaterDatabase.getInstance(this.getContext());
        setupViewModel();

        return view;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupViewModel() {
        AddWaterViewModel viewModel = ViewModelProviders.of(this).get(AddWaterViewModel.class);
        viewModel.getWater().observe(this, new Observer<List<WaterEntry>>() {
            @Override
            public void onChanged(@Nullable List<WaterEntry> waterEntries) {
                Log.d(TAG, "Updating list of waters from LiveData in ViewModel");
                 long now = System.currentTimeMillis();
                 Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat(MainFragment.DATE_FORMAT);
                String getTime = sdf.format(date);

                for(int i = 0 ; i < waterEntries.size() ; i++ ){
                    if(waterEntries.get(i).getUpdateAt().toString().equals(getTime.toString())){
                        mAdapter.setmWaterEntries(waterEntries);
                        Log.d(TAG, "이건 리스트가 생선됬지만 데이트가 안맞는듯?");
                    }
                    else{
//                        mAdapter.setmWaterEntries(waterEntries);
                        Log.d(TAG, "데이트가 같지 않은듯"+""+waterEntries.get(i).getUpdateAt().toString()+""+getTime.toString());
                        return;
                    }
                }
            }
        });
    }

}
