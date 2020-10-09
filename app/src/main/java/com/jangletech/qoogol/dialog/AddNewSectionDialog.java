package com.jangletech.qoogol.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogAddNewSectionBinding;

public class AddNewSectionDialog extends DialogFragment implements TextWatcher {

    private DialogAddNewSectionBinding mBinding;
    private AddNewSectionClickListener listener;

    public AddNewSectionDialog(AddNewSectionClickListener listener){
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_new_section, container, false);
        getDialog().setCancelable(false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.etSectionName.addTextChangedListener(this);
        mBinding.etSectionMarks.addTextChangedListener(this);
        mBinding.btnSave.setOnClickListener(v -> {
            if (mBinding.etSectionName.getText().toString().trim().isEmpty()) {
                mBinding.tilSectionName.setError("Enter Section Name");
                return;
            } else if (mBinding.etSectionMarks.getText().toString().trim().isEmpty()) {
                mBinding.tilSectionMarks.setError("Enter Section Marks");
                return;
            } else {
                String name = mBinding.etSectionName.getText().toString().trim();
                int marks = Integer.parseInt(mBinding.etSectionMarks.getText().toString().trim());
                listener.onSaveClick(name, marks);
                dismiss();
            }
        });

        mBinding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.toString().length() > 0){
            mBinding.tilSectionName.setError(null);
            mBinding.tilSectionMarks.setError(null);
        }
    }

    public interface AddNewSectionClickListener {
        void onSaveClick(String name, int marks);
    }
}
