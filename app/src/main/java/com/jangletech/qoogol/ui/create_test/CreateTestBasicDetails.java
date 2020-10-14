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
import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentCreateBasicDetailsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.CreateTestResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.model.TestSubjectChapterMaster;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTestBasicDetails extends BaseFragment implements TextWatcher, View.OnClickListener {

    private static final String TAG = "CreateTestBasicDetails";
    private FragmentCreateBasicDetailsBinding mBinding;
    private String difficulty = "", category = "", strTestType = "", strNegativeMarks = "";
    private String chapters = "";
    private TestModelNew testModelNew = new TestModelNew();
    private TestSubjectChapterMaster testSubjectChapterMaster;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_basic_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null && getArguments().getSerializable("CREATED_TEST") != null) {
            testModelNew = (TestModelNew) getArguments().getSerializable("CREATED_TEST");
        }

        Gson gson = new Gson();
        String json = TinyDB.getInstance(getActivity()).getString(Constant.TEST_SUBJECT_CHAP);
        testSubjectChapterMaster = gson.fromJson(json, TestSubjectChapterMaster.class);


        HashMap<String, String> diffLevel = new HashMap<>();
        diffLevel.put("E", "Easy");
        diffLevel.put("M", "Medium");
        diffLevel.put("H", "Hard");
        prepareChips(mBinding.testDiffLevel, diffLevel, "");

        HashMap<String, String> testCategory = new HashMap<>();
        testCategory.put("U", "Unit Test Practise");
        testCategory.put("S", "Semester Final Practise");
        testCategory.put("A", "Annual Practise");
        prepareChips(mBinding.testCategoryChipGrp, testCategory, "");

        HashMap<String, String> testType = new HashMap<>();
        testType.put("P", "Practise Test");
        testType.put("M", "Mock Test");
        testType.put("F", "Formal Test");
        prepareChips(mBinding.testExecuteTypeChipGroup, testType, "");

        HashMap<String, String> negativeMarks = new HashMap<>();
        negativeMarks.put("0", "None");
        negativeMarks.put("25", "25%");
        negativeMarks.put("50", "50%");
        negativeMarks.put("75", "75%");
        negativeMarks.put("100", "100%");
        prepareChips(mBinding.testNegativeMarksGroup, negativeMarks, "0");

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

        mBinding.testCategoryChipGrp.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    category = chip.getTag().toString();
                }
            }
        });

        mBinding.testDiffLevel.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    difficulty = chip.getTag().toString();
                }
            }
        });

        mBinding.testExecuteTypeChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    strTestType = chip.getTag().toString();
                }
            }
        });

        mBinding.testNegativeMarksGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    strNegativeMarks = chip.getTag().toString();
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

            if (getSelectedChipValue(mBinding.testExecuteTypeChipGroup).isEmpty()) {
                showToast("Please select Test Type.");
                return;
            }

            if (getSelectedChipValue(mBinding.testDiffLevel).isEmpty()) {
                showToast("Please select difficulty level.");
                return;
            }
            if (getSelectedChipValue(mBinding.testCategoryChipGrp).isEmpty()) {
                showToast("Please select Category.");
                return;
            }

            testModelNew.setTm_name(mBinding.etTestTitle.getText().toString().trim());
            testModelNew.setTest_description(mBinding.etTestDesc.getText().toString().trim());
            testModelNew.setTm_tot_marks(mBinding.etTotalMarks.getText().toString().trim());
            testModelNew.setTm_duration(mBinding.etDuration.getText().toString());
            testModelNew.setSm_sub_name(testSubjectChapterMaster.getSubjectName());
            testModelNew.setTm_sm_id(testSubjectChapterMaster.getSubjectId()); //subjectId
            testModelNew.setTm_cm_id(testSubjectChapterMaster.getChap1Id()); //todo Comma seperated Chapter Ids
            testModelNew.setTm_neg_mks(strNegativeMarks);
            testModelNew.setTm_type(strTestType);
            testModelNew.setTm_diff_level(difficulty);
            testModelNew.setTm_catg(category);
            createTest(testModelNew);

//            TinyDB.getInstance(getActivity()).putString("SECTIONS", testModelNew.getTest_sections());
//            navigationFromCreateTest(R.id.nav_create_test_section, Bundle.EMPTY);
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
        if (s.toString().length() > 0) {
            mBinding.tilTestTitle.setError(null);
            mBinding.tilTotalMarks.setError(null);
            mBinding.tilDuration.setError(null);
        }
    }

    private void prepareChips(ChipGroup chipGroup, HashMap<String, String> params, String chekedKey) {
        chipGroup.removeAllViews();
        int counter = 0;
        Iterator<Map.Entry<String, String>> itr = params.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            Chip chip = (Chip) LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.chip_new, chipGroup, false);
            chip.setText(entry.getValue());
            chip.setTag(entry.getKey());
            chip.setId(counter);
            chip.setClickable(true);
            chip.setCheckable(true);
            chip.setOnClickListener(this);
            chipGroup.addView(chip);
            counter++;
            if (entry.getKey().equalsIgnoreCase(chekedKey)) {
                chip.setChecked(true);
            }

            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {

        }
    }

    private String getSelectedChipValue(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip != null && chip.isChecked()) {
                return chip.getText().toString();
            }
        }
        return "";
    }

    private void createTest(TestModelNew testModelNew) {
        ProgressDialog.getInstance().show(getActivity());
        Call<CreateTestResponse> call = ApiClient.getInstance().getApi()
                .createModifyTest(
                        AppUtils.getUserId(),
                        testModelNew.getTm_name(),
                        testModelNew.getTest_description(),
                        testModelNew.getTm_tot_marks(),
                        testModelNew.getTm_duration(),
                        testModelNew.getTm_sm_id(),
                        testModelNew.getTm_cm_id(),
                        testModelNew.getTm_neg_mks(),
                        testModelNew.getTm_type(),
                        testModelNew.getTm_diff_level(),
                        testModelNew.getTm_catg(),
                        new Date().toString()
                );
        call.enqueue(new Callback<CreateTestResponse>() {
            @Override
            public void onResponse(Call<CreateTestResponse> call, Response<CreateTestResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body().getResponse() == 200) {
                    Log.i(TAG, "onResponse : " + response.body());
                    TinyDB.getInstance(getActivity()).putString("SECTIONS", testModelNew.getTest_sections());
                    navigationFromCreateTest(R.id.nav_create_test_section, Bundle.EMPTY);

                } else {
                    Log.e(TAG, "Response Code : " + response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<CreateTestResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(getActivity(), t, "");
                apiCallFailureDialog(t);
                t.printStackTrace();
            }
        });
    }
}
