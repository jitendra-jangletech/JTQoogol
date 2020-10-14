package com.jangletech.qoogol.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogAddNewSectionBinding;
import com.jangletech.qoogol.util.TinyDB;

public class AddNewSectionDialog extends DialogFragment implements TextWatcher {

    private static final String TAG = "AddNewSectionDialog";
    private DialogAddNewSectionBinding mBinding;
    private AddNewSectionClickListener listener;
    private String[] sectionsAvailable;
    private String sectionName = "None";

    public AddNewSectionDialog(AddNewSectionClickListener listener) {
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
        setSections();
        mBinding.etSectionName.addTextChangedListener(this);
        mBinding.etSectionMarks.addTextChangedListener(this);
        mBinding.btnSave.setOnClickListener(v -> {
            if (mBinding.etSectionName.getText().toString().trim().isEmpty()) {
                mBinding.tilSectionName.setError("Enter Section Name");
                return;
            } else if (mBinding.tilSectionMarks.getVisibility() == View.VISIBLE &&
                    mBinding.etSectionMarks.getText().toString().trim().isEmpty()) {
                mBinding.tilSectionMarks.setError("Enter Section Marks");
                return;
            } else {
                String name = mBinding.etSectionName.getText().toString().trim();
                int marks = Integer.parseInt(mBinding.etSectionMarks.getText().toString().trim());
                listener.onSaveClick(name, marks);
                dismiss();
            }
        });

        mBinding.sectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton != null) {
                    sectionName = radioButton.getText().toString();
                }
            }
        });

        mBinding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void setSections() {
        if (TinyDB.getInstance(getActivity()).getString("SECTIONS") != null ||
                !TinyDB.getInstance(getActivity()).getString("SECTIONS").isEmpty()) {
            sectionsAvailable = TinyDB.getInstance(getActivity()).getString("SECTIONS").split(",", -1);

            for (int i = 0; i < sectionsAvailable.length; i++) {
                Log.i(TAG, "setSections: " + sectionsAvailable[i]);
                if (i == 0) {
                    if (!sectionsAvailable[i].isEmpty())
                        mBinding.section1.setVisibility(View.GONE);
                    mBinding.section1.setText(sectionsAvailable[i]);
                }
                if (i == 1) {
                    if (!sectionsAvailable[i].isEmpty())
                        mBinding.section2.setVisibility(View.GONE);

                    mBinding.section2.setText(sectionsAvailable[i]);
                }
                if (i == 2) {
                    if (!sectionsAvailable[i].isEmpty())
                        mBinding.section3.setVisibility(View.GONE);

                    mBinding.section3.setText(sectionsAvailable[i]);
                }
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() > 0) {
            mBinding.tilSectionName.setError(null);
            mBinding.tilSectionMarks.setError(null);
        }
    }

    public interface AddNewSectionClickListener {
        void onSaveClick(String name, int marks);
    }
}
