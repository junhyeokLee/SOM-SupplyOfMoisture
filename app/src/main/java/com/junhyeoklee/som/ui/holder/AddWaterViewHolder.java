package com.junhyeoklee.som.ui.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.junhyeoklee.som.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddWaterViewHolder extends RecyclerView.ViewHolder{

    public View mView;

    @BindView(R.id.waterAmount)
    public TextView waterAmountView;
    @BindView(R.id.taskUpdatedAt)
    public TextView updatedAtView;

    public AddWaterViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        ButterKnife.bind(this,itemView);
    }

}
