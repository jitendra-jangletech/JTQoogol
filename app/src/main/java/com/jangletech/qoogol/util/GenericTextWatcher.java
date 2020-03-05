package com.jangletech.qoogol.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.SignInActivity;
import com.jangletech.qoogol.SignUpActivity;

import java.util.Objects;

public class GenericTextWatcher implements TextWatcher {

    private TextInputLayout view;
    private Context mContext;

    public GenericTextWatcher(TextInputLayout view, Context mContext) {
        this.view = view;
        this.mContext = mContext;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void afterTextChanged(Editable editable) {

        if (mContext instanceof SignUpActivity) {
            if (view.getId() == R.id.til_first_name)
                if (TextUtils.isEmpty(Objects.requireNonNull(view.getEditText()).getText().toString().trim())) {
                    view.setError(mContext.getResources().getString(R.string.empty_first_name));
                } else {
                    view.setError(null);
                    hideErrorSpace(view);
                }
            else if (view.getId() == R.id.til_last_name) {
                if (TextUtils.isEmpty(Objects.requireNonNull(view.getEditText()).getText().toString().trim())) {
                    view.setError(mContext.getResources().getString(R.string.empty_last_name));
                } else {
                    view.setError(null);
                    hideErrorSpace(view);
                }
            } else if (view.getId() == R.id.til_mobile) {
                if (TextUtils.isEmpty(Objects.requireNonNull(view.getEditText()).getText().toString().trim())) {
                    view.setError(mContext.getResources().getString(R.string.empty_mobile));
                } else if (view.getEditText().getText().length() < 10) {
                    view.setError(mContext.getResources().getString(R.string.empty_mobile));
                } else {
                    view.setError(null);
                    hideErrorSpace(view);
                }
            } else if (view.getId() == R.id.til_email) {
                if (TextUtils.isEmpty(Objects.requireNonNull(view.getEditText()).getText().toString().trim())
                        || !isEmailValid(view.getEditText().getText().toString().trim())) {
                    view.setError(mContext.getResources().getString(R.string.empty_email));
                } else {
                    view.setError(null);
                    hideErrorSpace(view);
                }
            } else if (view.getId() == R.id.til_create_password) {
                if (TextUtils.isEmpty(Objects.requireNonNull(view.getEditText()).getText().toString().trim())) {
                    view.setError(mContext.getResources().getString(R.string.empty_password));
                } else {
                    view.setError(null);
                    hideErrorSpace(view);
                }
            }

        }

        if (mContext instanceof SignInActivity) {
            if (view.getId() == R.id.tilEmail) {
                if (TextUtils.isEmpty(Objects.requireNonNull(view.getEditText()).getText().toString().trim())
                        || !isEmailValid(view.getEditText().getText())) {
                    view.setError(mContext.getResources().getString(R.string.empty_email));
                } else {
                    view.setError(null);
                    hideErrorSpace(view);
                }
            } else if (view.getId() == R.id.tilPassword) {
                if (TextUtils.isEmpty(Objects.requireNonNull(view.getEditText()).getText().toString().trim())) {
                    view.setError(mContext.getResources().getString(R.string.empty_password));
                } else {
                    view.setError(null);
                    hideErrorSpace(view);
                }
            }
        }
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches();
    }

    private void hideErrorSpace(TextInputLayout view) {
        if (view.isErrorEnabled()) {
            view.setErrorEnabled(false);
        }
    }
}
