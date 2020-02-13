package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jangletech.qoogol.R;

public class UniversalDialog extends Dialog {

    public DialogButtonClickListener buttonclickListener;
    private Context context;
    private String strTitle;
    private String strMsg;
    private String strPositiveText;
    private String strNegativeText;

    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvPositiveTxt;
    private TextView tvNegativeTxt;

    public UniversalDialog(@NonNull Context context, Activity activity,
                           String title, String msg, String positiveText, String negativeText,
                           DialogButtonClickListener buttonclickListener) {
        super(context);
        this.context = context;
        this.strTitle = title;
        this.strMsg = msg;
        this.strPositiveText = positiveText;
        this.strNegativeText = negativeText;
        this.buttonclickListener = buttonclickListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_universal_dialog);
        tvTitle = findViewById(R.id.tv_title);
        tvMessage = findViewById(R.id.tvMessage);
        tvPositiveTxt = findViewById(R.id.tvPositiveBtn);
        tvNegativeTxt = findViewById(R.id.tvNegativeBtn);

        //CallBack For Negative Button
        tvPositiveTxt.setOnClickListener(v->{
            dismiss();
            buttonclickListener.onPositiveButtonClick();

        });
        tvNegativeTxt.setOnClickListener(v-> dismiss());

    }

    public interface DialogButtonClickListener {
        void onPositiveButtonClick();
        //void onNegativeButtonClick();
    }


}
