package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogSubmitTestBinding;

public class SubmitTestDialog extends Dialog {

    private DialogSubmitTestBinding mBinding;
    private SubmitDialogClickListener submitDialogClickListener;
    Long milliLeft, min, sec,hrs;
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

    public interface SubmitDialogClickListener{
        void onYesClick();
        void onNoClick();
    }
}
