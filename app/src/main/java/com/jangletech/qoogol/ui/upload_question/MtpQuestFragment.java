package com.jangletech.qoogol.ui.upload_question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
    private String A1 = "", A2 = "", A3 = "", A4 = "";

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

        mBinding.a1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetRadioGroup(mBinding.a2);
                resetRadioGroup(mBinding.a3);
                resetRadioGroup(mBinding.a4);
                getCheckedRadioButton(mBinding.a1, "A1");

            }
        });

        mBinding.a2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetRadioGroup(mBinding.a3);
                resetRadioGroup(mBinding.a4);
                getCheckedRadioButton(mBinding.a2, "A2");

            }
        });

        mBinding.a3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                resetRadioGroup(mBinding.a4);
                getCheckedRadioButton(mBinding.a3, "A3");
            }
        });

        mBinding.a4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getCheckedRadioButton(mBinding.a4, "A4");
            }
        });
    }

    @Override
    public void onAnswerEntered(String answer) {

    }

    private String getCheckedRadioButton(RadioGroup radioGroup, String strSelected) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (radioButton.isChecked()) {
                if(strSelected.equalsIgnoreCase(radioButton.getText().toString())){

                }
            }
        }
        return "";
    }

    private void setRadioDisable(RadioGroup radioGroup, String strSelected) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            if (strSelected.equalsIgnoreCase(radioButton.getText().toString())) {
                radioButton.setChecked(false);
                radioButton.setEnabled(false);
            } else {
                radioButton.setEnabled(true);
            }
        }
    }

    private void resetRadioGroup(RadioGroup radioGroup) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            radioButton.setEnabled(true);
            radioButton.setChecked(false);
        }
    }
}
