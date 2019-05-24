package com.junhyeoklee.som.ui.holder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
    @BindView(R.id.v_step_connector_bottom)
    public View v_step;
    @BindView(R.id.img_up)
    public ImageView img_up;
    @BindView(R.id.img_down)
    public ImageView img_down;

    public AddWaterViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        ButterKnife.bind(this,itemView);
    }

}
