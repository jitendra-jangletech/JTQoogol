package com.jangletech.qoogol.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.Test.TestViewModel;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.databinding.FillInTheBlanksLayoutBinding;
import com.jangletech.qoogol.databinding.MultiChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.MultiLineQuestAnsBinding;
import com.jangletech.qoogol.databinding.SingleChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.TestFragmentBinding;
import com.jangletech.qoogol.databinding.TestQuestionFragmentBinding;
import com.jangletech.qoogol.databinding.TrueFalseLayoutBinding;
import com.jangletech.qoogol.dialog.QuestReportDialog;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.util.Constant;

import java.util.HashMap;
import java.util.List;

public class TestQuestionFragment extends BaseFragment implements QueViewClick, QuestReportDialog.QuestReportDialogListener {

    private static final String TAG = "TestFragment";
    private static final String ARG_COUNT = "param1";
    private TestViewModel mViewModel;
    private TestFragmentBinding testFragmentBinding;
    private SingleChoiceQuestionBinding singleChoiceQuestionBinding;
    private MultiChoiceQuestionBinding multiChoiceQuestionBinding;
    private FillInTheBlanksLayoutBinding fillInTheBlanksBinding;
    private TrueFalseLayoutBinding trueFalseLayoutBinding;
    private MultiLineQuestAnsBinding multiLineQuestAnsBinding;
    private TestQuestionFragmentBinding testQuestionFragmentBinding;
    private List<TestQuestionNew> testQuestions;
    QuestReportDialog questReportDialog;
    private int position;
    HashMap<Integer, Chip> mapScqQuest = new HashMap();
    HashMap<Integer, Chip> mapMcqQuest = new HashMap();
    HashMap<Integer, ImageView> markedQuestMap = new HashMap<>();


    public TestQuestionFragment() {
        this.testQuestions = StartTestActivity.testQuestionList;
    }

    public static TestQuestionFragment newInstance(Integer counter) {
        TestQuestionFragment fragment = new TestQuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

       /* if (testQuestions.get(position).getQ_type().equals(Constant.SCQ)) {
            singleChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.single_choice_question, container, false);
            setScqQuestionLayout(singleChoiceQuestionBinding, position);
            return singleChoiceQuestionBinding.getRoot();
        }

        if (testQuestions.get(position).getQ_type().equals(Constant.MCQ)) {
            multiChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.multi_choice_question, container, false);
            setMcqQuestionLayout(multiChoiceQuestionBinding, position);
            return multiChoiceQuestionBinding.getRoot();
        }

        if (testQuestions.get(position).getQ_type().equals(Constant.SHORT_ANSWER)) {
            multiLineQuestAnsBinding = DataBindingUtil.inflate(inflater, R.layout.multi_line_quest_ans, container, false);
            setMultiLineQuestAnsLayout(multiLineQuestAnsBinding, position);
            return multiLineQuestAnsBinding.getRoot();
        }

        if (testQuestions.get(position).getQ_type().equals(Constant.LONG_ANSWER)) {
            multiLineQuestAnsBinding = DataBindingUtil.inflate(inflater, R.layout.multi_line_quest_ans, container, false);
            setMultiLineQuestAnsLayout(multiLineQuestAnsBinding, position);
            return multiLineQuestAnsBinding.getRoot();
        }

        if (testQuestions.get(position).getQ_type().equals(Constant.Fill_THE_BLANKS)) {
            fillInTheBlanksBinding = DataBindingUtil.inflate(inflater, R.layout.fill_in_the_blanks_layout, container, false);
            setFillTheBlanksLayout(fillInTheBlanksBinding, position);
            return fillInTheBlanksBinding.getRoot();
        }

        if (testQuestions.get(position).getQ_type().equals(Constant.Fill_THE_BLANKS)) {
            fillInTheBlanksBinding = DataBindingUtil.inflate(inflater, R.layout.fill_in_the_blanks_layout, container, false);
            setFillTheBlanksLayout(fillInTheBlanksBinding, position);
            return fillInTheBlanksBinding.getRoot();
        }*/

        testQuestionFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.test_question_fragment, container, false);
        return setCommonLayout(testQuestionFragmentBinding);
        //return testQuestionFragmentBinding.getRoot();

        //return null;
    }

    private View setCommonLayout(TestQuestionFragmentBinding testQuestionFragmentBinding) {
        TestQuestionNew questionNew = testQuestions.get(position);

        testQuestionFragmentBinding.tvQuestNo.setText(questionNew.getTq_quest_seq_num());
        if (questionNew.isTtqa_marked()) {
            testQuestionFragmentBinding.imgSave.setChecked(true);
        }

        if (questionNew.getQ_type().equals(Constant.SCQ)) {
            testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceQuestRootLayout.setVisibility(View.VISIBLE);
            testQuestionFragmentBinding.singleChoiceQuestion.tvQuestion.setText(questionNew.getQ_quest());

            testQuestionFragmentBinding.singleChoiceQuestion.ans1.setText(questionNew.getQ_mcq_op_1());
            testQuestionFragmentBinding.singleChoiceQuestion.ans2.setText(questionNew.getQ_mcq_op_2());
            testQuestionFragmentBinding.singleChoiceQuestion.ans3.setText(questionNew.getQ_mcq_op_3());
            testQuestionFragmentBinding.singleChoiceQuestion.ans4.setText(questionNew.getQ_mcq_op_4());
            testQuestionFragmentBinding.singleChoiceQuestion.ans5.setText(questionNew.getQ_mcq_op_5());

            setTimer(testQuestionFragmentBinding.tvQuestTimer, 0, 0);
        }

        if (questionNew.getQ_type().equals(Constant.SHORT_ANSWER)) {
            testQuestionFragmentBinding.shortAnswer.shortAnswerRootLayout.setVisibility(View.VISIBLE);
            testQuestionFragmentBinding.shortAnswer.tvQuestion.setText(questionNew.getQ_quest());
            setTimer(testQuestionFragmentBinding.tvQuestTimer, 0, 0);
            testQuestionFragmentBinding.shortAnswer.etMultiLineAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));
            testQuestionFragmentBinding.shortAnswer.etMultiLineAnswer.setText(questionNew.getTtqa_sub_ans());
            answerCharCounter(testQuestionFragmentBinding.shortAnswer.etMultiLineAnswer, testQuestionFragmentBinding.shortAnswer.tvCharCounter, 50);
        }

        if (questionNew.getQ_type().equals(Constant.LONG_ANSWER)) {
            testQuestionFragmentBinding.longAnswer.multiLineQuestionRootLayout.setVisibility(View.VISIBLE);
            testQuestionFragmentBinding.longAnswer.tvQuestion.setText(questionNew.getQ_quest());
            setTimer(testQuestionFragmentBinding.tvQuestTimer, 0, 0);
            testQuestionFragmentBinding.longAnswer.etMultiLineAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));
            testQuestionFragmentBinding.longAnswer.etMultiLineAnswer.setText(questionNew.getTtqa_sub_ans());
            answerCharCounter(testQuestionFragmentBinding.longAnswer.etMultiLineAnswer, testQuestionFragmentBinding.longAnswer.tvCharCounter, 200);
        }

        if (questionNew.getQ_type().equals(Constant.MCQ)) {
            testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceRootLayout.setVisibility(View.VISIBLE);
            testQuestionFragmentBinding.multiChoiceQuestion.tvQuestion.setText(questionNew.getQ_quest());

            testQuestionFragmentBinding.multiChoiceQuestion.ans1.setText(questionNew.getQ_mcq_op_1());
            testQuestionFragmentBinding.multiChoiceQuestion.ans2.setText(questionNew.getQ_mcq_op_2());
            testQuestionFragmentBinding.multiChoiceQuestion.ans3.setText(questionNew.getQ_mcq_op_3());
            testQuestionFragmentBinding.multiChoiceQuestion.ans4.setText(questionNew.getQ_mcq_op_4());
            testQuestionFragmentBinding.multiChoiceQuestion.ans5.setText(questionNew.getQ_mcq_op_5());

            setTimer(testQuestionFragmentBinding.tvQuestTimer, 0, 0);
        }

        if (questionNew.getQ_type().equals(Constant.ONE_LINE_ANSWER)) {
            testQuestionFragmentBinding.oneLineQuestionAns.oneLineAnsRootLayout.setVisibility(View.VISIBLE);
            testQuestionFragmentBinding.oneLineQuestionAns.tvQuestion.setText(questionNew.getQ_quest());
            setTimer(testQuestionFragmentBinding.tvQuestTimer, 0, 0);
            testQuestionFragmentBinding.oneLineQuestionAns.etAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));
            testQuestionFragmentBinding.oneLineQuestionAns.etAnswer.setText(questionNew.getTtqa_sub_ans());
            answerCharCounter(testQuestionFragmentBinding.oneLineQuestionAns.etAnswer, testQuestionFragmentBinding.oneLineQuestionAns.tvCharCounter, 10);
        }

        if (questionNew.getQ_type().equals(Constant.Fill_THE_BLANKS)) {
            testQuestionFragmentBinding.fillInTheBlanks.fillInTheBlanksRootLayout.setVisibility(View.VISIBLE);
            testQuestionFragmentBinding.fillInTheBlanks.tvQuestion.setText(questionNew.getQ_quest());
            setTimer(testQuestionFragmentBinding.tvQuestTimer, 0, 0);
            testQuestionFragmentBinding.fillInTheBlanks.etAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));
            testQuestionFragmentBinding.fillInTheBlanks.etAnswer.setText(questionNew.getTtqa_sub_ans());
            answerCharCounter(testQuestionFragmentBinding.fillInTheBlanks.etAnswer, testQuestionFragmentBinding.fillInTheBlanks.tvCharCounter, 10);
        }

        return testQuestionFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        Log.d(TAG, "onActivityCreated position : " + position);

        testQuestionFragmentBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });

        testQuestionFragmentBinding.btnNext.setOnClickListener(v -> {
            testQuestions.get(position).setTtqa_visited(true);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });

        testQuestionFragmentBinding.btnPrevious.setOnClickListener(v -> {
            testQuestions.get(position).setTtqa_visited(true);
            StartTestActivity.viewPager.setCurrentItem(position - 1, true);
        });

        testQuestionFragmentBinding.btnReset.setOnClickListener(v -> {
            testQuestions.get(position).setTtqa_visited(true);
            resetAnswer();
        });

        testQuestionFragmentBinding.imgSave.setOnClickListener(v -> {
            if (testQuestionFragmentBinding.imgSave.isChecked()) {
                showToast("Checked");
                testQuestions.get(position).setTtqa_marked(true);
                testQuestions.get(position).setTtqa_visited(true);
            } else {
                showToast("UnChecked");
                testQuestions.get(position).setTtqa_marked(false);
                testQuestions.get(position).setTtqa_visited(true);
            }
        });

        //Single Choice Chip Selection
        testQuestionFragmentBinding.singleChoiceQuestion.ans1.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceAnswerGrp);
            if (testQuestionFragmentBinding.singleChoiceQuestion.ans1.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_1(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_1(false);
            }
        });
        testQuestionFragmentBinding.singleChoiceQuestion.ans2.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceAnswerGrp);
            if (testQuestionFragmentBinding.singleChoiceQuestion.ans2.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_2(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_2(false);
            }
        });
        testQuestionFragmentBinding.singleChoiceQuestion.ans3.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceAnswerGrp);
            if (testQuestionFragmentBinding.singleChoiceQuestion.ans3.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_3(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_3(false);
            }
        });
        testQuestionFragmentBinding.singleChoiceQuestion.ans4.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceAnswerGrp);
            if (testQuestionFragmentBinding.singleChoiceQuestion.ans4.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_4(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_4(false);
            }
        });
        testQuestionFragmentBinding.singleChoiceQuestion.ans5.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceAnswerGrp);
            if (testQuestionFragmentBinding.singleChoiceQuestion.ans5.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_5(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_5(false);
            }
        });

        //Multiple Choice Chip selection
        testQuestionFragmentBinding.multiChoiceQuestion.ans1.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceAnswerGrp);
            if (testQuestionFragmentBinding.multiChoiceQuestion.ans1.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_1(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_1(false);
            }
        });
        testQuestionFragmentBinding.multiChoiceQuestion.ans2.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceAnswerGrp);
            if (testQuestionFragmentBinding.multiChoiceQuestion.ans2.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_2(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_2(false);
            }
        });
        testQuestionFragmentBinding.multiChoiceQuestion.ans3.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceAnswerGrp);
            if (testQuestionFragmentBinding.multiChoiceQuestion.ans3.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_3(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_3(false);
            }
        });
        testQuestionFragmentBinding.multiChoiceQuestion.ans4.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceAnswerGrp);
            if (testQuestionFragmentBinding.multiChoiceQuestion.ans4.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_4(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_4(false);
            }
        });
        testQuestionFragmentBinding.multiChoiceQuestion.ans5.setOnClickListener(v -> {
            attemptedOrNot(testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceAnswerGrp);
            if (testQuestionFragmentBinding.multiChoiceQuestion.ans5.isChecked()) {
                testQuestions.get(position).setTtqa_mcq_ans_5(true);
            } else {
                testQuestions.get(position).setTtqa_mcq_ans_5(false);
            }
        });
    }

   /* private void setFillTheBlanksLayout(FillInTheBlanksLayoutBinding fillInTheBlanksBinding, int position) {
        Log.d(TAG, "setFillTheBlanksLayout: ");
        TestQuestionNew question = testQuestions.get(position);

        question.setA_sub_ans(fillInTheBlanksBinding.etAnswer.getText().toString());

        //setTimer(fillInTheBlanksBinding.tvtimer, 0, 0);
        answerCharCounter(fillInTheBlanksBinding.etAnswer, fillInTheBlanksBinding.tvCharCounter, 10);
       *//* fillInTheBlanksBinding.tvQuestNo.setText(String.valueOf(question.getQuestNo()));
        fillInTheBlanksBinding.tvQuestion.setText(question.getQuestionDesc());
        fillInTheBlanksBinding.etAnswer.setText(question.getDescriptiveAns());
        markedQuestMap.put(question.getQuestNo(), fillInTheBlanksBinding.imgSave);*//*
        //setMarkedQuest(question, fillInTheBlanksBinding.imgSave);

        fillInTheBlanksBinding.etAnswer.setKeyListener(DigitsKeyListener.getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));

        fillInTheBlanksBinding.imgSave.setOnClickListener(v -> {
            question.setTtqa_marked(true);
            fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        fillInTheBlanksBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });

        fillInTheBlanksBinding.btnMarkNext.setOnClickListener(v -> {
            question.setA_sub_ans(fillInTheBlanksBinding.etAnswer.getText().toString());
            //question.setMarked(true);
            question.setTtqa_visited(true);
            //fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star);
            StartTestActivity.viewPager.setCurrentItem(position - 1, true);
        });
        fillInTheBlanksBinding.btnSaveNext.setOnClickListener(v -> {
            //question.setMarked(false);
            //question.setSaved(true);
            question.setTtqa_visited(true);
            //fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });
        fillInTheBlanksBinding.btnClearResponse.setOnClickListener(v -> {
            fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            fillInTheBlanksBinding.etAnswer.setText("");
            question.setTtqa_marked(false);
            question.setTtqa_saved(false);
            question.setTtqa_attempted(false);
            question.setTtqa_visited(true);
        });
    }*/

  /*  private void setTrueFalseLayout(TrueFalseLayoutBinding trueFalseLayoutBinding, int position) {
        Log.d(TAG, "setTrueFalseLayout: ");
        TestQuestionNew testQuestion = testQuestions.get(position);
        setTimer(trueFalseLayoutBinding.tvQuestTimer, 0, 0);
        trueFalseLayoutBinding.tvQuestNo.setText(String.valueOf(testQuestion.getQuestNo()));
        trueFalseLayoutBinding.tvQuestion.setText(testQuestion.getQuestionDesc());
        markedQuestMap.put(testQuestion.getQuestNo(), trueFalseLayoutBinding.imgSave);
        setMarkedQuest(testQuestion, trueFalseLayoutBinding.imgSave);

        trueFalseLayoutBinding.imgSave.setOnClickListener(v -> {
            testQuestion.setTtqa_marked(true);
            trueFalseLayoutBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        trueFalseLayoutBinding.chipTrue.setOnClickListener(v -> {
            if (trueFalseLayoutBinding.chipTrue.isChecked()) {
                testQuestion.setTtqa_attempted(true);
                testQuestion.setTtqa_visited(false);
                testQuestion.setTtqa_mcq_ans_1(true);
            } else {
                testQuestion.setTtqa_attempted(false);
                testQuestion.setTtqa_visited(true);
            }
        });
        trueFalseLayoutBinding.chipFalse.setOnClickListener(v -> {
            if (trueFalseLayoutBinding.chipFalse.isChecked()) {
                testQuestion.setTtqa_attempted(true);
                testQuestion.setTtqa_visited(false);
                testQuestion.setTtqa_mcq_ans_2(true);
            } else {
                testQuestion.setTtqa_attempted(false);
                testQuestion.setTtqa_visited(true);
            }
        });
        trueFalseLayoutBinding.trueFalseAnswerChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {

            }
        });

        trueFalseLayoutBinding.btnMarkNext.setOnClickListener(v -> {
            //showToast("True False");
            //setMarkedQuest(testQuestion, trueFalseLayoutBinding.imgSave);
            //testQuestion.setMarked(true);
            //trueFalseLayoutBinding.imgSave.setImageResource(R.drawable.ic_star);
            StartTestActivity.viewPager.setCurrentItem(position - 1, true);
        });
        trueFalseLayoutBinding.btnSaveNext.setOnClickListener(v -> {
            //testQuestion.setMarked(false);
            //testQuestion.setSaved(true);
            //trueFalseLayoutBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });
        trueFalseLayoutBinding.btnClearResponse.setOnClickListener(v -> {
            trueFalseLayoutBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            resetAnswer(trueFalseLayoutBinding.trueFalseAnswerChipGrp, null, QuestionType..toString());
        });

        trueFalseLayoutBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });
    }*/

    /*private void setScqQuestionLayout(SingleChoiceQuestionBinding mBinding, int position) {
        Log.d(TAG, "setScqQuestionLayout: ");
        TestQuestionNew testQuestion = testQuestions.get(position);
        setTimer(mBinding.tvQuestTimer, 0, 0);
        setScqChipHashMaps();
        setCheckedChip(mapScqQuest);
        mBinding.tvQuestNo.setText(String.valueOf(testQuestion.getTq_quest_seq_num()));
        mBinding.tvQuestion.setText(testQuestion.getQ_quest());
        mBinding.ans1.setText(testQuestion.getA_mcq_2());
        mBinding.ans2.setText(testQuestion.getA_mcq_2());
        mBinding.ans3.setText(testQuestion.getA_mcq_3());
        mBinding.ans4.setText(testQuestion.getA_mcq_4());
        mBinding.ans5.setText(testQuestion.getA_mcq_5());
        markedQuestMap.put(Integer.parseInt(testQuestion.getTq_quest_seq_num()), mBinding.imgSave);
        //setMarkedQuest(testQuestion, mBinding.imgSave);

        mBinding.imgSave.setOnClickListener(v -> {
            testQuestion.setTtqa_marked(true);
            mBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        mBinding.ans1.setOnClickListener(v -> {
            if (mBinding.ans1.isChecked()) {
                testQuestion.setTtqa_mcq_ans_1(true);
            } else {
                testQuestion.setTtqa_mcq_ans_1(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans2.setOnClickListener(v -> {
            if (mBinding.ans2.isChecked()) {
                testQuestion.setTtqa_mcq_ans_2(true);
            } else {
                testQuestion.setTtqa_mcq_ans_2(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans3.setOnClickListener(v -> {
            if (mBinding.ans3.isChecked()) {
                testQuestion.setTtqa_mcq_ans_3(true);
            } else {
                testQuestion.setTtqa_mcq_ans_3(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans4.setOnClickListener(v -> {
            if (mBinding.ans4.isChecked()) {
                testQuestion.setTtqa_mcq_ans_4(true);
            } else {
                testQuestion.setTtqa_mcq_ans_4(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans5.setOnClickListener(v -> {
            if (mBinding.ans1.isChecked()) {
                testQuestion.setTtqa_mcq_ans_5(true);
            } else {
                testQuestion.setTtqa_mcq_ans_5(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });

        mBinding.btnMarkNext.setOnClickListener(v -> {
            //testQuestion.setMarked(true);
            //mBinding.imgSave.setImageResource(R.drawable.ic_star);
            StartTestActivity.viewPager.setCurrentItem(position - 1, true);
        });
        mBinding.btnSaveNext.setOnClickListener(v -> {
            //testQuestion.setSaved(true);
            //testQuestion.setMarked(false);
            testQuestion.setTtqa_visited(true);
            //mBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });
        mBinding.btnClearResponse.setOnClickListener(v -> {
            mBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            resetAnswer(mBinding.singleChoiceAnswerGrp, null, QuestionType.SCQ.toString());
        });

        mBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });

    }*/


    private void setMcqQuestionLayout(MultiChoiceQuestionBinding multiChoiceQuestionBinding, int position) {
        Log.d(TAG, "setMcqQuestionLayout: ");
        TestQuestionNew testQuestion = testQuestions.get(position);
        setMcqChipHashMaps();
        setCheckedChip(mapMcqQuest);
        setTimer(multiChoiceQuestionBinding.tvQuestTimer, 0, 0);
        multiChoiceQuestionBinding.tvQuestNo.setText(String.valueOf(position + 1));
        multiChoiceQuestionBinding.tvQuestion.setText(testQuestion.getQ_quest());

       /* multiChoiceQuestionBinding.tvQuestNo.setText(String.valueOf(testQuestion.getQuestNo()));
        multiChoiceQuestionBinding.tvQuestion.setText(testQuestion.getQuestionDesc());
        multiChoiceQuestionBinding.ans1.setText(testQuestion.getAnswer1());
        multiChoiceQuestionBinding.ans2.setText(testQuestion.getAnswer2());
        multiChoiceQuestionBinding.ans3.setText(testQuestion.getAnswer3());
        multiChoiceQuestionBinding.ans4.setText(testQuestion.getAnswer4());
        multiChoiceQuestionBinding.ans5.setText(testQuestion.getAnswer5());
        markedQuestMap.put(testQuestion.getQuestNo(), multiChoiceQuestionBinding.imgSave);
        setMarkedQuest(testQuestion, multiChoiceQuestionBinding.imgSave);

        multiChoiceQuestionBinding.imgSave.setOnClickListener(v -> {
            testQuestion.setMarked(true);
            multiChoiceQuestionBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        multiChoiceQuestionBinding.ans1.setOnClickListener(v -> {
            if (multiChoiceQuestionBinding.ans1.isChecked()) {
                testQuestion.setAns1(true);
            } else {
                testQuestion.setAns1(false);
            }
            attemptedOrNot(multiChoiceQuestionBinding.multiChoiceAnswerGrp);
            setCheckedChip(mapMcqQuest);
        });
        multiChoiceQuestionBinding.ans2.setOnClickListener(v -> {
            if (multiChoiceQuestionBinding.ans2.isChecked()) {
                testQuestion.setAns2(true);
            } else {
                testQuestion.setAns2(false);
            }
            attemptedOrNot(multiChoiceQuestionBinding.multiChoiceAnswerGrp);
            setCheckedChip(mapMcqQuest);
        });
        multiChoiceQuestionBinding.ans3.setOnClickListener(v -> {
            if (multiChoiceQuestionBinding.ans3.isChecked()) {
                testQuestion.setAns3(true);
            } else {
                testQuestion.setAns3(false);
            }
            attemptedOrNot(multiChoiceQuestionBinding.multiChoiceAnswerGrp);
            setCheckedChip(mapMcqQuest);
        });
        multiChoiceQuestionBinding.ans4.setOnClickListener(v -> {
            if (multiChoiceQuestionBinding.ans4.isChecked()) {
                testQuestion.setAns4(true);
            } else {
                testQuestion.setAns4(false);
            }
            attemptedOrNot(multiChoiceQuestionBinding.multiChoiceAnswerGrp);
            setCheckedChip(mapMcqQuest);
        });
        multiChoiceQuestionBinding.ans5.setOnClickListener(v -> {
            if (multiChoiceQuestionBinding.ans5.isChecked()) {
                testQuestion.setAns5(true);
            } else {
                testQuestion.setAns5(false);
            }
            attemptedOrNot(multiChoiceQuestionBinding.multiChoiceAnswerGrp);
            setCheckedChip(mapMcqQuest);
        });*/

        multiChoiceQuestionBinding.btnMarkNext.setOnClickListener(v -> {
            //setMarkedQuest(testQuestion, multiChoiceQuestionBinding.imgSave);
            //testQuestion.setMarked(true);
            //multiChoiceQuestionBinding.imgSave.setImageResource(R.drawable.ic_star);
            StartTestActivity.viewPager.setCurrentItem(position - 1, true);
        });
        multiChoiceQuestionBinding.btnSaveNext.setOnClickListener(v -> {
            //testQuestion.setSaved(true);
            //testQuestion.setMarked(false);
            //multiChoiceQuestionBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });
        multiChoiceQuestionBinding.btnClearResponse.setOnClickListener(v -> {
            multiChoiceQuestionBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            //resetAnswer(multiChoiceQuestionBinding.multiChoiceAnswerGrp, null, QuestionType.MCQ.toString());
            //questAnsBtnClickListener.onResetClick();
        });

        multiChoiceQuestionBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });
    }

   /* private void setMultiLineQuestAnsLayout(MultiLineQuestAnsBinding multiLineQuestAnsBinding, int position) {
        Log.d(TAG, "setMultiLineQuestAnsLayout: ");
        TestQuestionNew testQuestion = testQuestions.get(position);
        testQuestion.setTtqa_visited(true);
        testQuestion.setTtqa_sub_ans(multiLineQuestAnsBinding.etMultiLineAnswer.getText().toString());
        multiLineQuestAnsBinding.etMultiLineAnswer.setKeyListener(DigitsKeyListener.getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));

        setTimer(multiLineQuestAnsBinding.tvQuestTimer, 0, 0);
        answerCharCounter(multiLineQuestAnsBinding.etMultiLineAnswer, multiLineQuestAnsBinding.tvCharCounter, 200);
        multiLineQuestAnsBinding.tvQuestNo.setText(String.valueOf(testQuestion.getTq_quest_seq_num()));
        multiLineQuestAnsBinding.tvQuestion.setText(testQuestion.getQ_quest());
        multiLineQuestAnsBinding.etMultiLineAnswer.setText(testQuestion.getTtqa_sub_ans());
        markedQuestMap.put(Integer.parseInt(testQuestion.getTq_quest_seq_num()), multiLineQuestAnsBinding.imgSave);
        setMarkedQuest(testQuestion, multiLineQuestAnsBinding.imgSave);

        multiLineQuestAnsBinding.imgSave.setOnClickListener(v -> {
            testQuestion.setTtqa_marked(true);
            multiLineQuestAnsBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        multiLineQuestAnsBinding.btnMarkNext.setOnClickListener(v -> {
            //showToast("MultiLine");
            //setMarkedQuest(testQuestion, multiLineQuestAnsBinding.imgSave);
            //testQuestion.setMarked(true);
            if (multiLineQuestAnsBinding.etMultiLineAnswer.getText().toString().trim().length() > 0)
                testQuestion.setTtqa_attempted(true);
            testQuestion.setTtqa_visited(false);
            //multiLineQuestAnsBinding.imgSave.setImageResource(R.drawable.ic_star);
            StartTestActivity.viewPager.setCurrentItem(position - 1, true);
        });
        multiLineQuestAnsBinding.btnSaveNext.setOnClickListener(v -> {
            //testQuestion.setMarked(false);
            //testQuestion.setSaved(true);
            //testQuestion.setAttempted(true);
            //testQuestion.setVisited(false);
            //multiLineQuestAnsBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });
        multiLineQuestAnsBinding.btnClearResponse.setOnClickListener(v -> {
            testQuestion.setTtqa_attempted(false);
            testQuestion.setTtqa_saved(false);
            testQuestion.setTtqa_visited(true);
            testQuestion.setTtqa_marked(false);
            multiLineQuestAnsBinding.etMultiLineAnswer.setText("");
            multiLineQuestAnsBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            //multiLineQuestAnsBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            //resetAnswer(null, multiLineQuestAnsBinding.etMultiLineAnswer, QuestionType.MULTI_LINE_ANSWER.toString());
            //questAnsBtnClickListener.onResetClick();
        });
        multiLineQuestAnsBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });
    }*/


    @Override
    public void getQueViewClick(String strQuestTag, int position) {
    }

    @Override
    public void onTabClickClick(int queNo, String strQuestTag, int position) {

    }

    @Override
    public void onTabPositionChange(int position) {
        this.position = position;
    }

    private void setTimer(TextView timer, int seconds, int minutes) {

        new CountDownTimer(60 * 1000 * 60, 1000) {
            int timerCountSeconds = seconds;
            int timerCountMinutes = minutes;

            public void onTick(long millisUntilFinished) {
                // timer.setText(new SimpleDateFormat("mm:ss").format(new Date( millisUntilFinished)));
                if (timerCountSeconds < 59) {
                    timerCountSeconds++;
                } else {
                    timerCountSeconds = 0;
                    timerCountMinutes++;
                }
                if (timerCountMinutes < 10) {
                    if (timerCountSeconds < 10) {
                        timer.setText(String.valueOf("0" + timerCountMinutes + ":0" + timerCountSeconds));
                    } else {
                        timer.setText(String.valueOf("0" + timerCountMinutes + ":" + timerCountSeconds));
                    }
                } else {
                    if (timerCountSeconds < 10) {
                        timer.setText(String.valueOf(timerCountMinutes + ":0" + timerCountSeconds));
                    } else {
                        timer.setText(String.valueOf(timerCountMinutes + ":" + timerCountSeconds));
                    }
                }
            }

            public void onFinish() {
                timer.setText("00:00");
            }

        }.start();
    }

    private void answerCharCounter(EditText etAnswer, TextView tvCounter, int maxWordLength) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return null;
            }
        };
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int wordCount = 0;
                if (s.toString().contains(" ")) {
                    String[] words = s.toString().split(" ", -1);
                    for (int i = 0; i < words.length; i++) {
                        if (!words[i].isEmpty()) {
                            wordCount++;
                        }
                    }
                    //wordCount = words.length;
                    //showToast(" Count " + wordCount);

                    if (wordCount > 0) {
                        testQuestions.get(position).setTtqa_sub_ans(s.toString());
                        testQuestions.get(position).setTtqa_attempted(true);
                        testQuestions.get(position).setTtqa_visited(false);
                    } else {
                        testQuestions.get(position).setTtqa_sub_ans("");
                        testQuestions.get(position).setTtqa_attempted(false);
                        testQuestions.get(position).setTtqa_visited(true);
                    }
                }

                if (wordCount > maxWordLength) {
                    etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length())});
                }
                tvCounter.setText("Words Remaining : " + (maxWordLength - wordCount + "/" + String.valueOf(maxWordLength)));
            }
        });
    }

    private void setCheckedChip(HashMap<Integer, Chip> map) {
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i).isChecked()) {
                map.get(i).setTextColor(Color.WHITE);
                //showToast("Attempted True: "+testQuestions.get(position).getQuestNo()+","+testQuestions.get(position).getQuestType());
                //testQuestions.get(position).setAttempted(true);
            } else {
                //testQuestions.get(position).setAttempted(false);
                map.get(i).setTextColor(Color.BLACK);
                //showToast("Attempted False: "+testQuestions.get(position).getQuestNo()+","+testQuestions.get(position).getQuestType());
            }
        }
    }


    private void setScqChipHashMaps() {
        mapScqQuest.put(0, singleChoiceQuestionBinding.ans1);
        mapScqQuest.put(1, singleChoiceQuestionBinding.ans2);
        mapScqQuest.put(2, singleChoiceQuestionBinding.ans3);
        mapScqQuest.put(3, singleChoiceQuestionBinding.ans4);
        mapScqQuest.put(4, singleChoiceQuestionBinding.ans5);
    }

    private void setMcqChipHashMaps() {
        mapMcqQuest.put(0, multiChoiceQuestionBinding.ans1);
        mapMcqQuest.put(1, multiChoiceQuestionBinding.ans2);
        mapMcqQuest.put(2, multiChoiceQuestionBinding.ans3);
        mapMcqQuest.put(3, multiChoiceQuestionBinding.ans4);
        mapMcqQuest.put(4, multiChoiceQuestionBinding.ans5);
    }

    @Override
    public void onReportQuestSubmitClick() {
        questReportDialog.dismiss();
    }

    private void resetAnswer() {
        TestQuestionNew question = testQuestions.get(position);

        if (question.getQ_type().equals(Constant.SCQ)) {
            ChipGroup chipGroup = testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceAnswerGrp;
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                chip.setChecked(false);
            }
        }

        if (question.getQ_type().equals(Constant.MCQ)) {
            ChipGroup chipGroup = testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceAnswerGrp;
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                chip.setChecked(false);
            }
        }

        if (question.getQ_type().equals(Constant.Fill_THE_BLANKS)) {
            question.setTtqa_sub_ans("");
            testQuestionFragmentBinding.fillInTheBlanks.etAnswer.setText("");
        }

        if (question.getQ_type().equals(Constant.ONE_LINE_ANSWER)) {
            question.setTtqa_sub_ans("");
            testQuestionFragmentBinding.oneLineQuestionAns.etAnswer.setText("");
        }

        if (question.getQ_type().equals(Constant.LONG_ANSWER)) {
            question.setTtqa_sub_ans("");
            testQuestionFragmentBinding.longAnswer.etMultiLineAnswer.setText("");
        }

        if (question.getQ_type().equals(Constant.SHORT_ANSWER)) {
            question.setTtqa_sub_ans("");
            testQuestionFragmentBinding.shortAnswer.etMultiLineAnswer.setText("");
        }

        question.setTtqa_mcq_ans_1(false);
        question.setTtqa_mcq_ans_2(false);
        question.setTtqa_mcq_ans_3(false);
        question.setTtqa_mcq_ans_4(false);
        question.setTtqa_mcq_ans_5(false);

        question.setTtqa_visited(true);
        question.setTtqa_marked(false);
        question.setTtqa_saved(false);
        question.setTtqa_attempted(false);
    }

    private void attemptedOrNot(ChipGroup chipGroup) {
        int checkCounter = 0;
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                checkCounter++;
            }
        }
        if (checkCounter > 0) {
            testQuestions.get(position).setTtqa_attempted(true);
            testQuestions.get(position).setTtqa_visited(false);
        } else {
            testQuestions.get(position).setTtqa_attempted(false);
            testQuestions.get(position).setTtqa_visited(true);
        }
    }

    private void setMarkedQuest(TestQuestionNew testQuestion, ImageView imageView) {
        Log.d(TAG, "setMarkedQuest Image Pos : " + testQuestion.getTq_quest_disp_num());
        ImageView image = markedQuestMap.get(testQuestion.getTq_quest_disp_num());
        if (testQuestion != null) {
            if (testQuestion.isTtqa_marked()) {
                //testQuestion.setImageResource(R.drawable.ic_star);
                image.setImageResource(R.drawable.ic_star);
            } else {
                image.setImageResource(R.drawable.ic_star_border);
            }
        }
    }
}