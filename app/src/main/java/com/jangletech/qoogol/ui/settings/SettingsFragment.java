package com.jangletech.qoogol.ui.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.databinding.FragmentSettingsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";
    private MyTestViewModel mViewModel;
    private FragmentSettingsBinding mBinding;
    private HashMap<Integer,Chip> mapExamChips = new HashMap();
    private HashMap<Integer, Chip> mapSubjectChips = new HashMap();
    ApiInterface apiService = ApiClient.getInstance().getApi();
    public static String strBoardName;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews(){
        mBinding.btnApply.setOnClickListener(v->{
            MainActivity.navController.navigate(R.id.nav_test_my);
        });
    }

    private void fetchSubjectList() {
        ProgressDialog.getInstance().show(getActivity());
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));//todo change userId
        call.enqueue(new Callback<FetchSubjectResponseList>() {
            @Override
            public void onResponse(Call<FetchSubjectResponseList> call, Response<FetchSubjectResponseList> response) {
                ProgressDialog.getInstance().dismiss();
                mViewModel.setAllSubjectList(response.body().getFetchSubjectResponseList());
            }

            @Override
            public void onFailure(Call<FetchSubjectResponseList> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.boardChip.setText(new PreferenceManager(getActivity()).getString(Constant.BOARD));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);

        fetchSubjectList();
        mViewModel.getAllSubjects().observe(getActivity(), new Observer<List<FetchSubjectResponse>>() {
            @Override
            public void onChanged(@Nullable final List<FetchSubjectResponse> subjects) {
                Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
                prepareSubjectChips(subjects);
            }
        });

        mBinding.tvBoardEdit.setOnClickListener(v->{
            MainActivity.navController.navigate(R.id.nav_board_fragment, Bundle.EMPTY);
        });

        mBinding.boardChip.setOnClickListener(v->{
            MainActivity.navController.navigate(R.id.nav_board_fragment, Bundle.EMPTY);
        });

        mBinding.boardChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.boardChipGrp);
                }
            }
        });

        mBinding.classChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.classChipGrp);
                    if (chip.getText().toString().contains("11")
                            || chip.getText().toString().contains("12")) {
                        mBinding.layoutStream.setVisibility(View.VISIBLE);
                        mBinding.layoutSubjects.setVisibility(View.VISIBLE);
                        mBinding.layoutExams.setVisibility(View.VISIBLE);
                    } else {
                        mBinding.layoutStream.setVisibility(View.GONE);
                        mBinding.layoutSubjects.setVisibility(View.VISIBLE);
                        mBinding.layoutExams.setVisibility(View.VISIBLE);
                    }

                    if (chip.getText().toString().contains("Degree")) {
                        mBinding.layoutStream.setVisibility(View.GONE);
                        mBinding.layoutDegree.setVisibility(View.VISIBLE);
                        mBinding.layoutSubjects.setVisibility(View.GONE);
                        mBinding.layoutExams.setVisibility(View.GONE);

                    } else {
                        mBinding.layoutDegree.setVisibility(View.GONE);
                        mBinding.layoutDegreeStream.setVisibility(View.GONE);
                        mBinding.layoutCourseYear.setVisibility(View.GONE);
                        mBinding.layoutSubjects.setVisibility(View.VISIBLE);
                        mBinding.layoutExams.setVisibility(View.VISIBLE);

                    }
                    //showToast("Selected : " + chip.getText().toString());
                }
            }
        });

        mBinding.hscChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            //showToast("id : " + id);
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.hscChipGrp);
                    mBinding.layoutSubjects.setVisibility(View.VISIBLE);
                    mBinding.layoutExams.setVisibility(View.VISIBLE);
                    //showToast("Selected : " + chip.getText().toString());
                }
            }
        });

        mBinding.degreeChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            //showToast("id : " + id);
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.degreeChipGrp);
                    mBinding.layoutDegreeStream.setVisibility(View.VISIBLE);
                    mBinding.layoutSubjects.setVisibility(View.GONE);
                    mBinding.layoutExams.setVisibility(View.GONE);
                }
            }
        });

        mBinding.degreeStreamChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            //showToast("id : " + id);
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.degreeStreamChipGrp);
                    mBinding.layoutCourseYear.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.CourseYearChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            //showToast("id : " + id);
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.CourseYearChipGrp);
                    mBinding.layoutSubjects.setVisibility(View.VISIBLE);
                    mBinding.layoutExams.setVisibility(View.VISIBLE);
                    //showToast("Selected : " + chip.getText().toString());
                }
            }
        });


        List boardList = new ArrayList();
        boardList.add("Maharashtra State Board");
        boardList.add("ICSE");
        boardList.add("CBSE");
        //prepareBoardChips(boardList);

        List classList = new ArrayList();
        classList.add("5th Std.");
        classList.add("6th Std");
        classList.add("7th Std");
        classList.add("8th Std.");
        classList.add("9th Std");
        classList.add("10th Std");
        classList.add("11th Std.");
        classList.add("12th Std");
        classList.add("Degree");
        prepareClassesChips(classList);

        List hscStreamList = new ArrayList();
        hscStreamList.add("Science");
        hscStreamList.add("Commerce");
        prepareHscStreamChips(hscStreamList);

        List degreeList = new ArrayList();
        degreeList.add("B-Tech");
        degreeList.add("M-Tech");
        degreeList.add("Bsc");
        degreeList.add("Msc");
        degreeList.add("MBBS");
        prepareDegreeChips(degreeList);

        List degreeStreamList = new ArrayList();
        degreeStreamList.add("Computer Science");
        degreeStreamList.add("Mechanical");
        degreeStreamList.add("Civil");
        degreeStreamList.add("Electrical");
        degreeStreamList.add("Instrumentation");
        prepareDegreeStreamChips(degreeStreamList);

        List courseYearList = new ArrayList();
        courseYearList.add("1st Year");
        courseYearList.add("2nd Year");
        courseYearList.add("3rd Year");
        courseYearList.add("4th Year");
        prepareCourseYearList(courseYearList);

        List examsList = new ArrayList();
        examsList.add("MPSC");
        examsList.add("UPSC");
        examsList.add("GATE");
        examsList.add("GRE");
        prepareExamList(examsList);

        List subjectList = new ArrayList();
        subjectList.add("Physics");
        subjectList.add("Maths");
        subjectList.add("Engineering Drawing");
        subjectList.add("Chemistry");
        subjectList.add("English");
        subjectList.add("Networking");
        subjectList.add("Database Management Systems");
        subjectList.add("All");
        //prepareSubjectChips(subjectList);

    }


    private void prepareClassesChips(List classList) {
        mBinding.classChipGrp.removeAllViews();
        for (int i = 0; i < classList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.classChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.classChipGrp, false);
            //chip = new Chip(getActivity());
            chip.setText(classList.get(i).toString());
            chip.setTag(classList.get(i).toString());
            chip.setId(i);
           /* chip.setClickable(true);
            chip.setFocusable(true);
            chip.setCheckable(true);*/
            mBinding.classChipGrp.addView(chip);
        }
    }

    private void prepareHscStreamChips(List classList) {
        mBinding.hscChipGrp.removeAllViews();
        for (int i = 0; i < classList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.hscChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.hscChipGrp, false);
            chip.setText(classList.get(i).toString());
            chip.setTag(classList.get(i).toString());
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.hscChipGrp.addView(chip);
        }
    }

    private void prepareDegreeChips(List degreeList) {
        mBinding.degreeChipGrp.removeAllViews();
        for (int i = 0; i < degreeList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.degreeChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.degreeChipGrp, false);
            chip.setText(degreeList.get(i).toString());
            chip.setTag(degreeList.get(i).toString());
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.degreeChipGrp.addView(chip);
        }
    }

    private void prepareDegreeStreamChips(List degreeStreamList) {
        mBinding.degreeStreamChipGrp.removeAllViews();
        for (int i = 0; i < degreeStreamList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.degreeStreamChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.degreeStreamChipGrp, false);
            chip.setText(degreeStreamList.get(i).toString());
            chip.setTag(degreeStreamList.get(i).toString());
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.degreeStreamChipGrp.addView(chip);
        }
    }

    private void prepareCourseYearList(List degreeStreamList) {
        mBinding.CourseYearChipGrp.removeAllViews();
        for (int i = 0; i < degreeStreamList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.CourseYearChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.CourseYearChipGrp, false);
            chip.setText(degreeStreamList.get(i).toString());
            chip.setTag(degreeStreamList.get(i).toString());
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.CourseYearChipGrp.addView(chip);
        }
    }

    private void prepareExamList(List examsList) {
        mBinding.examsChipGrp.removeAllViews();
        for (int i = 0; i < examsList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.examsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.examsChipGrp, false);
            chip.setText(examsList.get(i).toString());
            chip.setTag("Exams");
            chip.setId(i);
            mapExamChips.put(i, chip);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.examsChipGrp.addView(chip);
        }
    }

    private void prepareSubjectChips(List<FetchSubjectResponse> subjectList) {
        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
            chip.setText(subjectList.get(i).getSm_sub_name());
            chip.setTag("Subjects");
            chip.setId(i);
            mapSubjectChips.put(i, chip);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.subjectsChipGrp.addView(chip);
        }
    }

    private void setCheckedChip(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setTextColor(Color.BLACK);
            }
        }
    }

    private void setSelectedExamChips(Chip chip) {
        showToast("Selected : " + chip.getText().toString());
        Chip selectedChip = mapExamChips.put(chip.getId(),chip);
        for (int i = 0; i <mapExamChips.size() ; i++) {
            if(mapExamChips.get(i).isChecked()){
                mapExamChips.get(i).setTextColor(Color.WHITE);
            }else{
                mapExamChips.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    private void setSelectedSubjectsChips(Chip chip) {
        showToast("Selected : " + chip.getText().toString());
        Chip selectedChip = mapSubjectChips.put(chip.getId(),chip);
        for (int i = 0; i <mapSubjectChips.size() ; i++) {
         if(mapSubjectChips.get(i).isChecked()){
             mapSubjectChips.get(i).setTextColor(Color.WHITE);
         }else{
             mapSubjectChips.get(i).setTextColor(Color.BLACK);
         }
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            if (v.getTag().toString().equalsIgnoreCase("Subjects")) {
                Chip chip = (Chip) v;
                setSelectedSubjectsChips(chip);
            }
            if (v.getTag().toString().equalsIgnoreCase("Exams")) {
                Chip chip = (Chip) v;
                setSelectedExamChips(chip);
            }
        }
    }
}
