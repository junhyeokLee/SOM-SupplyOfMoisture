package com.junhyeoklee.som.ui.dialog;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialog;

public abstract class AestheticDialog extends AppCompatDialog {

    public AestheticDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
