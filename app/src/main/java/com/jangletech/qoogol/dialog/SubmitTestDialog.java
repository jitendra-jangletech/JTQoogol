package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.DialogSubmitTestBinding;
import com.jangletech.qoogol.model.TestQuestionNew;

import java.util.List;

public class SubmitTestDialog extends Dialog {

    private static final String TAG = "SubmitTestDialog";
    private DialogSubmitTestBinding mBinding;
    private SubmitDialogClickListener submitDialogClickListener;
    private Long milliLeft, min, sec, hrs;
    private CountDownTimer timer;
    private String testName;
    private List<TestQuestionNew> questionList;
    private int totalQuest, wrongQuest, unAttemptedQuest, markedQuest;


    public SubmitTestDialog(@NonNull Context context, SubmitDialogClickListener submitDialogClickListener, String testName) {
        super(context);
        this.submitDialogClickListener = submitDialogClickListener;
        this.testName = testName;
        this.questionList = PracticeTestActivity.questionsNewList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_submit_test, null, false);
        setContentView(mBinding.getRoot());

        mBinding.tvYes.setOnClickListener(v -> {
            dismiss();
            submitDialogClickListener.onYesClick();
        });

        mBinding.tvNo.setOnClickListener(v -> {
            dismiss();
            submitDialogClickListener.onNoClick();
        });

        mBinding.wrongLayout.setOnClickListener(v -> {
            submitDialogClickListener.onWrongClick();
            dismiss();
        });

        mBinding.unattemptedLayout.setOnClickListener(v -> {
            submitDialogClickListener.onUnAttemptedClick();
            dismiss();
        });

        mBinding.markedLayout.setOnClickListener(v -> {
            submitDialogClickListener.onMarkedClick();
            dismiss();
        });

        setQuestCounts();
    }

    private void setQuestCounts() {
        double totalMarks = 0;
        double obtainMarks = 0;
        totalQuest = questionList.size();
        markedQuest = 0;
        wrongQuest = 0;
        unAttemptedQuest = 0;


        for (TestQuestionNew testQuestionNew : questionList) {
            totalMarks = totalMarks + testQuestionNew.getTq_marks();
            if (testQuestionNew.isTtqa_attempted() && !testQuestionNew.isAnsweredRight()) {
                wrongQuest++;
            }
            if (testQuestionNew.isTtqa_marked()) {
                markedQuest++;
            }
            if (!testQuestionNew.isTtqa_attempted()) {
                unAttemptedQuest++;
            }

            if (testQuestionNew.isAnsweredRight()) {
                obtainMarks = obtainMarks + testQuestionNew.getTq_marks();
                Log.d(TAG, "marksCalculation Right Answer : " + testQuestionNew.getTq_quest_seq_num());
            } else {
                Log.d(TAG, "marksCalculation Wrong Answer : " + testQuestionNew.getTq_quest_seq_num());
            }

        }

        Log.d(TAG, "setQuestCounts Wrong : " + wrongQuest);
        Log.d(TAG, "setQuestCounts UnAttempted : " + unAttemptedQuest);
        Log.d(TAG, "setQuestCounts Marked : " + markedQuest);

        //Dialog Title Test name
        mBinding.tvTitle.setText(testName);

        //set obtain Marks & Total Marks
        mBinding.tvTotalMarksValue.setText(String.valueOf(totalMarks));
        mBinding.tvObtainMarksValue.setText(String.valueOf(obtainMarks));

        mBinding.tvWrongCount.setText(Html.fromHtml("<u>" + String.valueOf(wrongQuest) + "</u>"));
        mBinding.tvUnAttemptedCount.setText(Html.fromHtml("<u>" + String.valueOf(unAttemptedQuest) + "</u>"));
        mBinding.tvMarkedCount.setText(Html.fromHtml("<u>" + String.valueOf(markedQuest) + "</u>"));
    }


//    public void startTimer(long timeLengthMilli) {
//        timer = new CountDownTimer(timeLengthMilli, 1000) {
//            @Override
//            public void onTick(long milliTillFinish) {
//                milliLeft = milliTillFinish;
//                hrs = (milliTillFinish / (1000 * 60 * 60));
//                min = ((milliTillFinish / (1000 * 60)) - hrs * 60);
//                sec = ((milliTillFinish / 1000) - min * 60);
//                String time = String.format("%02d:%02d:%02d", hrs, min, sec);
//                mBinding.tvTimerCount.setText(time);
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        }.start();
//    }

    public interface SubmitDialogClickListener {
        void onYesClick();

        void onNoClick();

        void onWrongClick();

        void onUnAttemptedClick();

        void onMarkedClick();
    }
}
