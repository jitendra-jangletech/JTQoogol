package com.jangletech.qoogol.ui.upload_question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmenUpScqImageBinding;
import com.jangletech.qoogol.databinding.FragmentScqQueBinding;
import com.jangletech.qoogol.dialog.AddImageDialog;
import com.jangletech.qoogol.dialog.AnsScanDialog;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;

public class ScqUpImageFragment extends BaseFragment {

    private static final String TAG = "ScqUpImageFragment";
    private UploadQuestion uploadQuestion;
    private FragmenUpScqImageBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragmen_up_scq_image, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
            mBinding.etQuestion.setText(uploadQuestion.getQuestDescription());
            mBinding.subject.setText("Subject : " + uploadQuestion.getSubjectName());
        }

        mBinding.image1.setOnClickListener(v -> {
            new AddImageDialog(getActivity())
                    .show();
        });
    }
}
