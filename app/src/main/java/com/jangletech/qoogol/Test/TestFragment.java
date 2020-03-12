package com.jangletech.qoogol.Test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import com.jangletech.qoogol.CourseActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestFragmentBinding;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.ui.learning.course.CourseFragment;

public class TestFragment extends CourseFragment implements QueViewClick {

    private TestViewModel mViewModel;
    private TestFragmentBinding testFragmentBinding;

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((CourseActivity) getActivity()).setListenerInstance(this);
        testFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.test_fragment, container, false);
        return testFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);

        QueViewClick queViewClick = this;
        testFragmentBinding.incorrect.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            ((CourseActivity) getActivity()).setInCorrectView();
            ((CourseActivity) getActivity()).getQuestNo(queViewClick);
        });

        testFragmentBinding.correct.setOnClickListener(v -> {
            ((CourseActivity) getActivity()).setCorrectView();
            ((CourseActivity) getActivity()).getQuestNo(queViewClick);
        });
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
        setQue(strQuestTag, position);
    }

    @Override
    public void onTabClickClick(int queNo, String strQuestTag, int position) {
        testFragmentBinding.tvQuestNo.setText("" + queNo);
        setQue(strQuestTag, position);
    }
}
