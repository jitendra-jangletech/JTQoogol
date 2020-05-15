package com.jangletech.qoogol.Test;

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
import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FillInTheBlanksLayoutBinding;
import com.jangletech.qoogol.databinding.MultiChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.MultiLineQuestAnsBinding;
import com.jangletech.qoogol.databinding.SingleChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.TestFragmentBinding;
import com.jangletech.qoogol.databinding.TrueFalseLayoutBinding;
import com.jangletech.qoogol.dialog.QuestReportDialog;
import com.jangletech.qoogol.enums.QuestionType;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.TestQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import java.util.HashMap;
import java.util.List;


public class TestFragment extends BaseFragment implements QueViewClick, QuestReportDialog.QuestReportDialogListener {
    private static final String TAG = "TestFragment";
    private static final String ARG_COUNT = "param1";
    private TestViewModel mViewModel;
    private TestFragmentBinding testFragmentBinding;
    private SingleChoiceQuestionBinding singleChoiceQuestionBinding;
    private MultiChoiceQuestionBinding multiChoiceQuestionBinding;
    private FillInTheBlanksLayoutBinding fillInTheBlanksBinding;
    private TrueFalseLayoutBinding trueFalseLayoutBinding;
    private MultiLineQuestAnsBinding multiLineQuestAnsBinding;
    private List<TestQuestion> testQuestions;
    QuestReportDialog questReportDialog;
    private int position;
    HashMap<Integer, Chip> mapScqQuest = new HashMap();
    HashMap<Integer, Chip> mapMcqQuest = new HashMap();
    HashMap<Integer, ImageView> markedQuestMap = new HashMap<>();
    TabLayout tabLayout;

   /* public TestFragment(List<TestQuestion> testQuestionList, int pos, QuestAnsBtnClickListener questAnsBtnClickListener) {
        this.testQuestions = testQuestionList;
        this.position = pos;
        this.questAnsBtnClickListener = questAnsBtnClickListener;
    }*/

    public TestFragment() {
       // this.testQuestions = StartTestActivity.testQuestionList;
    }

    public static TestFragment newInstance(Integer counter) {
        TestFragment fragment = new TestFragment();
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

        //showToast("Position Selected " + position);

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.MCQ.toString())) {
            multiChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.multi_choice_question, container, false);
            //setMcqQuestionLayout(multiChoiceQuestionBinding, position);
            return multiChoiceQuestionBinding.getRoot();
        }
        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.SCQ.toString())) {
            singleChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.single_choice_question, container, false);
            //setScqQuestionLayout(singleChoiceQuestionBinding, position);
            return singleChoiceQuestionBinding.getRoot();
        }

      /*  if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.FILL_THE_BLANKS.toString())) {
            fillInTheBlanksBinding = DataBindingUtil.inflate(inflater, R.layout.fill_in_the_blanks_layout, container, false);
            setFillTheBlanksLayout(fillInTheBlanksBinding, position);
            return fillInTheBlanksBinding.getRoot();
        }

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.MULTI_LINE_ANSWER.toString())) {
            multiLineQuestAnsBinding = DataBindingUtil.inflate(inflater, R.layout.multi_line_quest_ans, container, false);
            setMultiLineQuestAnsLayout(multiLineQuestAnsBinding, position);
            return multiLineQuestAnsBinding.getRoot();
        }*/

     /*   if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.TRUE_FALSE.toString())) {
            trueFalseLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.true_false_layout, container, false);
            setTrueFalseLayout(trueFalseLayoutBinding, position);
            return trueFalseLayoutBinding.getRoot();
        }*/



        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);
        Log.d(TAG, "onActivityCreated position : " + position);

    }

   /* private void setFillTheBlanksLayout(FillInTheBlanksLayoutBinding fillInTheBlanksBinding, int position) {
        Log.d(TAG, "setFillTheBlanksLayout: ");
        TestQuestion question = testQuestions.get(position);

        question.setDescriptiveAns(fillInTheBlanksBinding.etAnswer.getText().toString());

        //setTimer(fillInTheBlanksBinding.tvtimer, 0, 0);
        answerCharCounter(fillInTheBlanksBinding.etAnswer, fillInTheBlanksBinding.tvCharCounter, 10);
        fillInTheBlanksBinding.tvQuestNo.setText(String.valueOf(question.getQuestNo()));
        fillInTheBlanksBinding.tvQuestion.setText(question.getQuestionDesc());
        fillInTheBlanksBinding.etAnswer.setText(question.getDescriptiveAns());
        markedQuestMap.put(question.getQuestNo(), fillInTheBlanksBinding.imgSave);
        setMarkedQuest(question, fillInTheBlanksBinding.imgSave);


        fillInTheBlanksBinding.etAnswer.setKeyListener(DigitsKeyListener.getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));

        fillInTheBlanksBinding.imgSave.setOnClickListener(v->{
            question.setMarked(true);
            fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        fillInTheBlanksBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });

        fillInTheBlanksBinding.btnMarkNext.setOnClickListener(v -> {
            question.setDescriptiveAns(fillInTheBlanksBinding.etAnswer.getText().toString());
            //question.setMarked(true);
            question.setVisited(true);
            //fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star);
            StartTestActivity.viewPager.setCurrentItem(position - 1, true);
        });
        fillInTheBlanksBinding.btnSaveNext.setOnClickListener(v -> {
            //question.setMarked(false);
            //question.setSaved(true);
            question.setVisited(true);
            //fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });
        fillInTheBlanksBinding.btnClearResponse.setOnClickListener(v -> {
            fillInTheBlanksBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            fillInTheBlanksBinding.etAnswer.setText("");
            question.setMarked(false);
            question.setSaved(false);
            question.setAttempted(false);
            question.setVisited(true);
        });
    }*/

   /* private void setTrueFalseLayout(TrueFalseLayoutBinding trueFalseLayoutBinding, int position) {
        Log.d(TAG, "setTrueFalseLayout: ");
        TestQuestion testQuestion = testQuestions.get(position);
        setTimer(trueFalseLayoutBinding.tvQuestTimer, 0, 0);
        trueFalseLayoutBinding.tvQuestNo.setText(String.valueOf(testQuestion.getQuestNo()));
        trueFalseLayoutBinding.tvQuestion.setText(testQuestion.getQuestionDesc());
        markedQuestMap.put(testQuestion.getQuestNo(), trueFalseLayoutBinding.imgSave);
        setMarkedQuest(testQuestion, trueFalseLayoutBinding.imgSave);

        trueFalseLayoutBinding.imgSave.setOnClickListener(v->{
            testQuestion.setMarked(true);
            trueFalseLayoutBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        trueFalseLayoutBinding.chipTrue.setOnClickListener(v -> {
            if (trueFalseLayoutBinding.chipTrue.isChecked()) {
                testQuestion.setAttempted(true);
                testQuestion.setVisited(false);
                testQuestion.setAns1(true);
            } else {
                testQuestion.setAttempted(false);
                testQuestion.setVisited(true);
            }
        });
        trueFalseLayoutBinding.chipFalse.setOnClickListener(v -> {
            if (trueFalseLayoutBinding.chipFalse.isChecked()) {
                testQuestion.setAttempted(true);
                testQuestion.setVisited(false);
                testQuestion.setAns2(true);
            } else {
                testQuestion.setAttempted(false);
                testQuestion.setVisited(true);
            }
        });
        *//*trueFalseLayoutBinding.trueFalseAnswerChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {

            }
        });*//*

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
            resetAnswer(trueFalseLayoutBinding.trueFalseAnswerChipGrp, null, QuestionType.TRUE_FALSE.toString());
        });

        trueFalseLayoutBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });
    }*/

   /* private void setScqQuestionLayout(SingleChoiceQuestionBinding mBinding, int position) {
        Log.d(TAG, "setScqQuestionLayout: ");
        TestQuestion testQuestion = testQuestions.get(position);

        setTimer(mBinding.tvQuestTimer, 0, 0);
        setScqChipHashMaps();
        setCheckedChip(mapScqQuest);
        mBinding.tvQuestNo.setText(String.valueOf(testQuestion.getQuestNo()));
        mBinding.tvQuestion.setText(testQuestion.getQuestionDesc());
        mBinding.ans1.setText(testQuestion.getAnswer1());
        mBinding.ans2.setText(testQuestion.getAnswer2());
        mBinding.ans3.setText(testQuestion.getAnswer3());
        mBinding.ans4.setText(testQuestion.getAnswer4());
        mBinding.ans5.setText(testQuestion.getAnswer5());
        markedQuestMap.put(testQuestion.getQuestNo(), mBinding.imgSave);
        setMarkedQuest(testQuestion, mBinding.imgSave);

        mBinding.imgSave.setOnClickListener(v->{
            testQuestion.setMarked(true);
            mBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        mBinding.ans1.setOnClickListener(v -> {
            if (mBinding.ans1.isChecked()) {
                testQuestion.setAns1(true);
            } else {
                testQuestion.setAns1(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans2.setOnClickListener(v -> {
            if (mBinding.ans2.isChecked()) {
                testQuestion.setAns2(true);
            } else {
                testQuestion.setAns2(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans3.setOnClickListener(v -> {
            if (mBinding.ans3.isChecked()) {
                testQuestion.setAns3(true);
            } else {
                testQuestion.setAns3(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans4.setOnClickListener(v -> {
            if (mBinding.ans4.isChecked()) {
                testQuestion.setAns4(true);
            } else {
                testQuestion.setAns4(false);
            }
            attemptedOrNot(mBinding.singleChoiceAnswerGrp);
            setCheckedChip(mapScqQuest);
        });
        mBinding.ans5.setOnClickListener(v -> {
            if (mBinding.ans1.isChecked()) {
                testQuestion.setAns5(true);
            } else {
                testQuestion.setAns5(false);
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
            testQuestion.setVisited(true);
            //mBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            StartTestActivity.viewPager.setCurrentItem(position + 1, true);
        });
        mBinding.btnClearResponse.setOnClickListener(v -> {
            mBinding.imgSave.setImageResource(R.drawable.ic_star_border);
            //resetAnswer(mBinding.singleChoiceAnswerGrp, null, QuestionType.SCQ.toString());
        });

        mBinding.imgReport.setOnClickListener(v -> {
            questReportDialog = new QuestReportDialog(getContext(), this);
            questReportDialog.show();
        });

    }


    private void setMcqQuestionLayout(MultiChoiceQuestionBinding multiChoiceQuestionBinding, int position) {
        Log.d(TAG, "setMcqQuestionLayout: ");
        TestQuestion testQuestion = testQuestions.get(position);
        setMcqChipHashMaps();
        setCheckedChip(mapMcqQuest);
        setTimer(multiChoiceQuestionBinding.tvQuestTimer, 0, 0);
        multiChoiceQuestionBinding.tvQuestNo.setText(String.valueOf(testQuestion.getQuestNo()));
        multiChoiceQuestionBinding.tvQuestion.setText(testQuestion.getQuestionDesc());
        multiChoiceQuestionBinding.ans1.setText(testQuestion.getAnswer1());
        multiChoiceQuestionBinding.ans2.setText(testQuestion.getAnswer2());
        multiChoiceQuestionBinding.ans3.setText(testQuestion.getAnswer3());
        multiChoiceQuestionBinding.ans4.setText(testQuestion.getAnswer4());
        multiChoiceQuestionBinding.ans5.setText(testQuestion.getAnswer5());
        markedQuestMap.put(testQuestion.getQuestNo(), multiChoiceQuestionBinding.imgSave);
        setMarkedQuest(testQuestion, multiChoiceQuestionBinding.imgSave);

        multiChoiceQuestionBinding.imgSave.setOnClickListener(v->{
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
        });

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

    private void setMultiLineQuestAnsLayout(MultiLineQuestAnsBinding multiLineQuestAnsBinding, int position) {
        Log.d(TAG, "setMultiLineQuestAnsLayout: ");
        TestQuestion testQuestion = testQuestions.get(position);
        //testQuestion.setVisited(true);
        testQuestion.setDescriptiveAns(multiLineQuestAnsBinding.etMultiLineAnswer.getText().toString());
        multiLineQuestAnsBinding.etMultiLineAnswer.setKeyListener(DigitsKeyListener.getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));

       // setTimer(multiLineQuestAnsBinding.tvtimer, 0, 0);
        answerCharCounter(multiLineQuestAnsBinding.etMultiLineAnswer, multiLineQuestAnsBinding.tvCharCounter, 200);
        multiLineQuestAnsBinding.tvQuestNo.setText(String.valueOf(testQuestion.getQuestNo()));
        multiLineQuestAnsBinding.tvQuestion.setText(testQuestion.getQuestionDesc());
        multiLineQuestAnsBinding.etMultiLineAnswer.setText(testQuestion.getDescriptiveAns());
        markedQuestMap.put(testQuestion.getQuestNo(), multiLineQuestAnsBinding.imgSave);
        setMarkedQuest(testQuestion, multiLineQuestAnsBinding.imgSave);

        multiLineQuestAnsBinding.imgSave.setOnClickListener(v->{
            testQuestion.setMarked(true);
            multiLineQuestAnsBinding.imgSave.setImageResource(R.drawable.ic_star);
        });

        multiLineQuestAnsBinding.btnMarkNext.setOnClickListener(v -> {
            //showToast("MultiLine");
            //setMarkedQuest(testQuestion, multiLineQuestAnsBinding.imgSave);
            //testQuestion.setMarked(true);
            if(multiLineQuestAnsBinding.etMultiLineAnswer.getText().toString().trim().length() > 0)
            testQuestion.setAttempted(true);
            testQuestion.setVisited(false);
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
            testQuestion.setAttempted(false);
            testQuestion.setSaved(false);
            testQuestion.setVisited(true);
            testQuestion.setMarked(false);
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
                    for (int i = 0; i <words.length ; i++) {
                        if(!words[i].isEmpty()){
                            wordCount++;
                        }
                    }
                    //wordCount = words.length;
                    showToast(" Count "+wordCount);

                    if (wordCount > 0) {
                        testQuestions.get(position).setDescriptiveAns(s.toString());
                        testQuestions.get(position).setAttempted(true);
                        testQuestions.get(position).setVisited(false);
                    } else {
                        testQuestions.get(position).setDescriptiveAns("");
                        testQuestions.get(position).setAttempted(false);
                        testQuestions.get(position).setVisited(true);
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
    public void onReportQuestSubmitClick(int position) {
        showToast("Question No : "+StartTestActivity.testQuestionList.get(position).getTq_quest_seq_num()+" Reported successfully.");
        questReportDialog.dismiss();
    }

    /*private void resetAnswer(ChipGroup chipGroup, EditText etAnswer, String questType) {
        TestQuestion question = testQuestions.get(position);
        Log.d(TAG, "Quest No : " + question.getQuestNo());
        Log.d(TAG, "Question : " + question.getQuestionDesc());
        Log.d(TAG, "Ans 1: " + question.isAns1());
        Log.d(TAG, "Ans 2: " + question.isAns1());
        Log.d(TAG, "Ans 3: " + question.isAns1());
        Log.d(TAG, "Ans 4: " + question.isAns1());
        Log.d(TAG, "Ans 5: " + question.isAns1());
        if (questType.equals(QuestionType.SCQ.toString())
                || questType.equals(QuestionType.MCQ.toString())
                || questType.equals(QuestionType.TRUE_FALSE.toString())) {
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                chip.setChecked(false);
            }
            if (questType.equals(QuestionType.MCQ.toString()))
                setCheckedChip(mapMcqQuest);

            if (questType.equals(QuestionType.SCQ.toString()))
                setCheckedChip(mapScqQuest);

            question.setAns1(false);
            question.setAns2(false);
            question.setAns3(false);
            question.setAns4(false);
            question.setAns5(false);

            question.setMarked(false);
            question.setSaved(false);
            question.setAttempted(false);
            question.setVisited(true);

        }

        if (question.equals(QuestionType.FILL_THE_BLANKS.toString())
                || question.equals(QuestionType.MULTI_LINE_ANSWER.toString())) {
            question.setDescriptiveAns("");
            etAnswer.setText("");
            question.setMarked(false);
            question.setSaved(false);
        }
    }*/

    private void attemptedOrNot(ChipGroup chipGroup) {
        int checkCounter = 0;
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                checkCounter++;
            }
        }
        if (checkCounter > 0) {
            testQuestions.get(position).setAttempted(true);
            testQuestions.get(position).setVisited(false);
        } else {
            testQuestions.get(position).setAttempted(false);
            testQuestions.get(position).setVisited(true);
        }
    }

    private void setMarkedQuest(TestQuestion testQuestion, ImageView imageView) {
        Log.d(TAG, "setMarkedQuest Image Pos : " + testQuestion.getQuestNo());
        ImageView image = markedQuestMap.get(testQuestion.getQuestNo());
        if (testQuestion != null) {
            if (testQuestion.isMarked()) {
                //testQuestion.setImageResource(R.drawable.ic_star);
                image.setImageResource(R.drawable.ic_star);
            } else {
                image.setImageResource(R.drawable.ic_star_border);
            }
        }
    }

    /*public interface QuestAnsBtnClickListener {
        void onMarkAndNextClick();

        void onSaveAndNextClick();

        void onResetClick();
    }*/
}
