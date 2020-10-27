package com.jangletech.qoogol.ui.create_test;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentCreateBasicDetailsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.SyllabusDialog;
import com.jangletech.qoogol.model.CreateTestResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.model.TestSubjectChapterMaster;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTestBasicDetails extends BaseFragment implements TextWatcher, View.OnClickListener, SyllabusDialog.SyllabusClickListener {

    private static final String TAG = "CreateTestBasicDetails";
    private FragmentCreateBasicDetailsBinding mBinding;
    private String difficulty = "", category = "", strTestType = "", strNegativeMarks = "";
    private String chapters = "";
    private HashMap<String, String> diffLevel = new HashMap<>();
    private HashMap<String, String> testCategory = new HashMap<>();
    private HashMap<String, String> testType = new HashMap<>();
    private TestModelNew testModelNew = new TestModelNew();
    private HashMap<String, String> negativeMarks = new HashMap<>();
    private TestSubjectChapterMaster testSubjectChapterMaster = new TestSubjectChapterMaster();
    private Calendar mcurrentTime;
    private TimePickerDialog mTimePicker;
    private String tmId = "";
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_basic_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated Executed: ");
        try {
            bundle = new Bundle();
            initializeHashMaps();
            mcurrentTime = Calendar.getInstance();
            //testSubjectChapterMaster = getSyllabusDetails();

            if (getArguments() != null && getArguments().getSerializable("CREATED_TEST") != null) {
                testModelNew = (TestModelNew) getArguments().getSerializable("CREATED_TEST");
                setCreatedTestDetails(testModelNew);
            } else {
                mcurrentTime.set(Calendar.HOUR_OF_DAY, 0);
                mcurrentTime.set(Calendar.MINUTE, 30);
                mBinding.etDuration.setText("00:30");

                prepareChips(mBinding.testDiffLevel, diffLevel, "");
                prepareChips(mBinding.testExecuteTypeChipGroup, testType, "");
                prepareChips(mBinding.testNegativeMarksGroup, negativeMarks, "0");
                prepareChips(mBinding.testCategoryChipGrp, testCategory, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBinding.etTestTitle.addTextChangedListener(this);
        mBinding.etTotalMarks.addTextChangedListener(this);
        mBinding.etDuration.addTextChangedListener(this);
        mBinding.etNoOfQuests.addTextChangedListener(this);

        mBinding.etDuration.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    mTimePicker = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            String duration = String.format("%02d:%02d", selectedHour, selectedMinute);
                            mBinding.etDuration.setText(duration);
                            mcurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            mcurrentTime.set(Calendar.MINUTE, selectedMinute);
                        }
                    }, mcurrentTime.get(Calendar.HOUR_OF_DAY), mcurrentTime.get(Calendar.MINUTE), true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Duration");
                    mTimePicker.show();
                }
                return true;
            }
        });

        mBinding.chipSubject.setOnClickListener(v -> {
            //navigationFromCreateTest(R.id.nav_modify_syllabus, Bundle.EMPTY);
            DialogFragment syllabusDialog = new SyllabusDialog(this);
            syllabusDialog.show(getParentFragmentManager(), "syllabus_dialog");
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
            try {
                if (mBinding.etTestTitle.getText().toString().isEmpty()) {
                    mBinding.tilTestTitle.setError("Please enter test title.");
                    mBinding.etTestTitle.requestFocus();
                    return;
                }

                if (mBinding.etNoOfQuests.getText().toString().isEmpty()) {
                    mBinding.tilTotalQuest.setError("Please enter Total No. of questions.");
                    mBinding.etNoOfQuests.requestFocus();
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

                if (mBinding.chipSubject.getText().toString().equalsIgnoreCase("No Subject") ||
                        mBinding.chipSubject.getText().toString().isEmpty()) {
                    showToast("Please select subject.");
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
                testModelNew.setSm_sub_name(testModelNew.getSm_sub_name());
                testModelNew.setTm_sm_id(testModelNew.getTm_sm_id()); //subjectId
                testModelNew.setTm_cm_id(testModelNew.getTm_cm_id()); //
                testModelNew.setTm_neg_mks(strNegativeMarks);
                testModelNew.setTm_type(strTestType);
                testModelNew.setTm_diff_level(difficulty);
                testModelNew.setTm_catg(category);
                testModelNew.setQuest_count(mBinding.etNoOfQuests.getText().toString());
                TinyDB.getInstance(getActivity()).putString(Constant.tm_tot_marks, mBinding.etTotalMarks.getText().toString().trim());
                createTest(testModelNew);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initializeHashMaps() {

        diffLevel.put("E", "Easy");
        diffLevel.put("M", "Medium");
        diffLevel.put("H", "Hard");

        testCategory.put("U", "Unit Test-Practice");
        testCategory.put("S", "Semester Final-Practice");
        testCategory.put("A", "Annual-Practice");

        testType.put("P", "Practice Test");
        testType.put("M", "Mock Test");
        testType.put("F", "Formal Test");

        negativeMarks.put("0", "None");
        negativeMarks.put("25", "25%");
        negativeMarks.put("50", "50%");
        negativeMarks.put("75", "75%");
        negativeMarks.put("100", "100%");

    }

    private void setCreatedTestDetails(TestModelNew testModelNew) {

        Log.i(TAG, "setCreatedTestDetails TmId : " + testModelNew.getTm_id());
        Log.i(TAG, "setCreatedTestDetails: " + testModelNew.getTest_sections());
        Log.i(TAG, "setCreatedTestDetails Sections : " + testModelNew.getTest_sections());
        TinyDB.getInstance(getActivity()).putString(Constant.TEST_SUBJECT_CHAP, testModelNew.getTest_sections());
        category = testModelNew.getTm_catg();
        strTestType = testModelNew.getTm_type();
        difficulty = testModelNew.getTm_diff_level();
        strNegativeMarks = testModelNew.getTm_neg_mks();
        //testSubjectChapterMaster.setSubjectName(testModelNew.getSm_sub_name());
        //testSubjectChapterMaster.setSubjectId(testModelNew.getTm_sm_id());
        //testSubjectChapterMaster.setChap1Id(testModelNew.getTm_cm_id());
        Log.i(TAG, "setCreatedTestDetails duration: " + testModelNew.getTm_duration());
        if (testModelNew.getTm_duration() != null && !testModelNew.getTm_duration().isEmpty()) {
            String[] duration = testModelNew.getTm_duration().split(":", -1);
            mcurrentTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(duration[0]));
            mcurrentTime.set(Calendar.MINUTE, Integer.parseInt(duration[1]));
        }

        mBinding.etTestTitle.setText(testModelNew.getTm_name());
        mBinding.etTestDesc.setText(testModelNew.getTest_description());
        mBinding.etNoOfQuests.setText(testModelNew.getQuest_count());
        mBinding.etDuration.setText(testModelNew.getTm_duration());
        mBinding.etTotalMarks.setText(testModelNew.getTm_tot_marks());
        mBinding.chipSubject.setText(testModelNew.getSm_sub_name());
        mBinding.chipSubject.setEnabled(false);
        prepareChips(mBinding.testDiffLevel, diffLevel, testModelNew.getTm_diff_level());
        prepareChips(mBinding.testExecuteTypeChipGroup, testType, testModelNew.getTm_type());

        Log.i(TAG, "setCreatedTestDetails Neg Marks : " + getKeyFromValuea(testCategory, testModelNew.getTm_catg()));

        prepareChips(mBinding.testNegativeMarksGroup, negativeMarks, testModelNew.getTm_neg_mks());
        prepareChips(mBinding.testCategoryChipGrp, testCategory, testModelNew.getTm_catg());

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
            mBinding.tilTotalQuest.setError(null);
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
            if (v.getTag().equals("CHAPTER")) {
                DialogFragment syllabusDialog = new SyllabusDialog(this);
                syllabusDialog.show(getParentFragmentManager(), "syllabus_dialog");
            }
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
        try {
            ProgressDialog.getInstance().show(getActivity());
            Gson gson = new Gson();
            String json = gson.toJson(testModelNew);

            if (testModelNew.getTm_id() == 0) {
                tmId = "";
            } else {
                tmId = String.valueOf(testModelNew.getTm_id());
            }
            bundle.putString(Constant.tm_id, tmId);
            Log.i(TAG, "createTest Object : " + json);
            Log.i(TAG, "createTest Publish Date : " + new Date().toString());
            Log.i(TAG, "createTest Final TmId : " + tmId);
            Call<CreateTestResponse> call = ApiClient.getInstance().getApi()
                    .createModifyTest(
                            AppUtils.getUserId(),
                            AppUtils.getDeviceId(),
                            Constant.APP_NAME,
                            tmId,
                            "I",
                            testModelNew.getTm_name(),
                            testModelNew.getTest_description(),
                            testModelNew.getQuest_count(),
                            testModelNew.getTm_tot_marks(),
                            testModelNew.getTm_duration(),
                            testModelNew.getTm_sm_id(),
                            testModelNew.getTm_cm_id(),
                            testModelNew.getTm_neg_mks(),
                            testModelNew.getTm_type(),
                            testModelNew.getTm_diff_level(),
                            testModelNew.getTm_catg(),
                            getFormattedDate()
                    );
            call.enqueue(new Callback<CreateTestResponse>() {
                @Override
                public void onResponse(Call<CreateTestResponse> call, Response<CreateTestResponse> response) {
                    ProgressDialog.getInstance().dismiss();
                    try {
                        if (response.body().getResponse() == 200) {
                            Log.i(TAG, "onResponse : " + response.body());
                            bundle.putString(Constant.tm_id, response.body().getTmId());
                            navigationFromCreateTest(R.id.nav_create_test_section, bundle);
                        } else {
                            //navigationFromCreateTest(R.id.nav_create_test_section, bundle);
                            AppUtils.showToast(getActivity(), null, response.body().getMessage());
                            Log.e(TAG, "Response Code : " + response.body().getResponse());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSyllabusSaveClick() {
        testSubjectChapterMaster = getSyllabusDetails();
        mBinding.chipSubject.setText(testSubjectChapterMaster.getSubjectName());
        List<String> listChapters = new ArrayList<>();
        listChapters.add(testSubjectChapterMaster.getChap1Name());
        listChapters.add(testSubjectChapterMaster.getChap2Name());
        listChapters.add(testSubjectChapterMaster.getChap3Name());
        prepareChapteChips(listChapters);
        testModelNew.setSm_sub_name(testSubjectChapterMaster.getSubjectName());
        testModelNew.setTm_sm_id(testSubjectChapterMaster.getSubjectId());
        testModelNew.setTm_cm_id(testSubjectChapterMaster.getChap1Id());
    }

    private void prepareChapteChips(List<String> list) {
        mBinding.chipGrpChapter.removeAllViews();
        int id = 0;
        for (String string : list) {
            if (string != null && !string.isEmpty()) {
                Chip chip = (Chip) LayoutInflater.from(mBinding.chipGrpChapter.getContext()).inflate(R.layout.chip_new, mBinding.chipGrpChapter, false);
                chip.setText(string);
                chip.setTag("CHAPTER");
                chip.setId(id);
                chip.setClickable(true);
                chip.setCheckable(true);
                chip.setOnClickListener(this);
                mBinding.chipGrpChapter.addView(chip);
            }
        }
    }
}
