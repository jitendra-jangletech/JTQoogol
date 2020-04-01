package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.LayoutChangePasswordBinding;

public class ChangePasswordDialog extends Dialog {

    public ChangeDialogClickListener changeDialogClickListener;
    private LayoutChangePasswordBinding layoutChangePasswordBinding;


    public ChangePasswordDialog(@NonNull Context context, ChangeDialogClickListener changeDialogClickListener) {
      super(context,android.R.style.Theme_Light);
        this.changeDialogClickListener = changeDialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        layoutChangePasswordBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_change_password, null, false);
        setContentView(layoutChangePasswordBinding.getRoot());

        layoutChangePasswordBinding.btnSubmit.setOnClickListener(v -> {

            if (layoutChangePasswordBinding.etOldPassword.getText().toString().trim().isEmpty()) {
                layoutChangePasswordBinding.tilOldPassword.setError("Enter old password");
                return;
            } else if (layoutChangePasswordBinding.etNewPassword.getText().toString().trim().isEmpty()) {
                layoutChangePasswordBinding.tilNewPassword.setError("Enter new password");
                return;
            } else {
                changeDialogClickListener.onSubmitClick(
                        layoutChangePasswordBinding.etOldPassword.getText().toString().trim(),
                        layoutChangePasswordBinding.etNewPassword.getText().toString().trim()
                );
            }
        });
        //layoutChangePasswordBinding.btnCancel.setOnClickListener(v -> dismiss());

    }

    public interface ChangeDialogClickListener {
        void onSubmitClick(String oldPwd,String newPwd);
    }
}
