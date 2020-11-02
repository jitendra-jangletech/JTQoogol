package com.jangletech.qoogol.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogSaveFileBinding;

public class SaveFileDialog extends DialogFragment {

    private static final String TAG = "SaveFileDialog";
    private DialogSaveFileBinding mBinding;
    private SaveFileClickListener listener;
    private String fileName = "";

    public SaveFileDialog(SaveFileClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog_Alert);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_save_file, container, false);
        setCancelable(false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated File Name : " + fileName);

        mBinding.btnSave.setOnClickListener(v -> {
            fileName = mBinding.etFileName.getText().toString().trim();
            if (fileName.isEmpty()) {
                mBinding.tilFileName.setError("Please Enter File Name.");
            } else {
                listener.onSaveClick(fileName);
                dismiss();
            }
        });

        mBinding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.etFileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "afterTextChanged : " + s.toString());
                if (s.toString().length() > 0) {
                    mBinding.tilFileName.setError(null);
                }
            }
        });
    }

    public interface SaveFileClickListener {
        void onSaveClick(String name);
    }
}
