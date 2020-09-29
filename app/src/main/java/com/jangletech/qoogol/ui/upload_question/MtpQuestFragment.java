package com.jangletech.qoogol.ui.upload_question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentUpMtpQueBinding;
import com.jangletech.qoogol.dialog.SubjectiveAnsDialog;
import com.jangletech.qoogol.ui.BaseFragment;

public class MtpQuestFragment extends BaseFragment implements SubjectiveAnsDialog.GetAnsListener {

    private static final String TAG = "MtpQuestFragment";
    private FragmentUpMtpQueBinding mBinding;
    private SubjectiveAnsDialog subjectiveAnsDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_up_mtp_que, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.toggleAddQuestDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBinding.etQuestionDesc.setVisibility(View.VISIBLE);
                } else {
                    mBinding.etQuestionDesc.setVisibility(View.GONE);
                }
            }
        });

//        mBinding.etQuestion.setOnClickListener(v -> {
//            mBinding.etQuestion.requestFocus();
//            subjectiveAnsDialog = new SubjectiveAnsDialog(getActivity(), mBinding.etQuestion.getText().toString().trim(), "UP_QUESTION", this);
//        });
//
//        mBinding.etQuestion.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mBinding.etQuestion.clearFocus();
//                    subjectiveAnsDialog.show();
//                }
//            }
//        });
    }

    @Override
    public void onAnswerEntered(String answer) {

    }
}
