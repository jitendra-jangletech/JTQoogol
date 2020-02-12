package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.LayoutUniversalDialogBinding;

public class UniversalDialog extends Dialog {

    //private LayoutUniversalDialogBinding mBinding;
    private Activity activity;
    private Context context;
    private String strTitle;
    private String strMsg;
    private String strPositiveText;
    private String strNegativeText;

    public UniversalDialog(@NonNull Context context,Activity activity,
                           String title,String msg,String positiveText,String negativeText) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.strTitle = title;
        this.strMsg = msg;
        this.strPositiveText = positiveText;
        this.strNegativeText = negativeText;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_universal_dialog);
        //findViewById(R.id.t).setText(strTitle);
        //tvMessage.setText(strMsg);
        //mBinding.tvPositiveBtn.setText(strPositiveText);
        //mBinding.tvNegativeBtn.setText(strNegativeText);
    }


}
