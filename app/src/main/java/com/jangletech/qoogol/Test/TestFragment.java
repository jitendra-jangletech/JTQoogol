package com.jangletech.qoogol.Test;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.CourseActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.LayoutMultiChoiceQuestBinding;
import com.jangletech.qoogol.databinding.LayoutSingleChoiceQuestBinding;
import com.jangletech.qoogol.databinding.MultiChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.SingleChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.TestFragmentBinding;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.TestQuestion;
import com.jangletech.qoogol.ui.BaseFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TestFragment extends BaseFragment implements QueViewClick {

    private TestViewModel mViewModel;
    private TestFragmentBinding testFragmentBinding;
    private SingleChoiceQuestionBinding singleChoiceQuestionBinding;
    private MultiChoiceQuestionBinding multiChoiceQuestionBinding;
    private LayoutMultiChoiceQuestBinding layoutMultiChoiceQuestBinding;
    private LayoutSingleChoiceQuestBinding layoutSingleChoiceQuestBinding;
    private List<TestQuestion> testQuestions;
    private int position;

    public TestFragment(List<TestQuestion> testQuestionList, int pos) {
        this.testQuestions = testQuestionList;
        this.position = pos;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((CourseActivity) getActivity()).setListenerInstance(this);
        if (testQuestions.get(position).getQuestType().equalsIgnoreCase("MCQ")) {
            multiChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.multi_choice_question, container, false);
            return multiChoiceQuestionBinding.getRoot();
        } else {
            singleChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.single_choice_question, container, false);
            return singleChoiceQuestionBinding.getRoot();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        QueViewClick queViewClick = this;

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase("MCQ")) {
                setMcqQuestionLayout();
        }

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase("SCQ")) {
            setScqQuestionLayout();
        }

        /*testFragmentBinding.incorrect.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            ((CourseActivity) getActivity()).setInCorrectView();
            ((CourseActivity) getActivity()).getQuestNo(queViewClick);
        });

        testFragmentBinding.correct.setOnClickListener(v -> {
            ((CourseActivity) getActivity()).setCorrectView();
            ((CourseActivity) getActivity()).getQuestNo(queViewClick);
        });*/
    }

    private void setScqQuestionLayout() {
        setTimer(singleChoiceQuestionBinding.tvQuestTimer);
        //singleChoiceQuestionBinding.
    }

    private void setMcqQuestionLayout() {
        setTimer(multiChoiceQuestionBinding.tvQuestTimer);
    }


    public void setQue(String strQuestTag, int position) {
        if (strQuestTag.equalsIgnoreCase("Correct")) {
            testFragmentBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_correct);
            testFragmentBinding.tvQuestNo.setTag("Correct");
            testFragmentBinding.tvQuestNo.setText("" + (position + 1));
        }
        if (strQuestTag.equalsIgnoreCase("Incorrect")) {
            testFragmentBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_incorrect);
            testFragmentBinding.tvQuestNo.setTag("Incorrect");
            testFragmentBinding.tvQuestNo.setText("" + (position + 1));
        }
        if (strQuestTag.equalsIgnoreCase("Visited")) {
            testFragmentBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_visited);
            testFragmentBinding.tvQuestNo.setTag("Visited");
            testFragmentBinding.tvQuestNo.setText("" + (position + 1));
        }
    }

    @Override
    public void getQueViewClick(String strQuestTag, int position) {
        //setQue(strQuestTag, position);
    }

    @Override
    public void onTabClickClick(int queNo, String strQuestTag, int position) {
        //testFragmentBinding.tvQuestNo.setText("" + queNo);
        //setQue(strQuestTag, position);
    }

    @Override
    public void onTabPositionChange(int position) {
        this.position = position;
    }

    private void setTimer(TextView timer){
        new CountDownTimer(60*1000*20, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText(new SimpleDateFormat("mm:ss").format(new Date( millisUntilFinished)));
            }

            public void onFinish() {
                timer.setText("done!");
            }
        }.start();
    }
}
