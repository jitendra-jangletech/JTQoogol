package com.jangletech.qoogol.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.databinding.MultiChoiceQuestionBinding;
import com.jangletech.qoogol.dialog.QuestReportDialog;
import com.jangletech.qoogol.model.TestQuestionNew;

public class McqFragment extends BaseFragment implements QuestReportDialog.QuestReportDialogListener {

    private static final String TAG = "McqFragment";
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private MultiChoiceQuestionBinding mBinding;
    private QuestReportDialog questReportDialog;

    public static McqFragment newInstance(Integer counter) {
        McqFragment fragment = new McqFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.multi_choice_question, container, false);
        initView(mBinding);
        return mBinding.getRoot();
    }

    private void initView(MultiChoiceQuestionBinding mBinding) {
        TestQuestionNew testQuestionNew = StartTestActivity.testQuestionList.get(counter);
        mBinding.tvQuestNo.setText(testQuestionNew.getTq_quest_seq_num());
        mBinding.tvQuestion.setText(testQuestionNew.getQ_quest());
        setTimer(mBinding.tvQuestTimer, 0, 0);
        mBinding.ans1.setText(testQuestionNew.getQ_mcq_op_1());
        mBinding.ans2.setText(testQuestionNew.getQ_mcq_op_2());
        mBinding.ans3.setText(testQuestionNew.getQ_mcq_op_3());
        mBinding.ans4.setText(testQuestionNew.getQ_mcq_op_4());
        mBinding.ans5.setText(testQuestionNew.getQ_mcq_op_5());

        if (mBinding.ans5.getText().toString().isEmpty()) {
            mBinding.ans5.setVisibility(View.GONE);
        }

        if (testQuestionNew.isTtqa_marked()) {
            mBinding.imgSave.setChecked(true);
        } else {
            mBinding.imgSave.setChecked(false);
        }

        mBinding.imgSave.setOnClickListener(v -> {
            if (mBinding.imgSave.isChecked()) {
                testQuestionNew.setTtqa_marked(true);
            } else {
                testQuestionNew.setTtqa_marked(false);
            }
        });

        mBinding.imgReport.setOnClickListener(v -> {
            QuestReportDialog questReportDialog = new QuestReportDialog(getContext(), this, counter);
            questReportDialog.show();
        });

        mBinding.btnNext.setOnClickListener(v -> {
            StartTestActivity.viewPager.setCurrentItem(StartTestActivity.viewPager.getCurrentItem() + 1, true);
        });

        mBinding.btnPrevious.setOnClickListener(v -> {
            StartTestActivity.viewPager.setCurrentItem(StartTestActivity.viewPager.getCurrentItem() - 1, true);
        });

        mBinding.btnReset.setOnClickListener(v -> {
            testQuestionNew.setTtqa_visited(true);
            testQuestionNew.setTtqa_marked(false);
            testQuestionNew.setTtqa_attempted(false);
            if (mBinding.imgSave.isChecked()) {
                mBinding.imgSave.setChecked(false);
            }

            ChipGroup chipGroup = mBinding.multiChoiceAnswerGrp;
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                chip.setChecked(false);
            }
        });

        //Ans Selection
        mBinding.ans1.setOnClickListener(v -> {
            //checkSetAns(testQuestionNew);
            if(mBinding.ans1.isChecked()){
                testQuestionNew.setTtqa_mcq_ans_1(true);
            }else{
                testQuestionNew.setTtqa_mcq_ans_1(false);
            }
            attemptedOrNot(mBinding.multiChoiceAnswerGrp,testQuestionNew);
        });

        mBinding.ans2.setOnClickListener(v -> {
            //checkSetAns(testQuestionNew);
            if(mBinding.ans2.isChecked()){
                testQuestionNew.setTtqa_mcq_ans_2(true);
            }else{
                testQuestionNew.setTtqa_mcq_ans_2(false);
            }
            attemptedOrNot(mBinding.multiChoiceAnswerGrp,testQuestionNew);
        });

        mBinding.ans3.setOnClickListener(v -> {
            //checkSetAns(testQuestionNew);
            if(mBinding.ans3.isChecked()){
                testQuestionNew.setTtqa_mcq_ans_3(true);
            }else{
                testQuestionNew.setTtqa_mcq_ans_3(false);
            }
            attemptedOrNot(mBinding.multiChoiceAnswerGrp,testQuestionNew);
        });

        mBinding.ans4.setOnClickListener(v -> {
            //checkSetAns(testQuestionNew);
            if(mBinding.ans4.isChecked()){
                testQuestionNew.setTtqa_mcq_ans_4(true);
            }else{
                testQuestionNew.setTtqa_mcq_ans_4(false);
            }
            attemptedOrNot(mBinding.multiChoiceAnswerGrp,testQuestionNew);
        });

        mBinding.ans5.setOnClickListener(v -> {
            //checkSetAns(testQuestionNew);
            if(mBinding.ans5.isChecked()){
                testQuestionNew.setTtqa_mcq_ans_5(true);
            }else{
                testQuestionNew.setTtqa_mcq_ans_5(false);
            }
            attemptedOrNot(mBinding.multiChoiceAnswerGrp,testQuestionNew);
        });
    }

    @Override
    public void onReportQuestSubmitClick(int pos) {
        showToast("Question No : " + StartTestActivity.testQuestionList.get(pos).getTq_quest_seq_num() + " Reported successfully.");
        questReportDialog.dismiss();
    }

    private void attemptedOrNot(ChipGroup chipGroup,TestQuestionNew testQuestionNew){
        boolean isAttempted = false;
        testQuestionNew.setTtqa_attempted(false);
        for (int i = 0; i <chipGroup.getChildCount() ; i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if(chip.isChecked()){
                isAttempted = true;
                Log.d(TAG, "attemptedOrNot: "+i);
                testQuestionNew.setTtqa_attempted(true);
                break;
            }
        }
    }
}
