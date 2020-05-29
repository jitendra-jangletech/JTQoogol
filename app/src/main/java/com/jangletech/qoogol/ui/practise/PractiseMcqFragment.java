package com.jangletech.qoogol.ui.practise;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.PracticeMcqBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.TestQuestion;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.UtilHelper;


public class PractiseMcqFragment extends BaseFragment {

    private static final String TAG = "PractiseMcqFragment";
    private PracticeMcqBinding mBinding;
    private CountDownTimer countDownTimer;
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    String scq_ans = "", mcq_ans = "", tfAns = "", scqimg_ans = "", scqimgtext_ans = "", mcqimg_ans = "", mcqimgtext_ans = "";

    public static PractiseMcqFragment newInstance(Integer counter) {
        PractiseMcqFragment fragment = new PractiseMcqFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.practice_mcq, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        TestQuestionNew learningQuestionsNew = PracticeTestActivity.questionsNewList.get(counter);
        /*mBinding.idTextview.setText(learningQuestionsNew.getQuestion_id());
        mBinding.timeTextview.setText("Time: " + learningQuestionsNew.getRecommended_time() + " Sec");
        mBinding.difflevelValue.setText(learningQuestionsNew.getDifficulty_level());
        mBinding.likeValue.setText(learningQuestionsNew.getLikes());
        mBinding.commentValue.setText(learningQuestionsNew.getComments());
        mBinding.shareValue.setText(learningQuestionsNew.getShares());
        mBinding.subjectTextview.setText(learningQuestionsNew.getSubject());
        mBinding.marksTextview.setText("Marks : " + UtilHelper.formatMarks(Float.parseFloat(learningQuestionsNew.getMarks())));

        mBinding.chapterTextview.setText(learningQuestionsNew.getChapter());
        mBinding.topicTextview.setText(learningQuestionsNew.getTopic());
        mBinding.postedValue.setText(learningQuestionsNew.getPosted_on() != null ? learningQuestionsNew.getPosted_on().substring(0, 10) : "");
        mBinding.lastUsedValue.setText(learningQuestionsNew.getLastused_on() != null ? learningQuestionsNew.getLastused_on().substring(0, 10) : "");
        mBinding.tvQuestion.setText(learningQuestionsNew.getQuestion());
        mBinding.tvMathQuestion.setText(learningQuestionsNew.getQuestiondesc());
        mBinding.attemptedValue.setText(learningQuestionsNew.getAttended_by() != null ? learningQuestionsNew.getAttended_by() : "0");
        mBinding.ratingvalue.setText(learningQuestionsNew.getRating());
        mBinding.solutionOption.setText("Answer : " + learningQuestionsNew.getAnswer());
        mBinding.solutionDesc.setText(learningQuestionsNew.getAnswerDesc());
        setTimer(mBinding.tvtimer, 0, 0);*/
        mBinding.categoryTextview.setText("MCQ");

    }
}
