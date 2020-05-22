package com.jangletech.qoogol.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.databinding.MultiLineQuestAnsBinding;
import com.jangletech.qoogol.dialog.QuestReportDialog;
import com.jangletech.qoogol.model.TestQuestionNew;

public class LongAnsFragment extends BaseFragment implements QuestReportDialog.QuestReportDialogListener{
    private static final String ARG_COUNT = "param1";
    private Integer counter;
    private MultiLineQuestAnsBinding mBinding;
    private QuestReportDialog questReportDialog;

    public static LongAnsFragment newInstance(Integer counter) {
        LongAnsFragment fragment = new LongAnsFragment();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.multi_line_quest_ans, container, false);
        initView(mBinding);
        return mBinding.getRoot();
    }

    private void initView(MultiLineQuestAnsBinding mBinding) {
        TestQuestionNew testQuestionNew = StartTestActivity.testQuestionList.get(counter);
        mBinding.tvQuestNo.setText(testQuestionNew.getTq_quest_seq_num());
        mBinding.tvQuestion.setText(testQuestionNew.getQ_quest());
        mBinding.etMultiLineAnswer.setText(testQuestionNew.getTtqa_sub_ans());
        setTimer(mBinding.tvQuestTimer,0,0);
        if(testQuestionNew.isTtqa_marked()){
            mBinding.imgSave.setChecked(true);
        }else{
            mBinding.imgSave.setChecked(false);
        }

        mBinding.imgSave.setOnClickListener(v->{
            if(mBinding.imgSave.isChecked()){
                testQuestionNew.setTtqa_marked(true);
            }else{
                testQuestionNew.setTtqa_marked(false);
            }
        });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return null;
            }
        };

        mBinding.etMultiLineAnswer.addTextChangedListener(new TextWatcher() {
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
                        testQuestionNew.setTtqa_sub_ans(s.toString());
                        testQuestionNew.setTtqa_attempted(true);
                        testQuestionNew.setTtqa_visited(false);
                    } else {
                        testQuestionNew.setTtqa_sub_ans("");
                        testQuestionNew.setTtqa_attempted(false);
                        testQuestionNew.setTtqa_visited(true);
                    }
                }

                if (wordCount > 200) {
                    mBinding.etMultiLineAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length())});
                }
                mBinding.tvCharCounter.setText("Words Remaining : " + (200 - wordCount + "/" + String.valueOf(200)));
            }
        });

        mBinding.imgReport.setOnClickListener(v->{
            questReportDialog = new QuestReportDialog(getContext(), this,counter);
            questReportDialog.show();
        });

        mBinding.btnNext.setOnClickListener(v->{
            //showToast("Next");
            StartTestActivity.viewPager.setCurrentItem(StartTestActivity.viewPager.getCurrentItem()+1,true);
        });
        mBinding.btnPrevious.setOnClickListener(v->{
            //showToast("Previous");
            StartTestActivity.viewPager.setCurrentItem(StartTestActivity.viewPager.getCurrentItem()-1,true);
        });
        mBinding.btnReset.setOnClickListener(v->{
            //showToast("Reset");
            mBinding.etMultiLineAnswer.setText("");
            testQuestionNew.setTtqa_visited(true);
            testQuestionNew.setTtqa_sub_ans("");
            testQuestionNew.setTtqa_marked(false);
            testQuestionNew.setTtqa_attempted(false);
            if(mBinding.imgSave.isChecked()){
                mBinding.imgSave.setChecked(false);
            }
        });
    }

    @Override
    public void onReportQuestSubmitClick(int pos) {
        showToast("Question No : "+StartTestActivity.testQuestionList.get(pos).getTq_quest_seq_num()+" Reported successfully.");
        questReportDialog.dismiss();
    }
}
