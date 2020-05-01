package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogSubmitTestBinding;
import com.jangletech.qoogol.model.TestQuestion;

public class SubmitTestDialog extends Dialog {

    private DialogSubmitTestBinding mBinding;
    private SubmitDialogClickListener submitDialogClickListener;
    Long milliLeft, min, sec;
    CountDownTimer timer;

    public SubmitTestDialog(@NonNull Context context,SubmitDialogClickListener submitDialogClickListener,long milliesLeft) {
        super(context);
        this.submitDialogClickListener = submitDialogClickListener;
        this.milliLeft = milliesLeft;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_submit_test, null, false);
        setContentView(mBinding.getRoot());
        startTimer(milliLeft);
        //setData();

        mBinding.tvYes.setOnClickListener(v->{
            submitDialogClickListener.onYesClick();
        });

        mBinding.tvNo.setOnClickListener(v->{
            submitDialogClickListener.onNoClick();
        });
    }

    public void startTimer(long timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 1000) {

            @Override
            public void onTick(long milliTillFinish) {
                milliLeft = milliTillFinish;
                min = (milliTillFinish / (1000 * 60));
                sec = ((milliTillFinish / 1000) - min * 60);
                String time = String.format("%02d:%02d", min, sec);
                mBinding.tvTimerCount.setText(time);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    /*private void setData(){
        if(StartTestActivity.testQuestionList!=null){
            int totalQuestCount = StartTestActivity.testQuestionList.size();
            int attemptedCount = 0;
            int markedQuestCount = 0;
            int unattemptedQuestCount = 0;
            for (TestQuestion question: StartTestActivity.testQuestionList) {
                if(question.isAttempted()){
                    attemptedCount++;
                }
                if(question.isMarked()){
                    markedQuestCount++;
                }
            }
            unattemptedQuestCount = totalQuestCount - attemptedCount;
            mBinding.tvAttemptedCount.setText(String.valueOf(attemptedCount));
            mBinding.tvUnAttemptedCount.setText(String.valueOf(unattemptedQuestCount));
            mBinding.tvMarkedCount.setText(String.valueOf(markedQuestCount));
        }
    }*/

    public interface SubmitDialogClickListener{
        void onYesClick();
        void onNoClick();
    }
}
