package com.jangletech.qoogol.ui.upload_question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentScqQueBinding;
import com.jangletech.qoogol.model.UploadQuestion;

/**
 * A simple {@link Fragment}5 subclass.
 */
public class SCQ_QueFragment extends Fragment {

    private FragmentScqQueBinding mBinding;
    private UploadQuestion uploadQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scq__que, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            mBinding.questionEdittext.setText(uploadQuestion.getQuestDescription());
            mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
        }



    }
}
