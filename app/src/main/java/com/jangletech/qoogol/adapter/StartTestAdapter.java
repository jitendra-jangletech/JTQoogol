package com.jangletech.qoogol.adapter;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.databinding.TestQuestionFragmentBinding;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.util.Constant;

import java.util.HashMap;
import java.util.List;

public class StartTestAdapter extends RecyclerView.Adapter<StartTestAdapter.ViewHolder> {

    private static final String TAG = "StartTestAdapter";
    public static List<TestQuestionNew> testQuestions;
    private Context mContext;
    private TestQuestionFragmentBinding testQuestionFragmentBinding;
    private int poss;
    public static CountDownTimer countDownTimer;
    StartAdapterButtonClickListener listener;
    HashMap<String, ToggleButton> mapImageMark = new HashMap<>();
    HashMap<String, TextView> mapTimer = new HashMap<>();
    HashMap<String, EditText> mapAnsEditText;

    public StartTestAdapter(List<TestQuestionNew> testQuestionNewList, Context mContext, StartAdapterButtonClickListener listener) {
        this.testQuestions = testQuestionNewList;
        this.mContext = mContext;
        this.listener = listener;
    }

    private void setEditTextMap(List<TestQuestionNew> list) {
        mapAnsEditText = new HashMap<>();
        for (TestQuestionNew questionNew : testQuestions) {
            if (questionNew.getQ_type().equals(Constant.SHORT_ANSWER)) {
                mapAnsEditText.put(questionNew.getTq_quest_seq_num(), testQuestionFragmentBinding.shortAnswer.etMultiLineAnswer);
            }
            if (questionNew.getQ_type().equals(Constant.LONG_ANSWER)) {
                mapAnsEditText.put(questionNew.getTq_quest_seq_num(), testQuestionFragmentBinding.longAnswer.etMultiLineAnswer);
            }
            if (questionNew.getQ_type().equals(Constant.ONE_LINE_ANSWER)) {
                mapAnsEditText.put(questionNew.getTq_quest_seq_num(), testQuestionFragmentBinding.oneLineQuestionAns.etAnswer);
            }
            if (questionNew.getQ_type().equals(Constant.FILL_THE_BLANKS)) {
                mapAnsEditText.put(questionNew.getTq_quest_seq_num(), testQuestionFragmentBinding.fillInTheBlanks.etAnswer);
            }
        }
    }

    @NonNull
    @Override
    public StartTestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        testQuestionFragmentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.test_question_fragment, parent, false);
        return new ViewHolder(testQuestionFragmentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StartTestAdapter.ViewHolder holder, int position) {
        poss = position; //StartTestActivity.viewPager.getCurrentItem();

        setEditTextMap(testQuestions);
        //startTimer(StartTestActivity.viewPager.getCurrentItem());
        //Toast.makeText(mContext, "Executed!!!", Toast.LENGTH_SHORT).show();
        //int qIdPos = StartTestActivity.getCurrentItemPos();
        EditText editText = mapAnsEditText.get(testQuestions.get(StartTestActivity.getCurrentItemPos()).getTq_quest_seq_num());
        TestQuestionNew questionNew = testQuestions.get(poss);
        holder.testQuestionFragmentBinding.tvQuestNo.setText(questionNew.getTq_quest_seq_num());
        Log.d(TAG, "Marked Position : " + position);
        Log.d(TAG, "Question : " + questionNew.getQ_quest());
        Log.d(TAG, "isMarked : " + questionNew.isTtqa_marked());
        if (questionNew.isTtqa_marked()) {
            holder.testQuestionFragmentBinding.imgSave.setChecked(true);
            mapImageMark.put(questionNew.getTq_quest_seq_num(), holder.testQuestionFragmentBinding.imgSave);
        } else {
            holder.testQuestionFragmentBinding.imgSave.setChecked(false);
            mapImageMark.put(questionNew.getTq_quest_seq_num(), holder.testQuestionFragmentBinding.imgSave);
        }

        if (questionNew.getQ_type().equals(Constant.SCQ)) {
            Log.d(TAG, "onBindViewHolder SCQ : " + questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceQuestRootLayout.setVisibility(View.VISIBLE);

            holder.testQuestionFragmentBinding.shortAnswer.shortAnswerRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.longAnswer.multiLineQuestionRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.oneLineQuestionAns.oneLineAnsRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.fillInTheBlanks.fillInTheBlanksRootLayout.setVisibility(View.GONE);

            holder.testQuestionFragmentBinding.singleChoiceQuestion.tvQuestion.setText(questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.singleChoiceQuestion.ans1.setText(questionNew.getQ_mcq_op_1());
            holder.testQuestionFragmentBinding.singleChoiceQuestion.ans2.setText(questionNew.getQ_mcq_op_2());
            holder.testQuestionFragmentBinding.singleChoiceQuestion.ans3.setText(questionNew.getQ_mcq_op_3());
            holder.testQuestionFragmentBinding.singleChoiceQuestion.ans4.setText(questionNew.getQ_mcq_op_4());
            holder.testQuestionFragmentBinding.singleChoiceQuestion.ans5.setText(questionNew.getQ_mcq_op_5());
            mapTimer.put(testQuestions.get(poss).getTq_quest_seq_num(), testQuestionFragmentBinding.tvQuestTimer);
            createTimer(StartTestActivity.getCurrentItemPos(), 0, 0);
        }

        if (questionNew.getQ_type().equals(Constant.SHORT_ANSWER)) {
            Log.d(TAG, "onBindViewHolder SHORT_ANSWER : " + questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.shortAnswer.shortAnswerRootLayout.setVisibility(View.VISIBLE);

            holder.testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceQuestRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.longAnswer.multiLineQuestionRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.oneLineQuestionAns.oneLineAnsRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.fillInTheBlanks.fillInTheBlanksRootLayout.setVisibility(View.GONE);

            holder.testQuestionFragmentBinding.shortAnswer.tvQuestion.setText(questionNew.getQ_quest());
            mapTimer.put(testQuestions.get(poss).getTq_quest_seq_num(), testQuestionFragmentBinding.tvQuestTimer);
            createTimer(StartTestActivity.getCurrentItemPos(), 0, 0);

            //mapAnsEditText.put(testQuestions.get(StartTestActivity.getCurrentItemPos()).getTq_quest_seq_num(),holder.testQuestionFragmentBinding.shortAnswer.etMultiLineAnswer);


            holder.testQuestionFragmentBinding.shortAnswer.etMultiLineAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));


            editText.setText(questionNew.getTtqa_sub_ans());

            answerCharCounter(editText, holder.testQuestionFragmentBinding.shortAnswer.tvCharCounter, 50);
        }

        if (questionNew.getQ_type().equals(Constant.LONG_ANSWER)) {
            Log.d(TAG, "onBindViewHolder : " + questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.longAnswer.multiLineQuestionRootLayout.setVisibility(View.VISIBLE);

            holder.testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceQuestRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.shortAnswer.shortAnswerRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.oneLineQuestionAns.oneLineAnsRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.fillInTheBlanks.fillInTheBlanksRootLayout.setVisibility(View.GONE);
            //focusChangeListener(testQuestionFragmentBinding.longAnswer.etMultiLineAnswer);
            holder.testQuestionFragmentBinding.longAnswer.tvQuestion.setText(questionNew.getQ_quest());
            mapAnsEditText.put(testQuestions.get(StartTestActivity.getCurrentItemPos()).getTq_quest_seq_num(), holder.testQuestionFragmentBinding.longAnswer.etMultiLineAnswer);
            mapTimer.put(testQuestions.get(poss).getTq_quest_seq_num(), testQuestionFragmentBinding.tvQuestTimer);
            createTimer(StartTestActivity.getCurrentItemPos(), 0, 0);
            holder.testQuestionFragmentBinding.longAnswer.etMultiLineAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));

            editText.setText(questionNew.getTtqa_sub_ans());
            //testQuestionFragmentBinding.longAnswer.etMultiLineAnswer.setText(questionNew.getTtqa_sub_ans());

            //answerCharCounter(holder.testQuestionFragmentBinding.longAnswer.etMultiLineAnswer, holder.testQuestionFragmentBinding.longAnswer.tvCharCounter, 200);
            answerCharCounter(editText, holder.testQuestionFragmentBinding.longAnswer.tvCharCounter, 200);
        }

        if (questionNew.getQ_type().equals(Constant.MCQ)) {
            Log.d(TAG, "onBindViewHolder : " + questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceRootLayout.setVisibility(View.VISIBLE);

            holder.testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceQuestRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.shortAnswer.shortAnswerRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.longAnswer.multiLineQuestionRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.oneLineQuestionAns.oneLineAnsRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.fillInTheBlanks.fillInTheBlanksRootLayout.setVisibility(View.GONE);

            holder.testQuestionFragmentBinding.multiChoiceQuestion.tvQuestion.setText(questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.multiChoiceQuestion.ans1.setText(questionNew.getQ_mcq_op_1());
            holder.testQuestionFragmentBinding.multiChoiceQuestion.ans2.setText(questionNew.getQ_mcq_op_2());
            holder.testQuestionFragmentBinding.multiChoiceQuestion.ans3.setText(questionNew.getQ_mcq_op_3());
            holder.testQuestionFragmentBinding.multiChoiceQuestion.ans4.setText(questionNew.getQ_mcq_op_4());
            holder.testQuestionFragmentBinding.multiChoiceQuestion.ans5.setText(questionNew.getQ_mcq_op_5());
            mapTimer.put(testQuestions.get(poss).getTq_quest_seq_num(), testQuestionFragmentBinding.tvQuestTimer);
            createTimer(StartTestActivity.getCurrentItemPos(), 0, 0);
        }
        if (questionNew.getQ_type().equals(Constant.ONE_LINE_ANSWER)) {
            Log.d(TAG, "onBindViewHolder : " + questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.oneLineQuestionAns.oneLineAnsRootLayout.setVisibility(View.VISIBLE);

            holder.testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceQuestRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.shortAnswer.shortAnswerRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.longAnswer.multiLineQuestionRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.fillInTheBlanks.fillInTheBlanksRootLayout.setVisibility(View.GONE);
            //focusChangeListener(testQuestionFragmentBinding.oneLineQuestionAns.etAnswer);
            holder.testQuestionFragmentBinding.oneLineQuestionAns.tvQuestion.setText(questionNew.getQ_quest());
            mapTimer.put(testQuestions.get(poss).getTq_quest_seq_num(), testQuestionFragmentBinding.tvQuestTimer);
            createTimer(StartTestActivity.getCurrentItemPos(), 0, 0);
            holder.testQuestionFragmentBinding.oneLineQuestionAns.etAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));
            //holder.testQuestionFragmentBinding.oneLineQuestionAns.etAnswer.setText(questionNew.getTtqa_sub_ans());
            editText.setText(questionNew.getTtqa_sub_ans());
            answerCharCounter(editText, holder.testQuestionFragmentBinding.oneLineQuestionAns.tvCharCounter, 10);
            //answerCharCounter(holder.testQuestionFragmentBinding.oneLineQuestionAns.etAnswer, holder.testQuestionFragmentBinding.oneLineQuestionAns.tvCharCounter, 10);
        }

        if (questionNew.getQ_type().equals(Constant.Fill_THE_BLANKS)) {
            Log.d(TAG, "onBindViewHolder : " + questionNew.getQ_quest());
            holder.testQuestionFragmentBinding.fillInTheBlanks.fillInTheBlanksRootLayout.setVisibility(View.VISIBLE);

            holder.testQuestionFragmentBinding.multiChoiceQuestion.multiChoiceRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.singleChoiceQuestion.singleChoiceQuestRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.shortAnswer.shortAnswerRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.longAnswer.multiLineQuestionRootLayout.setVisibility(View.GONE);
            holder.testQuestionFragmentBinding.oneLineQuestionAns.oneLineAnsRootLayout.setVisibility(View.GONE);
            //focusChangeListener(testQuestionFragmentBinding.fillInTheBlanks.etAnswer);
            holder.testQuestionFragmentBinding.fillInTheBlanks.tvQuestion.setText(questionNew.getQ_quest());
            mapTimer.put(testQuestions.get(poss).getTq_quest_seq_num(), testQuestionFragmentBinding.tvQuestTimer);
            createTimer(StartTestActivity.getCurrentItemPos(), 0, 0);
            holder.testQuestionFragmentBinding.fillInTheBlanks.etAnswer.setKeyListener(DigitsKeyListener
                    .getInstance(" qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM,.'?!()*&amp;%^-_+=\\/;:{}[]<>@`\""));
            //holder.testQuestionFragmentBinding.fillInTheBlanks.etAnswer.setText(questionNew.getTtqa_sub_ans());
            editText.setText(questionNew.getTtqa_sub_ans());
            answerCharCounter(editText, holder.testQuestionFragmentBinding.fillInTheBlanks.tvCharCounter, 10);
            //answerCharCounter(holder.testQuestionFragmentBinding.fillInTheBlanks.etAnswer, holder.testQuestionFragmentBinding.fillInTheBlanks.tvCharCounter, 10);
        }

        testQuestionFragmentBinding.imgSave.setOnClickListener(v -> {
            Log.d(TAG, "posfd : " + position);
            int posss = StartTestActivity.viewPager.getCurrentItem();

            Log.d(TAG, "Log Before : " + StartTestActivity.viewPager.getCurrentItem());

            ToggleButton markToggleButton = mapImageMark.get(testQuestions.get(posss).getTq_quest_seq_num());

            if (markToggleButton.isChecked()) {
                //Toast.makeText(mContext, "Checked!!", Toast.LENGTH_SHORT).show();
                markToggleButton.setChecked(true);
                testQuestions.get(posss).setTtqa_marked(true);
                testQuestions.get(posss).setTtqa_visited(true);
                mapImageMark.put(testQuestions.get(poss).getTq_quest_seq_num(), markToggleButton);
            } else {
                //Toast.makeText(mContext, "UnChecked!!", Toast.LENGTH_SHORT).show();
                markToggleButton.setChecked(false);
                testQuestions.get(posss).setTtqa_marked(false);
                testQuestions.get(posss).setTtqa_visited(true);
                mapImageMark.put(testQuestions.get(poss).getTq_quest_seq_num(), markToggleButton);
            }
            //notifyDataSetChanged();
            notifyItemChanged(poss);
        });

        testQuestionFragmentBinding.imgReport.setOnClickListener(v -> {
            listener.onReportClick(position);
        });

        testQuestionFragmentBinding.btnNext.setOnClickListener(v -> {
            listener.onNextClick(position);
        });

        testQuestionFragmentBinding.btnPrevious.setOnClickListener(v -> {
            listener.onPreviousClick(position);
        });

        testQuestionFragmentBinding.btnReset.setOnClickListener(v -> {
            testQuestions.get(position).setTtqa_visited(true);
            resetAnswer(position);
        });
    }

    @Override
    public int getItemCount() {
        return testQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TestQuestionFragmentBinding testQuestionFragmentBinding;

        public ViewHolder(@NonNull TestQuestionFragmentBinding testQuestionFragmentBinding) {
            super(testQuestionFragmentBinding.getRoot());
            this.testQuestionFragmentBinding = testQuestionFragmentBinding;
        }
    }

    /*private void focusChangeListener(EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(mContext, "Got the focus", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });
    }*/

    private void createTimer(int position, int seconds, int minutes) {
        TextView timer = mapTimer.get(String.valueOf(position));
        if (timer != null) {
            countDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
                int timerCountSeconds = seconds;
                int timerCountMinutes = minutes;

                public void onTick(long millisUntilFinished) {
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
                    timer.setText("00:00");//todo onFinish Action Call
                }
            }.start();
        }
    }

   /* private void startTimer(int exactPs) {
        for (int i = 0; i < testQuestions.size(); i++) {
            CountDownTimer countDownTimer = mapTimer.get(testQuestions.get(i).getTq_quest_seq_num());
            if (testQuestions.get(i).getTq_quest_seq_num().equals(testQuestions.get(exactPs).getTq_quest_seq_num())) {
                if (countDownTimer != null)
                    countDownTimer.start();
            } else {
                if (countDownTimer != null)
                    countDownTimer.cancel();
            }
        }
    }*/


    private void answerCharCounter(EditText etAnswer, TextView tvCounter, int maxWordLength) {

        int qPos = StartTestActivity.viewPager.getCurrentItem();

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

                    if (wordCount > 0) {
                        Log.d(TAG, "Subjective Ans pos " + qPos);
                        testQuestions.get(qPos).setTtqa_sub_ans(s.toString());
                        testQuestions.get(qPos).setTtqa_attempted(true);
                        testQuestions.get(qPos).setTtqa_visited(false);
                    } else {
                        testQuestions.get(qPos).setTtqa_sub_ans("");
                        testQuestions.get(qPos).setTtqa_attempted(false);
                        testQuestions.get(qPos).setTtqa_visited(true);
                    }
                }

                if (wordCount > maxWordLength) {
                    etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length())});
                }
                tvCounter.setText("Words Remaining : " + (maxWordLength - wordCount + "/" + String.valueOf(maxWordLength)));
            }
        });
    }

    private void resetAnswer(int pos) {
        TestQuestionNew question = testQuestions.get(StartTestActivity.getCurrentItemPos());
        question.setTtqa_marked(false);
        testQuestionFragmentBinding.imgSave.setChecked(false);

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

        notifyItemChanged(StartTestActivity.getCurrentItemPos());
    }

    public interface StartAdapterButtonClickListener {
        void onPreviousClick(int pos);

        void onNextClick(int pos);

        void onReportClick(int pos);
    }
}
