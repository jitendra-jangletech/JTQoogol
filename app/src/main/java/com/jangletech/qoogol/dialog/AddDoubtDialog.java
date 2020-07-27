package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.AddDoubtBinding;
import com.jangletech.qoogol.util.PreferenceManager;

/**
 * Created by Pritali on 7/27/2020.
 */
public class AddDoubtDialog  extends Dialog {

    AddDoubtBinding addDoubtBinding;
    private Activity context;
    private PreferenceManager mSettings;

    public AddDoubtDialog(@NonNull Activity context, int questionId) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        addDoubtBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.add_doubt, null, false);
        setContentView(addDoubtBinding.getRoot());
        initView();

    }

    private void initView() {
        mSettings = new PreferenceManager(context);
        addDoubtBinding.titletv.setText("Ask Doubt");
        addDoubtBinding.btnCloseDialog.setOnClickListener(v -> dismiss());
    }
}
