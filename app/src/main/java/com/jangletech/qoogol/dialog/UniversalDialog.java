package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.LayoutUniversalDialogBinding;

public class UniversalDialog extends Dialog {

    private LayoutUniversalDialogBinding layoutUniversalDialogBinding;
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

    public UniversalDialog(@NonNull Context context,
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
        layoutUniversalDialogBinding = DataBindingUtil.setContentView(getOwnerActivity(),R.layout.layout_universal_dialog);

        layoutUniversalDialogBinding.tvTitle.setText(strTitle);
        layoutUniversalDialogBinding.tvMessage.setText(strMsg);

        //CallBack For Negative Button
        layoutUniversalDialogBinding.tvPositiveBtn.setOnClickListener(v->{
            dismiss();
            buttonclickListener.onPositiveButtonClick();

        });
        layoutUniversalDialogBinding.tvNegativeBtn.setOnClickListener(v-> dismiss());

    }

    public interface DialogButtonClickListener {
        void onPositiveButtonClick();
    }


}
