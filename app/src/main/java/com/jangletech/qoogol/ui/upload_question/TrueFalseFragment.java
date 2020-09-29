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
import com.jangletech.qoogol.databinding.FragmentUpTrueFalseBinding;
import com.jangletech.qoogol.ui.BaseFragment;

public class TrueFalseFragment extends BaseFragment{

    private static final String TAG = "TrueFalseFragment";
    private FragmentUpTrueFalseBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_up_true_false, container,false);
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
    }
}
