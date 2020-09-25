package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogQuestionTypeBinding;

public class QuestionTypeDialog extends BottomSheetDialogFragment {

    private Activity mContext;
    private DialogQuestionTypeBinding mBinding;

    public QuestionTypeDialog(@NonNull Activity mContext) {
        this.mContext = mContext;
    }

    public QuestionTypeDialog() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_question_type, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
