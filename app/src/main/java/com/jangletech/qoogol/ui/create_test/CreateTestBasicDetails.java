package com.jangletech.qoogol.ui.create_test;

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

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentCreateBasicDetailsBinding;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;

public class CreateTestBasicDetails extends BaseFragment implements TextWatcher {

    private static final String TAG = "CreateTestBasicDetails";
    private FragmentCreateBasicDetailsBinding mBinding;
    private String difficulty = "", category = "";
    private String chapters = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_basic_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getString(Constant.subjectName) != null && !getString(Constant.subjectName).isEmpty())
            mBinding.chipSubject.setText(getString(Constant.subjectName));

        if (getString(Constant.chapterName1) != null &&
                !getString(Constant.chapterName1).isEmpty()) {
            chapters = chapters + getString(Constant.chapterName1);
        }
        if (getString(Constant.chapterName2) != null &&
                !getString(Constant.chapterName2).isEmpty()) {
            chapters = chapters + "," + getString(Constant.chapterName2);
        }
        if (getString(Constant.chapterName3) != null &&
                !getString(Constant.chapterName3).isEmpty()) {
            chapters = chapters + "," + getString(Constant.chapterName3);
        }

        if (!chapters.isEmpty())
            mBinding.chipChapter.setText(chapters);

        mBinding.etTestTitle.addTextChangedListener(this);
        mBinding.etTotalMarks.addTextChangedListener(this);
        mBinding.etDuration.addTextChangedListener(this);

        mBinding.chipSubject.setOnClickListener(v -> {
            navigationFromCreateTest(R.id.nav_modify_syllabus, Bundle.EMPTY);
        });

        mBinding.chipChapter.setOnClickListener(v -> {
            navigationFromCreateTest(R.id.nav_modify_syllabus, Bundle.EMPTY);
        });

        mBinding.testDiffLevel.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    //showToast("Selected : " + chip.getText().toString());
                    difficulty = chip.getText().toString();
                }
            }
        });

        mBinding.testTypeChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    //showToast("Selected : " + chip.getText().toString());
                    category = chip.getText().toString();
                }
            }
        });

        mBinding.btnNext.setOnClickListener(v -> {
            if (mBinding.etTestTitle.getText().toString().isEmpty()) {
                mBinding.tilTestTitle.setError("Please enter test title.");
                mBinding.etTestTitle.requestFocus();
                return;
            }

            if (mBinding.etTotalMarks.getText().toString().isEmpty()) {
                mBinding.tilTotalMarks.setError("Please enter total marks.");
                mBinding.etTotalMarks.requestFocus();
                return;
            }

            if (mBinding.etDuration.getText().toString().isEmpty()) {
                mBinding.tilDuration.setError("Please enter duration.");
                mBinding.etDuration.requestFocus();
                return;
            }

            if (!mBinding.chipSubject.getText().toString().equalsIgnoreCase("No Subject") &&
                    mBinding.chipSubject.getText().toString().trim().isEmpty()) {
                showToast("Please select subject.");
                return;
            }

            if (!mBinding.chipChapter.getText().toString().equalsIgnoreCase("No Chapters") &&
                    mBinding.chipChapter.getText().toString().trim().isEmpty()) {
                showToast("Please select chapters.");
                return;
            }

            if (difficulty.isEmpty()) {
                showToast("Please select difficulty level.");
                return;
            }
            if (category.isEmpty()) {
                showToast("Please select Category.");
                return;
            }
            navigationFromCreateTest(R.id.nav_create_test_section, Bundle.EMPTY);
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
        Log.i(TAG, "afterTextChanged: " + s.toString());
        //showToast("" + s.toString());
        if (s.toString().length() > 0) {
            mBinding.tilTestTitle.setError(null);
            mBinding.tilTotalMarks.setError(null);
            mBinding.tilDuration.setError(null);
        }
    }
}
