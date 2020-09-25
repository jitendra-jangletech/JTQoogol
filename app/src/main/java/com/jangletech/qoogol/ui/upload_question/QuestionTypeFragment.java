package com.jangletech.qoogol.ui.upload_question;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogQuestionTypeBinding;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class QuestionTypeFragment extends BaseFragment implements View.OnClickListener {

    private Activity mContext;
    private UploadQuestion uploadQuestion;
    private DialogQuestionTypeBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_question_type, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareQueType();
        if (getArguments() != null && getArguments().getSerializable("Question") != null) {
            uploadQuestion = (UploadQuestion) getArguments().getSerializable("Question");
        }
        mBinding.tvSubject.setText(uploadQuestion.getSubjectName());
        mBinding.questionText.setText(uploadQuestion.getQuestDescription());
        mBinding.btnNext.setOnClickListener(v -> {
            if (uploadQuestion.getQuestionType().equalsIgnoreCase("SCQ")) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Question", uploadQuestion);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scq_question,bundle);
            }
        });
    }

    private void prepareQueType() {
        List que_categoryList = new ArrayList();
        que_categoryList.add(Constant.short_ans);
        que_categoryList.add(Constant.long_ans);
        que_categoryList.add(Constant.scq);
        que_categoryList.add(Constant.mcq);
        que_categoryList.add(Constant.fill_the_blanks);
        que_categoryList.add(Constant.true_false);
        que_categoryList.add(Constant.match_pair);

        mBinding.chipGrpQuestType.removeAllViews();
        for (int i = 0; i < que_categoryList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.chipGrpQuestType.getContext()).inflate(R.layout.chip_new, mBinding.chipGrpQuestType, false);
            chip.setText(que_categoryList.get(i).toString());
            chip.setTag("Question_Type");
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setOnClickListener(this);
            mBinding.chipGrpQuestType.addView(chip);
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            Chip chip = (Chip) v;
            uploadQuestion.setQuestionType(chip.getText().toString());
        }
    }
}
