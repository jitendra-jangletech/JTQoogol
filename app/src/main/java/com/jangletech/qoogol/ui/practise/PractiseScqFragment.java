package com.jangletech.qoogol.ui.practise;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;

import com.jangletech.qoogol.databinding.PracticeScqBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.UtilHelper;

import static com.jangletech.qoogol.util.Constant.SCQ;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE_WITH_TEXT;

public class PractiseScqFragment extends BaseFragment {

    private static final String TAG = "PractiseScqImageFragmen";
    private SubjectivePractiseViewModel mViewModel;
    private PracticeScqBinding mBinding;
    private CountDownTimer countDownTimer;
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    String scq_ans = "", mcq_ans = "", tfAns = "", scqimg_ans = "", scqimgtext_ans = "", mcqimg_ans = "", mcqimgtext_ans = "";

    public static PractiseScqFragment newInstance(Integer counter) {
        PractiseScqFragment fragment = new PractiseScqFragment();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.practice_scq, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        LearningQuestionsNew learningQuestionsNew = PracticeTestActivity.questionsNewList.get(counter);
        if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ)) {
            mBinding.categoryTextview.setText("SCQ");
            mBinding.scqLayout.setVisibility(View.VISIBLE);
            mBinding.scq1.setText(learningQuestionsNew.getMcq1());
            mBinding.scq2.setText(learningQuestionsNew.getMcq2());
            mBinding.scq3.setText(learningQuestionsNew.getMcq3());
            mBinding.scq4.setText(learningQuestionsNew.getMcq4());
        }
        if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ_IMAGE)) {
            mBinding.categoryTextview.setText("SCQ");
            mBinding.scqImageLayout.setVisibility(View.VISIBLE);
            loadImages(learningQuestionsNew.getMcq1(), mBinding.scq1Img);
            loadImages(learningQuestionsNew.getMcq2(), mBinding.scq2Img);
            loadImages(learningQuestionsNew.getMcq3(), mBinding.scq3Img);
            loadImages(learningQuestionsNew.getMcq4(), mBinding.scq4Img);
        }

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


        mBinding.scq1Layout.setOnClickListener(v -> {
            setSCQAnsIndicator();
            setLayoutBg();
            scq_ans = "A";
            mBinding.scq1Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        mBinding.scq2Layout.setOnClickListener(v -> {
            setLayoutBg();
            setSCQAnsIndicator();
            scq_ans = "B";
            mBinding.scq2Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        mBinding.scq3Layout.setOnClickListener(v -> {
            setLayoutBg();
            setSCQAnsIndicator();
            scq_ans = "C";
            mBinding.scq3Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        mBinding.scq4Layout.setOnClickListener(v -> {
            setLayoutBg();
            setSCQAnsIndicator();
            scq_ans = "D";
            mBinding.scq4Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        mBinding.submit.setOnClickListener(v -> {
            if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(SCQ)) {
                if (!scq_ans.trim().equalsIgnoreCase("")) {
                    setSCQAnsIndicator();
                    if (scq_ans.equalsIgnoreCase(learningQuestionsNew.getAnswer())) {
                        setRightSCQ(scq_ans);
                    } else {
                        setRightSCQ(learningQuestionsNew.getAnswer());
                        setWrongSCQ(scq_ans);
                    }
                    mBinding.solutionLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(requireActivity(), "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                }
            } else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE)) {
                if (!scqimg_ans.trim().equalsIgnoreCase("")) {
                   // setSCQImgAnsIndicator();
                    if (scqimg_ans.equalsIgnoreCase(learningQuestionsNew.getAnswer())) {
                        setRightSCQImg(scqimg_ans);
                    } else {
                        setRightSCQImg(learningQuestionsNew.getAnswer());
                        setWrongSCQImg(scqimg_ans);
                    }
                    mBinding.solutionLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(requireActivity(), "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                }
            } else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT)) {
                if (!scqimgtext_ans.trim().equalsIgnoreCase("")) {
                    //setSCQImgTextAnsIndicator();
                    if (scqimgtext_ans.equalsIgnoreCase(learningQuestionsNew.getAnswer())) {
                        setRightSCQImgText(scqimgtext_ans);
                    } else {
                        setRightSCQImgText(learningQuestionsNew.getAnswer());
                        setWrongSCQImgText(scqimgtext_ans);
                    }
                    mBinding.solutionLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(requireActivity(), "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void setSCQImgTextLayout() {
        mBinding.scqImgtextImg1.setAlpha(255);
        mBinding.scqImgtextImg2.setAlpha(255);
        mBinding.scqImgtextImg3.setAlpha(255);
        mBinding.scqImgtextImg4.setAlpha(255);
    }

    public void setLayoutBg() {
        mBinding.scq1Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_round_order));
        mBinding.scq2Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_round_order));
        mBinding.scq3Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_round_order));
        mBinding.scq4Layout.setBackground(requireActivity().getResources().getDrawable(R.drawable.grey_round_order));
    }

    private void setSCQAnsIndicator() {
        mBinding.scq1Img.setVisibility(View.GONE);
        mBinding.scq2Img.setVisibility(View.GONE);
        mBinding.scq3Img.setVisibility(View.GONE);
        mBinding.scq4Img.setVisibility(View.GONE);
    }

    public void setRightSCQ(String option) {
        switch (option) {
            case "A":
                mBinding.scq1Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right));
                mBinding.scq1Img.setVisibility(View.VISIBLE);
                break;
            case "B":
                mBinding.scq2Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right));
                mBinding.scq2Img.setVisibility(View.VISIBLE);
                break;
            case "C":
                mBinding.scq3Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right));
                mBinding.scq3Img.setVisibility(View.VISIBLE);
                break;
            case "D":
                mBinding.scq4Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right));
                mBinding.scq4Img.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setWrongSCQ(String option) {
        switch (option) {
            case "A":
                mBinding.scq1Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong));
                mBinding.scq1Img.setVisibility(View.VISIBLE);
                break;
            case "B":
                mBinding.scq2Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong));
                mBinding.scq2Img.setVisibility(View.VISIBLE);
                break;
            case "C":
                mBinding.scq3Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong));
                mBinding.scq3Img.setVisibility(View.VISIBLE);
                break;
            case "D":
                mBinding.scq4Img.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong));
                mBinding.scq4Img.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setRightSCQImg(String option) {
        switch (option) {
            case "A":
                mBinding.scqimgChck1.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                mBinding.scqimgChck2.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                mBinding.scqimgChck3.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                mBinding.scqimgChck4.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgChck4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setWrongSCQImg(String option) {
        switch (option) {
            case "A":
                mBinding.scqimgChck1.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                mBinding.scqimgChck2.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                mBinding.scqimgChck3.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                mBinding.scqimgChck4.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgChck4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setRightSCQImgText(String option) {
        switch (option) {
            case "A":
                mBinding.scqimgImgtextChck1.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                mBinding.scqimgImgtextChck2.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                mBinding.scqimgImgtextChck3.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                mBinding.scqimgImgtextChck4.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_right_mtp));
                mBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setWrongSCQImgText(String option) {
        switch (option) {
            case "A":
                mBinding.scqimgImgtextChck1.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                mBinding.scqimgImgtextChck2.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                mBinding.scqimgImgtextChck3.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                mBinding.scqimgImgtextChck4.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.ic_wrong_mtp));
                mBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                break;
        }
    }
}
