package com.jangletech.qoogol.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogEduListBinding;

public class EducationListDialog extends BottomSheetDialogFragment {

    private static final String TAG = "EducationListDialog";
    private DialogEduListBinding mBinding;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_edu_list, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {

    }
}
