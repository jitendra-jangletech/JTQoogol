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
import com.jangletech.qoogol.databinding.ImageItemBindingImpl;
import com.jangletech.qoogol.dialog.EducationListDialog;
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
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBinding.tvBoard.setOnClickListener(v->{
            new EducationListDialog(getActivity())
                    .show();
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



    private void fetchUserPreferences() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<VerifyResponse> call = apiService.fetchUserSettings(
                getUserId(getActivity()),
                getDeviceId(getActivity()),
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

//    private void prepareChapterChips(String chapters) {
//        mBinding.chaptersChipGroup.removeAllViews();
//        ArrayList<String> examNames = new ArrayList<>();
//        HashMap<String, String> examMap = new HashMap<>();
//        String[] strExams = chapters.split(",", -1);
//        for (int i = 0; i < strExams.length; i++) {
//            if (!strExams[i].isEmpty()) {
//                String key = strExams[i].split(":0:")[0];
//                String value = strExams[i].split(":0:")[1];
//                examNames.add(value);
//                examMap.put(value, key);
//                Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
//                chip.setText(value);
//                chip.setTag(key);
//                chip.setId(i);
//                chip.setOnClickListener(this);
//                chip.setClickable(true);
//                chip.setCheckable(true);
//                mBinding.chaptersChipGroup.addView(chip);
//            }
//        }
//        if (mBinding.chaptersChipGroup.getChildCount() > 0) {
//            mBinding.chaptersLayout.setVisibility(View.VISIBLE);
//        }
//    }


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
