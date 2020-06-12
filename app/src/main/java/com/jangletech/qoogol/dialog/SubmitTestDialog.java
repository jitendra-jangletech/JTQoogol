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
    private List<TestQuestionNew> questionList;
    private int totalQuest, attemptedQuest, unAttemptedQuest, markedQuest;


    public SubmitTestDialog(@NonNull Context context, SubmitDialogClickListener submitDialogClickListener, long milliesLeft) {
        super(context);
        this.submitDialogClickListener = submitDialogClickListener;
        this.milliLeft = milliesLeft;
        this.questionList = PracticeTestActivity.questionsNewList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_submit_test, null, false);
        setContentView(mBinding.getRoot());
        startTimer(milliLeft);

        mBinding.tvYes.setOnClickListener(v -> {
            submitDialogClickListener.onYesClick();
        });

        mBinding.tvNo.setOnClickListener(v -> {
            submitDialogClickListener.onNoClick();
        });

        mBinding.attemptedLayout.setOnClickListener(v -> {
            submitDialogClickListener.onAttemptedClick();
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
        totalQuest = questionList.size();
        markedQuest = 0;
        attemptedQuest = 0;
        unAttemptedQuest = 0;
        for (int i = 0; i < questionList.size(); i++) {
            TestQuestionNew testQuestionNew = questionList.get(i);
            if (testQuestionNew.isTtqa_attempted()) {
                attemptedQuest++;
            }
            if (testQuestionNew.isTtqa_marked()) {
                markedQuest++;
            }

            if (!testQuestionNew.isTtqa_attempted()) {
                unAttemptedQuest++;
            }
        }

        Log.d(TAG, "setQuestCounts Attempted : " + attemptedQuest);
        Log.d(TAG, "setQuestCounts UnAttempted : " + unAttemptedQuest);
        Log.d(TAG, "setQuestCounts Marked : " + markedQuest);

        mBinding.tvAttemptedCount.setText(Html.fromHtml("<u>" + String.valueOf(attemptedQuest) + "</u>"));
        mBinding.tvUnAttemptedCount.setText(Html.fromHtml("<u>" + String.valueOf(unAttemptedQuest) + "</u>"));
        mBinding.tvMarkedCount.setText(Html.fromHtml("<u>" + String.valueOf(markedQuest) + "</u>"));
    }


    public void startTimer(long timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 1000) {
            @Override
            public void onTick(long milliTillFinish) {
                milliLeft = milliTillFinish;
                hrs = (milliTillFinish / (1000 * 60 * 60));
                min = ((milliTillFinish / (1000 * 60)) - hrs * 60);
                sec = ((milliTillFinish / 1000) - min * 60);
                String time = String.format("%02d:%02d:%02d", hrs, min, sec);
                mBinding.tvTimerCount.setText(time);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public interface SubmitDialogClickListener {
        void onYesClick();

        void onNoClick();

        void onAttemptedClick();

        void onUnAttemptedClick();

        void onMarkedClick();
    }
}
