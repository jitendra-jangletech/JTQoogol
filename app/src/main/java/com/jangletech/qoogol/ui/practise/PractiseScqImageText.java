package com.jangletech.qoogol.ui.practise;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.PracticeScqBinding;
import com.jangletech.qoogol.databinding.PracticeScqImageTextBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.UtilHelper;

import java.net.MalformedURLException;
import java.net.URL;

public class PractiseScqImageText extends BaseFragment {

    private static final String TAG = "PractiseScqImageFragmen";
    private PracticeScqImageTextBinding mBinding;
    private CountDownTimer countDownTimer;
    private static final String ARG_COUNT = "param1";
    private Integer counter;

    String scq_ans = "", mcq_ans = "", tfAns = "", scqimg_ans = "", scqimgtext_ans = "", mcqimg_ans = "", mcqimgtext_ans = "";

    public static PractiseScqImageText newInstance(Integer counter) {
        PractiseScqImageText fragment = new PractiseScqImageText();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.practice_scq_image_text, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        LearningQuestionsNew learningQuestionsNew = PracticeTestActivity.questionsNewList.get(counter);

        mBinding.idTextview.setText(learningQuestionsNew.getQuestion_id());
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

        setTimer(mBinding.tvtimer, 0, 0);

        mBinding.categoryTextview.setText("SCQ");
        mBinding.scqImageWithTextLayout.setVisibility(View.VISIBLE);
//        loadImages(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq1().split(":")[0].trim(), mBinding.scqImgtextImg1);
//        loadImages(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq2().split(":")[0].trim(), mBinding.scqImgtextImg2);
//        loadImages(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq3().split(":")[0].trim(), mBinding.scqImgtextImg3);
//        loadImages(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq4().split(":")[0].trim(), mBinding.scqImgtextImg4);

       /* loadImages(getTempImageUrl(learningQuestionsNew.getMcq1()), mBinding.scqImgtextImg1);
        loadImages(getTempImageUrl(learningQuestionsNew.getMcq2()), mBinding.scqImgtextImg2);
        loadImages(getTempImageUrl(learningQuestionsNew.getMcq3()), mBinding.scqImgtextImg3);
        loadImages(getTempImageUrl(learningQuestionsNew.getMcq4()), mBinding.scqImgtextImg4);*/

        try {
            Glide.with(requireActivity()).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq2().split(":")[0].trim())).into(mBinding.scqImgtextImg2);
            Glide.with(requireActivity()).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq3().split(":")[0].trim())).into(mBinding.scqImgtextImg3);
            Glide.with(requireActivity()).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq4().split(":")[0].trim())).into(mBinding.scqImgtextImg4);
            Glide.with(requireActivity()).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestionsNew.getMcq1().split(":")[0].trim())).into(mBinding.scqImgtextImg1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mBinding.scqImgtextText1.setText(learningQuestionsNew.getMcq1().split(":")[1]);
        mBinding.scqImgtextText2.setText(learningQuestionsNew.getMcq2().split(":")[1]);
        mBinding.scqImgtextText3.setText(learningQuestionsNew.getMcq3().split(":")[1]);
        mBinding.scqImgtextText4.setText(learningQuestionsNew.getMcq4().split(":")[1]);

        mBinding.scqImg1.setOnClickListener(v -> {
            showToast("Clicked 1");
            setSCQImgAnsIndicator();
            setSCQImgLayout();
            scqimg_ans = "A";
            mBinding.scqImg1.setAlpha(130);
            mBinding.scqimgChck1.setVisibility(View.VISIBLE);
            mBinding.scqimgChck1.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.selectmark));
        });

        mBinding.scqImg2.setOnClickListener(v -> {
            setSCQImgAnsIndicator();
            setSCQImgLayout();
            scqimg_ans = "B";
            mBinding.scqImg2.setAlpha(130);
            mBinding.scqimgChck2.setVisibility(View.VISIBLE);
            mBinding.scqimgChck2.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.selectmark));
        });

        mBinding.scqImg3.setOnClickListener(v -> {
            setSCQImgAnsIndicator();
            setSCQImgLayout();
            scqimg_ans = "C";
            mBinding.scqImg3.setAlpha(130);
            mBinding.scqimgChck3.setVisibility(View.VISIBLE);
            mBinding.scqimgChck3.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.selectmark));
        });

        mBinding.scqImg4.setOnClickListener(v -> {
            setSCQImgAnsIndicator();
            setSCQImgLayout();
            scqimg_ans = "D";
            mBinding.scqImg4.setAlpha(130);
            mBinding.scqimgChck4.setVisibility(View.VISIBLE);
            mBinding.scqimgChck4.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.selectmark));
        });
    }

    private void setSCQImgLayout() {
        mBinding.scqImg1.setAlpha(255);
        mBinding.scqImg2.setAlpha(255);
        mBinding.scqImg3.setAlpha(255);
        mBinding.scqImg4.setAlpha(255);
    }

    private void setSCQImgTextAnsIndicator() {
        mBinding.scqimgImgtextChck1.setVisibility(View.GONE);
        mBinding.scqimgImgtextChck2.setVisibility(View.GONE);
        mBinding.scqimgImgtextChck3.setVisibility(View.GONE);
        mBinding.scqimgImgtextChck4.setVisibility(View.GONE);
    }

    private void setSCQImgAnsIndicator() {
        mBinding.scqimgChck1.setVisibility(View.GONE);
        mBinding.scqimgChck2.setVisibility(View.GONE);
        mBinding.scqimgChck3.setVisibility(View.GONE);
        mBinding.scqimgChck4.setVisibility(View.GONE);
    }
}
