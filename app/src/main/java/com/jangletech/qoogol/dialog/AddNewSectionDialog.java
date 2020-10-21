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

import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogAddNewSectionBinding;
import com.jangletech.qoogol.model.TestSubjectChapterMaster;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

public class AddNewSectionDialog extends DialogFragment implements TextWatcher {

    private static final String TAG = "AddNewSectionDialog";
    private DialogAddNewSectionBinding mBinding;
    private AddNewSectionClickListener listener;
    private String[] sectionsAvailable;
    private String sectionName = "None";
    private int pos;

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
            if (mBinding.tilSectionMarks.getVisibility() == View.VISIBLE &&
                    mBinding.etSectionMarks.getText().toString().trim().isEmpty()) {
                mBinding.tilSectionMarks.setError("Enter Section Marks");
                return;
            } else {
                //String name = mBinding.etSectionName.getText().toString().trim();
                if (mBinding.tilSectionMarks.getVisibility() == View.GONE) {
                    listener.onNewSectionSaveClick(sectionName, 0, pos);
                } else {
                    int marks = Integer.parseInt(mBinding.etSectionMarks.getText().toString().trim());
                    listener.onNewSectionSaveClick(sectionName, marks, pos);
                }
                dismiss();
            }
        });

        mBinding.sectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton != null) {
                    pos = Integer.parseInt(radioButton.getTag().toString());
                    sectionName = radioButton.getText().toString();

                    if (sectionName.equalsIgnoreCase("None")) {
                        mBinding.tilSectionMarks.getEditText().setText("");
                        mBinding.tilSectionMarks.setVisibility(View.GONE);
                    } else {
                        mBinding.tilSectionMarks.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mBinding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void setSections() {
        Gson gson = new Gson();
        String json = TinyDB.getInstance(getActivity()).getString(Constant.TEST_SUBJECT_CHAP);
        TestSubjectChapterMaster testSubjectChapterMaster = gson.fromJson(json, TestSubjectChapterMaster.class);
        Log.i(TAG, "setSections : " + testSubjectChapterMaster.getSections());
        int sectionCount = 0;
        String[] sectns = new String[5];
        if (testSubjectChapterMaster.getSections() != null) {
            sectns = testSubjectChapterMaster.getSections().split(",", -1);
            sectionCount = sectns.length;
        }
        if (sectionCount == 1) {
            mBinding.section1.setVisibility(View.VISIBLE);
            mBinding.section1.setText(sectns[0].split("=", -1)[1]);
        } else if (sectionCount == 2) {
            mBinding.section1.setVisibility(View.VISIBLE);
            mBinding.section1.setText(sectns[0].split("=", -1)[1]);
            mBinding.section2.setVisibility(View.VISIBLE);
            mBinding.section2.setText(sectns[1].split("=", -1)[1]);
        } else if (sectionCount == 3) {
            mBinding.section1.setVisibility(View.VISIBLE);
            mBinding.section1.setText(sectns[0].split("=", -1)[1]);
            mBinding.section2.setVisibility(View.VISIBLE);
            mBinding.section2.setText(sectns[1].split("=", -1)[1]);
            mBinding.section3.setVisibility(View.VISIBLE);
            mBinding.section3.setText(sectns[2].split("=", -1)[1]);
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
        void onNewSectionSaveClick(String name, int marks, int pos);
    }
}
