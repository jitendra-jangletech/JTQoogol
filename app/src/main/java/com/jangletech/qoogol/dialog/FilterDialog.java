package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.chip.Chip;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogFilterBinding;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;

import java.util.HashMap;
import java.util.List;

public class FilterDialog extends Dialog implements View.OnClickListener {

    private Activity mContext;
    private List<String> subjectList;
    private FilterClickListener filterClickListener;
    private DialogFilterBinding mBinding;
    private HashMap<Integer, Chip> mapSubjectChips = new HashMap();
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public FilterDialog(@NonNull Activity mContext, List<String> subjectList) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.subjectList = subjectList;
        //this.filterClickListener = filterClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_filter, null, false);
        setContentView(mBinding.getRoot());
        prepareSubjectChips(subjectList);

    }

    private void prepareSubjectChips(List<String> subjectList) {
        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
            chip.setText(subjectList.get(i));
            chip.setTag("Subjects");
            chip.setId(i);
            mapSubjectChips.put(i, chip);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.subjectsChipGrp.addView(chip);
        }
    }

    @Override
    public void onClick(View v) {

    }


    interface FilterClickListener {
        void onFiltersApply(HashMap<String, String> params);
    }
}
