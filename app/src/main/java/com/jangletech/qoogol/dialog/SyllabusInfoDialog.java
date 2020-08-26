package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogSyllabusInfoBinding;

public class SyllabusInfoDialog extends Dialog {

    private DialogSyllabusInfoBinding mBinding;

    public SyllabusInfoDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_syllabus_info, null, false);
        setContentView(mBinding.getRoot());
    }
}
