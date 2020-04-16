package com.jangletech.qoogol.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.CourseActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FillInTheBlanksLayoutBinding;
import com.jangletech.qoogol.databinding.MultiChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.MultiLineQuestAnsBinding;
import com.jangletech.qoogol.databinding.SingleChoiceQuestionBinding;
import com.jangletech.qoogol.databinding.TrueFalseLayoutBinding;
import com.jangletech.qoogol.dialog.QuestReportDialog;
import com.jangletech.qoogol.model.Answer;
import com.jangletech.qoogol.model.TestQuestion;
import com.jangletech.qoogol.enums.QuestionType;

import java.util.HashMap;
import java.util.List;

public class ViewPagerAdapter{

   /* private static final String TAG = "ViewPagerAdapter";
    private Context mContext;

    private List<TestQuestion> testQuestions;
    private SingleChoiceQuestionBinding singleChoiceQuestionBinding;
    private MultiChoiceQuestionBinding multiChoiceQuestionBinding;
    private FillInTheBlanksLayoutBinding fillInTheBlanksLayoutBinding;
    private TrueFalseLayoutBinding trueFalseLayoutBinding;
    private MultiLineQuestAnsBinding multiLineQuestAnsBinding;
    private HashMap<Integer, Chip> mapScqChips = new HashMap();
    private HashMap<Integer, Chip> mapMcqChips = new HashMap();
    private HashMap<Integer, Chip> mapTrueFalseChips = new HashMap();

    public ViewPagerAdapter(Context context, List<TestQuestion> testQuestionList) {
        mContext = context;
        testQuestions = testQuestionList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return testQuestions.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.SCQ.toString())) {
            singleChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.single_choice_question, container, false);
            container.addView(singleChoiceQuestionBinding.getRoot());
            setScqQuestionLayout(singleChoiceQuestionBinding, position);
            return singleChoiceQuestionBinding.getRoot();
        }

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.MCQ.toString())) {
            multiChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.multi_choice_question, container, false);
            container.addView(multiChoiceQuestionBinding.getRoot());
            setMcqQuestionLayout(multiChoiceQuestionBinding, position);
            return multiChoiceQuestionBinding.getRoot();
        }

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.TRUE_FALSE.toString())) {
            trueFalseLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.true_false_layout, container, false);
            container.addView(trueFalseLayoutBinding.getRoot());
            setTrueFalseLayout(trueFalseLayoutBinding, position);
            return trueFalseLayoutBinding.getRoot();
        }

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.FILL_THE_BLANKS.toString())) {
            fillInTheBlanksLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fill_in_the_blanks_layout, container, false);
            container.addView(fillInTheBlanksLayoutBinding.getRoot());
            setFillTheBlanksLayout(fillInTheBlanksLayoutBinding, position);
            return fillInTheBlanksLayoutBinding.getRoot();
        }

        if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.MULTI_LINE_ANSWER.toString())) {
            multiLineQuestAnsBinding = DataBindingUtil.inflate(inflater, R.layout.multi_line_quest_ans, container, false);
            container.addView(multiLineQuestAnsBinding.getRoot());
            setMultiLineQuestAnsLayout(multiLineQuestAnsBinding, position);
            return multiLineQuestAnsBinding.getRoot();
        }

        return null;

    }

    private void setMultiLineQuestAnsLayout(MultiLineQuestAnsBinding multiLineQuestAnsBinding, int position) {
        setTimer(multiLineQuestAnsBinding.tvtimer, 0, 0);
        answerCharCounter(multiLineQuestAnsBinding.etMultiLineAnswer, multiLineQuestAnsBinding.tvCharCounter, 200);
        multiLineQuestAnsBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
        multiLineQuestAnsBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
    }

    private void setFillTheBlanksLayout(FillInTheBlanksLayoutBinding fillInTheBlanksBinding, int position) {
        setTimer(fillInTheBlanksBinding.tvtimer, 0, 0);
        answerCharCounter(fillInTheBlanksBinding.etAnswer, fillInTheBlanksBinding.tvCharCounter, 10);
        fillInTheBlanksBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
        fillInTheBlanksBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
    }

    private void setTrueFalseLayout(TrueFalseLayoutBinding trueFalseLayoutBinding, int position) {
        setTimer(trueFalseLayoutBinding.tvQuestTimer, 0, 0);
        trueFalseLayoutBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
        trueFalseLayoutBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
        setTrueFalseAnswer(trueFalseLayoutBinding.trueFalseAnswerChipGrp);
        trueFalseLayoutBinding.trueFalseAnswerChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                mapTrueFalseChips.put(id, chip);
                //Toast.makeText(mContext, "" + id, Toast.LENGTH_SHORT).show();
                setCheckedChip(mapTrueFalseChips);
            }
        });
    }

    private void setScqQuestionLayout(SingleChoiceQuestionBinding mBinding, int position) {
        setTimer(mBinding.tvQuestTimer, 0, 0);
        mBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
        mBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
        setChipAnswers(mBinding.singleChoiceAnswerGrp, testQuestions.get(position).getAnswerList(), QuestionType.SCQ.toString());
        mBinding.singleChoiceAnswerGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                mapScqChips.put(id, chip);
                //Toast.makeText(mContext, "" + id, Toast.LENGTH_SHORT).show();
                setCheckedChip(mapScqChips);
            }
        });

        *//*mBinding.imgReport.setOnClickListener(v->{
            QuestReportDialog questReportDialog = new QuestReportDialog(mContext,this);
            questReportDialog.show();
        });*//*

    }

    private void setMcqQuestionLayout(MultiChoiceQuestionBinding multiChoiceQuestionBinding, int position) {
        setTimer(multiChoiceQuestionBinding.tvQuestTimer, 0, 0);
        multiChoiceQuestionBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
        multiChoiceQuestionBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
        setChipAnswers(multiChoiceQuestionBinding.multiChoiceAnswerGrp, testQuestions.get(position).getAnswerList(), QuestionType.SCQ.toString());
        multiChoiceQuestionBinding.multiChoiceAnswerGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            setCheckedChip(mapMcqChips);

        });
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
                timer.setText("done!");
            }
        }.start();
    }

    private void setChipAnswers(ChipGroup chipGroup, List<Answer> answers, String questType) {
        chipGroup.removeAllViews();
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            Chip chip = (Chip) LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.answer_chip, chipGroup, false);
            chip.setText(answer.getAnswer());
            chip.setOnClickListener(this);
            if (QuestionType.SCQ.toString().equals(questType)) {
                chip.setTag(QuestionType.SCQ);
                mapScqChips.put(i, chip);
            }
            if (QuestionType.MCQ.toString().equals(questType)) {
                chip.setTag(QuestionType.MCQ);
                mapMcqChips.put(i, chip);
            }
            chip.setId(i);
            chipGroup.addView(chip);
        }
    }

    private void setTrueFalseAnswer(ChipGroup chipGroup) {
        String[] trueFalse = {"True", "False"};
        chipGroup.removeAllViews();
        for (int i = 0; i < trueFalse.length; i++) {
            Chip chip = (Chip) LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.true_false_chip, chipGroup, false);
            chip.setText(trueFalse[i]);
            chip.setTag(QuestionType.TRUE_FALSE.toString());
            chip.setOnClickListener(this);
            chip.setId(i);
            mapTrueFalseChips.put(i, chip);
            chipGroup.addView(chip);
        }
    }

    private void setCheckedChip(HashMap<Integer, Chip> map) {
        for (int i = 0; i < map.size(); i++) {
            if (map.get(i).isChecked()) {
                map.get(i).setTextColor(Color.WHITE);
            } else {
                map.get(i).setTextColor(Color.BLACK);
            }
        }
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
                    wordCount = words.length;
                }

                if (wordCount == maxWordLength) {
                    etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length())});
                }
                Toast.makeText(mContext, "Word count is: " + wordCount, Toast.LENGTH_SHORT).show();
                tvCounter.setText(maxWordLength - wordCount + "/" + String.valueOf(maxWordLength));
            }
        });
    }

    @Override
    public void onClick(View v) {
        Chip chip = (Chip) v;
        if (chip != null && chip.getTag() != null) {
            if (chip.getTag().toString().equalsIgnoreCase(QuestionType.SCQ.toString())) {
                mapScqChips.put(chip.getId(), chip);
                setCheckedChip(mapScqChips);
            }
            if (chip.getTag().toString().equalsIgnoreCase(QuestionType.MCQ.toString())) {
                mapMcqChips.put(chip.getId(), chip);
                setCheckedChip(mapMcqChips);
            }

            if (chip.getTag().toString().equalsIgnoreCase(QuestionType.TRUE_FALSE.toString())) {
                mapTrueFalseChips.put(chip.getId(), chip);
                setCheckedChip(mapTrueFalseChips);
            }
        }
    }*/
}
