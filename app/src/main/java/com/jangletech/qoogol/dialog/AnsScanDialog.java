package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentScanQuestDescBinding;

public class AnsScanDialog extends Dialog {

    private FragmentScanQuestDescBinding mBinding;
    private AnsScannerListener listener;
    private Context context;
    private int id;

    public AnsScanDialog(@NonNull Context context,int id,AnsScannerListener listener) {
        super(context);
        this.id = id;
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.fragment_scan_quest_desc, null, false);
        setContentView(mBinding.getRoot());

        mBinding.scan.setOnClickListener(v -> {
            dismiss();
            listener.onCamScannerClick(id);
        });

        mBinding.Gallery.setOnClickListener(v -> {
            dismiss();
            listener.onGalleryClick(id);
        });
    }

    public interface AnsScannerListener{
        void onCamScannerClick(int id);
        void onGalleryClick(int id);
    }
}
