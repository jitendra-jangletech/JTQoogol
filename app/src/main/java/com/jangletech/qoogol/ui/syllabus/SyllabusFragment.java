package com.jangletech.qoogol.ui.syllabus;

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
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentSyllabusBinding;
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

public class SyllabusFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";
    private MyTestViewModel mViewModel;
    private UserPreferences userSettings;
    private FragmentSyllabusBinding mBinding;
    private Map<String, String> mMapClasses;
    private Map<String, String> mMapScrCoId;
    private Map<Integer, String> mMapUniversity;
    private Map<Integer, String> mMapInstitute;
    private Map<Integer, String> mMapDegree;
    private Map<Integer, String> mMapCourse;
    private HashMap<Integer, Chip> mapExamChips = new HashMap();
    private HashMap<Integer, Chip> mapSubjectChips = new HashMap();
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    public static String strBoardName;

    public static SyllabusFragment newInstance() {
        return new SyllabusFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_syllabus, container, false);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
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
            Log.e(TAG, "Degree Id : " + key);
            Log.e(TAG, "Degree : " + name);
        });

        mBinding.courseAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapCourse, name);
            if (key != -1) {
                mBinding.courseAutocompleteView.setTag(key);
                fetchClassList(String.valueOf(key));
            }
            mBinding.classAutocompleteView.setText("");
            mBinding.classAutocompleteView.setTag("");
            mBinding.subjectsChipGrp.removeAllViews();
            Log.e(TAG, "Course Id : " + key);
            Log.e(TAG, "Course : " + name);
        });

        mBinding.classAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            fetchSubjectList(mMapClasses.get(name));
            mBinding.classAutocompleteView.setTag(mMapClasses.get(name));
        });

        mBinding.btnApply.setOnClickListener(v -> {
            String ubmId = "", iomId = "", coId = "", dmId = "";
            if (mBinding.boardAutocompleteView.getTag() != null)
                ubmId = mBinding.boardAutocompleteView.getTag().toString();

            if (mBinding.instituteAutocompleteView.getTag() != null)
                iomId = mBinding.instituteAutocompleteView.getTag().toString();

            if (mBinding.degreeAutocompleteView.getTag() != null)
                dmId = mBinding.degreeAutocompleteView.getTag().toString();

            if (mBinding.courseAutocompleteView.getTag() != null)
                coId = mBinding.courseAutocompleteView.getTag().toString();

            HashMap<String, String> params = new HashMap<>();
            params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID)));
            params.put(Constant.device_id, getDeviceId());
            params.put(Constant.appName, Constant.APP_NAME);
            params.put(Constant.CASE, "I");
            params.put(Constant.ubm_id, ubmId);
            params.put(Constant.iom_id, iomId);
            params.put(Constant.co_id, coId);
            params.put(Constant.dm_id, dmId);
            saveUserSettingsPreferences(params);
        });

        mBinding.addUniversity.setOnClickListener(v -> {
            showAddElementDialog("Add University");
        });

        mBinding.addInstitute.setOnClickListener(v -> {
            showAddElementDialog("Add Institute");
        });

        fetchUserPreferences();
        mViewModel.getPreferences().observe(getViewLifecycleOwner(), new Observer<UserPreferences>() {
            @Override
            public void onChanged(@Nullable final UserPreferences userPreferences) {
                if (userPreferences != null) {
                    userSettings = userPreferences;
                    Log.d(TAG, "onChanged Board Name : " + userPreferences.getUbm_board_name());
                    Log.d(TAG, "onChanged Co Id : " + userPreferences.getCo_id());
                    mBinding.setPreference(userPreferences);
                    if (userPreferences.getDm_id() != null)
                        fetchCourseData(Integer.parseInt(userPreferences.getDm_id()));
                    if (userPreferences.getCo_id() != null)
                        fetchClassList(userPreferences.getCo_id());
                    prepareExamChips(userPreferences.getExamList());
                }
            }
        });

        mViewModel.getClasses().observe(getViewLifecycleOwner(), new Observer<List<ClassResponse>>() {
            @Override
            public void onChanged(@Nullable final List<ClassResponse> classResponseList) {
                Log.d(TAG, "onChanged Class List Size : " + classResponseList.size());
                List<String> classDesc = new ArrayList<>();
                mMapClasses = new HashMap<>();
                mMapScrCoId = new HashMap<>();
                for (ClassResponse classResponse : classResponseList) {
                    classDesc.add(classResponse.getClassName());
                    mMapScrCoId.put(classResponse.getClm_cy(), classResponse.getClm_co_id());
                    mMapClasses.put(classResponse.getClassName(), classResponse.getClm_co_id());
                }
                populateClasses(classDesc);
                new PreferenceManager(requireActivity()).saveString(Constant.scr_co_id, mMapScrCoId.get(userSettings.getClassName().toString()));
                fetchSubjectList(mMapScrCoId.get(userSettings.getClassName()));
            }
        });


        mViewModel.getAllSubjects().observe(getViewLifecycleOwner(), new Observer<List<FetchSubjectResponse>>() {
            @Override
            public void onChanged(@Nullable final List<FetchSubjectResponse> subjects) {
                if (subjects != null) {
                    Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
                    ArrayList<String> subjectList = new ArrayList<>();
                    for (FetchSubjectResponse obj : subjects) {
                        if (!subjectList.contains(obj.getSm_sub_name()))
                            subjectList.add(obj.getSm_sub_name());
                    }
                    prepareSubjectChips(subjectList);
                }
            }
        });
    }

    private void saveUserSettingsPreferences(HashMap<String, String> params) {
        Log.d(TAG, "saveUserSettingsPreferences: " + params);
        ProgressDialog.getInstance().show(requireActivity());
        Call<VerifyResponse> call = apiService.updateUserSettings(
                params.get(Constant.u_user_id),
                params.get(Constant.device_id),
                params.get(Constant.appName),
                params.get(Constant.CASE),
                params.get(Constant.ubm_id),
                params.get(Constant.iom_id),
                params.get(Constant.co_id),
                params.get(Constant.dm_id)
        );
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setUserPreference(response.body().getPreferencesList().get(0));
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_test_my);
                    //MainActivity.navController.navigate(R.id.nav_test_my);
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
                apiCallFailureDialog(t);
            }
        });
    }

    private void showAddElementDialog(String title) {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_element_dialog);
        dialog.show();
        TextInputLayout inputLayout = dialog.findViewById(R.id.tvInputLayout);
        inputLayout.getEditText().requestFocus();
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

       /* inputLayout.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });*/
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


    private void fetchClassList(String courseId) {
        Log.d(TAG, "fetchClassList: " + courseId);
        ProgressDialog.getInstance().show(requireActivity());
        Call<ClassList> call = apiService.fetchClasses(courseId);
        call.enqueue(new Callback<ClassList>() {
            @Override
            public void onResponse(Call<ClassList> call, Response<ClassList> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    mViewModel.setClassList(response.body().getClassResponseList());
                    ProgressDialog.getInstance().dismiss();
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
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
        ProgressDialog.getInstance().show(requireActivity());
        Call<VerifyResponse> call = apiService.fetchUserSettings(
                getUserId(),
                getDeviceId(),
                Constant.APP_NAME,
                "L"
        );
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setUserPreference(response.body().getPreferencesList().get(0));
                    Log.e(TAG, "Preference List Size: " + response.body().getPreferencesList().size());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
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
        Log.d(TAG, "fetchSubjectList scr_co_id : " + scr_co_id);
        ProgressDialog.getInstance().show(requireActivity());
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(Constant.SCR_CO_ID);
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

    private void prepareExamChips(String exams) {
        mBinding.examsChipGroup.removeAllViews();
        ArrayList<String> examNames = new ArrayList<>();
        HashMap<String, String> examMap = new HashMap<>();
        String[] strExams = exams.split(",", -1);
        for (int i = 0; i < strExams.length; i++) {
            if (!strExams[i].isEmpty()) {
                String key = strExams[i].split(":0:")[0];
                String value = strExams[i].split(":0:")[1];
                examNames.add(value);
                examMap.put(value, key);
                Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
                chip.setText(value);
                chip.setTag(key);
                chip.setId(i);
                chip.setOnClickListener(this);
                chip.setClickable(true);
                chip.setCheckable(true);
                mBinding.examsChipGroup.addView(chip);
            }
        }
        if (mBinding.examsChipGroup.getChildCount() > 0) {
            mBinding.examsLayout.setVisibility(View.VISIBLE);
        }
    }

    private void prepareSubjectChips(ArrayList<String> subjectList) {
        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
            chip.setText(subjectList.get(i));
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
        Log.d(TAG, "populateClasses list : " + list);
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
