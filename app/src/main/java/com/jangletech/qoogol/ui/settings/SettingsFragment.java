package com.jangletech.qoogol.ui.settings;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.databinding.FragmentSettingsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.AddElementResponse;
import com.jangletech.qoogol.model.ClassList;
import com.jangletech.qoogol.model.ClassResponse;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.CourseResponse;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.DegreeResponse;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.InstituteResponse;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.model.UniversityResponse;
import com.jangletech.qoogol.model.UserPreferences;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.board_id;
import static com.jangletech.qoogol.util.Constant.degree_id;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";
    private MyTestViewModel mViewModel;
    private FragmentSettingsBinding mBinding;
    private Map<String, String> mMapClasses;
    private Map<Integer, String> mMapUniversity;
    private Map<Integer, String> mMapInstitute;
    private Map<Integer, String> mMapDegree;
    private Map<Integer, String> mMapCourse;
    private HashMap<Integer, Chip> mapExamChips = new HashMap();
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
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        initViews(mBinding);
        return mBinding.getRoot();
    }

    private void initViews(FragmentSettingsBinding mBinding) {

        fetchInstituteData(0);
        fetchUniversityData();
        fetchDegreeData();

        mBinding.boardAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapUniversity, name);
            if (key != -1) {
                mBinding.boardAutocompleteView.setTag(key);
                //fetchInstituteData(key);
            }
        });

        mBinding.instituteAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapInstitute, name);
            if (key != -1) {
                mBinding.instituteAutocompleteView.setTag(key);
            }
        });

        mBinding.degreeAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapDegree, name);
            if (key != -1) {
                mBinding.degreeAutocompleteView.setTag(key);
                fetchCourseData(key);
            }
            mBinding.courseAutocompleteView.setText("");
            mBinding.courseAutocompleteView.setTag("");
            mBinding.classAutocompleteView.setText("");
            mBinding.classAutocompleteView.setText("");
            mBinding.subjectsChipGrp.removeAllViews();
            Log.e(TAG, "Degree Id : "+key);
            Log.e(TAG, "Degree : "+name);
        });

        mBinding.courseAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapCourse, name);
            if (key != -1) {
                mBinding.courseAutocompleteView.setTag(key);
                fetchClassList(key);
            }
            mBinding.classAutocompleteView.setText("");
            mBinding.classAutocompleteView.setTag("");
            mBinding.subjectsChipGrp.removeAllViews();
            Log.e(TAG, "Course Id : "+key);
            Log.e(TAG, "Course : "+name);
        });

        mBinding.classAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            fetchSubjectList(mMapClasses.get(name));
            mBinding.classAutocompleteView.setTag(mMapClasses.get(name));
        });

        mBinding.btnApply.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.nav_test_my);
        });

        mBinding.addUniversity.setOnClickListener(v -> {
            showAddElementDialog("Add University");
        });

        mBinding.addInstitute.setOnClickListener(v -> {
            showAddElementDialog("Add Institute");
        });

        fetchUserPreferences();
        mViewModel.getPreferences().observe(getActivity(), new Observer<UserPreferences>() {
            @Override
            public void onChanged(@Nullable final UserPreferences userPreferences) {
                Log.d(TAG, "onChanged Board Name : " + userPreferences.getUbm_board_name());
                Log.d(TAG, "onChanged Co Id : " + userPreferences.getCo_id());
                mBinding.setPreference(userPreferences);
                fetchCourseData(Integer.parseInt(userPreferences.getDm_id()));
                fetchClassList(Integer.parseInt(userPreferences.getCo_id()));
                setUserPreferences(userPreferences);
            }
        });


        mViewModel.getClasses().observe(getActivity(), new Observer<List<ClassResponse>>() {
            @Override
            public void onChanged(@Nullable final List<ClassResponse> classResponseList) {
                Log.d(TAG, "onChanged Class List Size : " + classResponseList.size());
                List<String> classDesc = new ArrayList<>();
                mMapClasses = new HashMap<>();
                for(ClassResponse classResponse:classResponseList){
                    classDesc.add(classResponse.get_1297());
                    mMapClasses.put(classResponse.get_1297(),classResponse.get_1296());
                }
                populateClasses(classDesc);
            }
        });


        mViewModel.getAllSubjects().observe(getActivity(), new Observer<List<FetchSubjectResponse>>() {
            @Override
            public void onChanged(@Nullable final List<FetchSubjectResponse> subjects) {
                Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
                prepareSubjectChips(subjects);
            }
        });


//        mBinding.tvBoardEdit.setOnClickListener(v -> {
//            MainActivity.navController.navigate(R.id.nav_board_fragment, Bundle.EMPTY);
//        });
//
//        mBinding.boardChip.setOnClickListener(v -> {
//            MainActivity.navController.navigate(R.id.nav_board_fragment, Bundle.EMPTY);
//        });
//
//        mBinding.boardChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
//            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//            if (chip != null) {
//                if (chip.isChecked()) {
//                    setCheckedChip(mBinding.boardChipGrp);
//                }
//            }
//        });
//
//        mBinding.classChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
//            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//            if (chip != null) {
//                if (chip.isChecked()) {
//                    setCheckedChip(mBinding.classChipGrp);
//                    if (chip.getText().toString().contains("11")
//                            || chip.getText().toString().contains("12")) {
//                        mBinding.layoutStream.setVisibility(View.VISIBLE);
//                        mBinding.layoutSubjects.setVisibility(View.VISIBLE);
//                        mBinding.layoutExams.setVisibility(View.VISIBLE);
//                    } else {
//                        mBinding.layoutStream.setVisibility(View.GONE);
//                        mBinding.layoutSubjects.setVisibility(View.VISIBLE);
//                        mBinding.layoutExams.setVisibility(View.VISIBLE);
//                    }
//
//                    if (chip.getText().toString().contains("Degree")) {
//                        mBinding.layoutStream.setVisibility(View.GONE);
//                        mBinding.layoutDegree.setVisibility(View.VISIBLE);
//                        mBinding.layoutSubjects.setVisibility(View.GONE);
//                        mBinding.layoutExams.setVisibility(View.GONE);
//
//                    } else {
//                        mBinding.layoutDegree.setVisibility(View.GONE);
//                        mBinding.layoutDegreeStream.setVisibility(View.GONE);
//                        mBinding.layoutCourseYear.setVisibility(View.GONE);
//                        mBinding.layoutSubjects.setVisibility(View.VISIBLE);
//                        mBinding.layoutExams.setVisibility(View.VISIBLE);
//
//                    }
//                    //showToast("Selected : " + chip.getText().toString());
//                }
//            }
//        });
//
//        mBinding.hscChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
//            //showToast("id : " + id);
//            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//            if (chip != null) {
//                if (chip.isChecked()) {
//                    setCheckedChip(mBinding.hscChipGrp);
//                    mBinding.layoutSubjects.setVisibility(View.VISIBLE);
//                    mBinding.layoutExams.setVisibility(View.VISIBLE);
//                    //showToast("Selected : " + chip.getText().toString());
//                }
//            }
//        });
//
//        mBinding.degreeChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
//            //showToast("id : " + id);
//            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//            if (chip != null) {
//                if (chip.isChecked()) {
//                    setCheckedChip(mBinding.degreeChipGrp);
//                    mBinding.layoutDegreeStream.setVisibility(View.VISIBLE);
//                    mBinding.layoutSubjects.setVisibility(View.GONE);
//                    mBinding.layoutExams.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        mBinding.degreeStreamChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
//            //showToast("id : " + id);
//            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//            if (chip != null) {
//                if (chip.isChecked()) {
//                    setCheckedChip(mBinding.degreeStreamChipGrp);
//                    mBinding.layoutCourseYear.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//
//        mBinding.CourseYearChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
//            //showToast("id : " + id);
//            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//            if (chip != null) {
//                if (chip.isChecked()) {
//                    setCheckedChip(mBinding.CourseYearChipGrp);
//                    mBinding.layoutSubjects.setVisibility(View.VISIBLE);
//                    mBinding.layoutExams.setVisibility(View.VISIBLE);
//                    //showToast("Selected : " + chip.getText().toString());
//                }
//            }
//        });

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
        //prepareClassesChips(classList);

        List hscStreamList = new ArrayList();
        hscStreamList.add("Science");
        hscStreamList.add("Commerce");
        //prepareHscStreamChips(hscStreamList);

        List degreeList = new ArrayList();
        degreeList.add("B-Tech");
        degreeList.add("M-Tech");
        degreeList.add("Bsc");
        degreeList.add("Msc");
        degreeList.add("MBBS");
        //prepareDegreeChips(degreeList);

        List degreeStreamList = new ArrayList();
        degreeStreamList.add("Computer Science");
        degreeStreamList.add("Mechanical");
        degreeStreamList.add("Civil");
        degreeStreamList.add("Electrical");
        degreeStreamList.add("Instrumentation");
        //prepareDegreeStreamChips(degreeStreamList);

        List courseYearList = new ArrayList();
        courseYearList.add("1st Year");
        courseYearList.add("2nd Year");
        courseYearList.add("3rd Year");
        courseYearList.add("4th Year");
        //prepareCourseYearList(courseYearList);

        List examsList = new ArrayList();
        examsList.add("MPSC");
        examsList.add("UPSC");
        examsList.add("GATE");
        examsList.add("GRE");
        //prepareExamList(examsList);

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

    private void showAddElementDialog(String title) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_element_dialog);
        dialog.show();
        TextInputLayout inputLayout = dialog.findViewById(R.id.tvInputLayout);
        inputLayout.requestFocus();
        TextView btnAdd = dialog.findViewById(R.id.btnAdd);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView tvHead = dialog.findViewById(R.id.tvHead);
        tvHead.setText(title);
        if (title.contains("Institute")) {
            inputLayout.setHint("Institute");
        } else {
            inputLayout.setHint("University");
        }
        btnAdd.setOnClickListener(v -> {
            if (inputLayout.getEditText().getText().toString().isEmpty()) {
                if (title.contains("Institute")) {
                    inputLayout.setError("Please Enter Institute Name.");
                } else {
                    inputLayout.setError("Please Enter University Name.");
                }
                return;
            }

            String text = inputLayout.getEditText().getText().toString();
            if (title.contains("Institute")) {
                dialog.dismiss();
                addNewInstitute(text);
            } else if (title.contains("University")) {
                dialog.dismiss();
                addNewUniversity(text);
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void addNewUniversity(String text) {
        ProgressDialog.getInstance().show(requireActivity());
        Call<AddElementResponse> call = apiService.addUniversity(text);
        call.enqueue(new Callback<AddElementResponse>() {
            @Override
            public void onResponse(Call<AddElementResponse> call, Response<AddElementResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    fetchUniversityData();
                    mBinding.boardAutocompleteView.setText(text);
                    mBinding.boardAutocompleteView.setTag(response.body().getUbm_id());
                } else {
                    Toast.makeText(requireActivity(), "Error : " + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddElementResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Toast.makeText(requireActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void addNewInstitute(String text) {
        ProgressDialog.getInstance().show(requireActivity());
        Call<AddElementResponse> call = apiService.addInstitute(text);
        call.enqueue(new Callback<AddElementResponse>() {
            @Override
            public void onResponse(Call<AddElementResponse> call, Response<AddElementResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mBinding.instituteAutocompleteView.setText(text);
                    fetchInstituteData(0);
                    if (response.body().getIom_id() != null)
                        mBinding.instituteAutocompleteView.setTag(response.body().getIom_id());
                    else
                        mBinding.instituteAutocompleteView.setTag(response.body().getInstituteId());
                } else {
                    Toast.makeText(requireActivity(), "Error : " + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddElementResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Toast.makeText(requireActivity(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void setUserPreferences(UserPreferences userPreference) {
        //todo update the ui
    }

    private void fetchClassList(int courseId) {
        Log.d(TAG, "fetchClassList: "+courseId);
        ProgressDialog.getInstance().show(getActivity());
        Call<ClassList> call = apiService.fetchClasses(courseId);
        call.enqueue(new Callback<ClassList>() {
            @Override
            public void onResponse(Call<ClassList> call, Response<ClassList> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    mViewModel.setClassList(response.body().getClassResponseList());
                    //List<String> list = new ArrayList<>();
                    //                    //Log.d(TAG, "onResponse Size  : "+list.size());
                    //                    //if (list != null && list.size() > 0) {
                        ProgressDialog.getInstance().dismiss();
                    //}
                }else {
                    showErrorDialog(getActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<ClassList> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchUserPreferences() {
        ProgressDialog.getInstance().show(getActivity());
        Call<VerifyResponse> call = apiService.fetchUserSettings(
                new PreferenceManager(requireActivity()).getInt(Constant.USER_ID),
                getDeviceId(),
                Constant.APP_NAME);
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setUserPreference(response.body().getPreferencesList().get(0));
                    Log.e(TAG, "Preference List Size: " + response.body().getPreferencesList().size());
                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchUniversityData() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<UniversityResponse> call = apiService.getUniversity();
        call.enqueue(new Callback<UniversityResponse>() {
            @Override
            public void onResponse(Call<UniversityResponse> call, Response<UniversityResponse> response) {
                try {
                    List<University> list = response.body().getMasterDataList();
                    if (list != null && list.size() > 0) {
                        mMapUniversity = new HashMap<>();
                        for (University university : list) {
                            mMapUniversity.put(Integer.valueOf(university.getUnivBoardId()), university.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateUniversityBoard(mMapUniversity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<UniversityResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchInstituteData(int university) {
        ProgressDialog.getInstance().show(requireActivity());
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(board_id, university);
        Call<InstituteResponse> call = apiService.getInstitute();
        call.enqueue(new Callback<InstituteResponse>() {
            @Override
            public void onResponse(Call<InstituteResponse> call, Response<InstituteResponse> response) {
                try {
                    List<Institute> list = response.body().getMasterDataList();
                    if (list != null && list.size() > 0) {
                        mMapInstitute = new HashMap<>();
                        for (Institute institute : list) {
                            mMapInstitute.put(Integer.valueOf(institute.getInstOrgId()), institute.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateInstitutre(mMapInstitute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<InstituteResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchDegreeData() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<DegreeResponse> call = apiService.getDegrees();
        call.enqueue(new Callback<DegreeResponse>() {
            @Override
            public void onResponse(Call<DegreeResponse> call, retrofit2.Response<DegreeResponse> response) {
                if (response.body() != null) {
                    List<Degree> list = response.body().getMasterDataList();
                    if (list != null && list.size() > 0) {
                        mMapDegree = new HashMap<>();
                        for (Degree degree : list) {
                            mMapDegree.put(degree.getDegreeId(), degree.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateDegrees(mMapDegree);
                    }
                }
            }

            @Override
            public void onFailure(Call<DegreeResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchCourseData(int degree) {
        ProgressDialog.getInstance().show(requireActivity());
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(degree_id, degree);
        Call<CourseResponse> call = apiService.getCourses();
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                if (response.body() != null) {
                    mViewModel.setCourses(response.body().getMasterDataList());
                    List<Course> list = response.body().getMasterDataList();
                    if (list != null && list.size() > 0) {
                        mMapCourse = new HashMap<>();
                        for (Course course : list) {
                            mMapCourse.put(Integer.valueOf(course.getCourseId()), course.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateCourse(mMapCourse);
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchSubjectList(String scr_co_id) {
        ProgressDialog.getInstance().show(getActivity());
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(scr_co_id);
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
        //mBinding.boardChip.setText(new PreferenceManager(getActivity()).getString(Constant.BOARD));
    }


//    private void prepareClassesChips(List classList) {
//        mBinding.classChipGrp.removeAllViews();
//        for (int i = 0; i < classList.size(); i++) {
//            Chip chip = (Chip) LayoutInflater.from(mBinding.classChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.classChipGrp, false);
//            //chip = new Chip(getActivity());
//            chip.setText(classList.get(i).toString());
//            chip.setTag(classList.get(i).toString());
//            chip.setId(i);
//           /* chip.setClickable(true);
//            chip.setFocusable(true);
//            chip.setCheckable(true);*/
//            mBinding.classChipGrp.addView(chip);
//        }
//    }

    /*private void prepareHscStreamChips(List classList) {
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
    }*/

   /* private void prepareDegreeChips(List degreeList) {
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
    }*/

   /* private void prepareDegreeStreamChips(List degreeStreamList) {
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
    }*/

    /*private void prepareCourseYearList(List degreeStreamList) {
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
    }*/

  /*  private void prepareExamList(List examsList) {
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
    }*/

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
        Chip selectedChip = mapExamChips.put(chip.getId(), chip);
        for (int i = 0; i < mapExamChips.size(); i++) {
            if (mapExamChips.get(i).isChecked()) {
                mapExamChips.get(i).setTextColor(Color.WHITE);
            } else {
                mapExamChips.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    private void setSelectedSubjectsChips(Chip chip) {
        showToast("Selected : " + chip.getText().toString());
        Chip selectedChip = mapSubjectChips.put(chip.getId(), chip);
        for (int i = 0; i < mapSubjectChips.size(); i++) {
            if (mapSubjectChips.get(i).isChecked()) {
                mapSubjectChips.get(i).setTextColor(Color.WHITE);
            } else {
                mapSubjectChips.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    private void populateUniversityBoard(Map<Integer, String> mMapUniversity) {
        List<String> list = new ArrayList<>(mMapUniversity.values());
        Collections.sort(list);
        ArrayAdapter<String> universityAdapter = new ArrayAdapter<String>(requireActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.boardAutocompleteView.setAdapter(universityAdapter);
    }

    private void populateClasses(List<String> list) {
        Log.d(TAG, "populateClasses list : "+list);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(requireActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.classAutocompleteView.setAdapter(classAdapter);
    }

    private void populateInstitutre(Map<Integer, String> mMapInstitute) {
        List<String> list = new ArrayList<>(mMapInstitute.values());
        Collections.sort(list);
        ArrayAdapter<String> instituteAdapter = new ArrayAdapter<String>(requireActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.instituteAutocompleteView.setAdapter(instituteAdapter);
    }

    private void populateDegrees(Map<Integer, String> mMapDegree) {
        List<String> list = new ArrayList<>(mMapDegree.values());
        Collections.sort(list);
        ArrayAdapter<String> degreeAdapter = new ArrayAdapter<String>(requireActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.degreeAutocompleteView.setAdapter(degreeAdapter);
    }

    private void populateCourse(Map<Integer, String> mMapCourse) {
        List<String> list = new ArrayList<>(mMapCourse.values());
        Collections.sort(list);
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(requireActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.courseAutocompleteView.setAdapter(courseAdapter);
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
