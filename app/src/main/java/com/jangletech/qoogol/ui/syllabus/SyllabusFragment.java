package com.jangletech.qoogol.ui.syllabus;

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
import com.jangletech.qoogol.databinding.FragmentSyllabusBinding;
import com.jangletech.qoogol.dialog.EducationListDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.SyllabusChapter;
import com.jangletech.qoogol.model.SyllabusSubject;
import com.jangletech.qoogol.model.UserPreferenceResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.educational_info.AddEduDialog;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyllabusFragment extends BaseFragment implements View.OnClickListener, EducationListDialog.EducationDialogClickListener, AddEduDialog.ApiCallListener {

    private static final String TAG = "SettingsFragment";
    private MyTestViewModel mViewModel;
    private boolean flag = false;
    private FragmentSyllabusBinding mBinding;
    private UserPreferenceResponse response;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private HashMap<String, String> params = new HashMap<>();

    public static SyllabusFragment newInstance() {
        return new SyllabusFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_syllabus, container, false);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        params.put(Constant.appName, Constant.APP_NAME);
        params.put(Constant.u_user_id, getUserId(getActivity()));
        params.put(Constant.device_id, getDeviceId(getActivity()));
        params.put(Constant.CASE, "L");
        params.put(Constant.selected_ue_id, "");
        params.put(Constant.subjectId, "");
        params.put(Constant.chapterId1, "");
        params.put(Constant.chapterId2, "");
        params.put(Constant.chapterId3, "");
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchUpdatePreferences(params);

//        mBinding.chapterChipGrp.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ChipGroup chipGroup, int checkedId) {
//                Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//                if (chip != null) {
//                    Log.d(TAG, "initViews : " + chip.getTag().toString());
//                    String[] selectedChapterIds = getSelectedChapters().split(" ", -1);
//                    //if(selectedChapterIds.length == 1)
//                    Log.d(TAG, "onCheckedChanged Length : " + selectedChapterIds.length);
//
////                    for (int i = 0; i < selectedChapterIds.length; i++) {
////                        if (selectedChapterIds[i] != null && !selectedChapterIds[i].isEmpty()) {
////                            if (selectedChapterIds.length == 1 && i == 0)
////                                params.put(Constant.chapterId1, selectedChapterIds[0]);
////                            if (selectedChapterIds.length == 2 && i == 1)
////                                params.put(Constant.chapterId2, selectedChapterIds[1]);
////                            if (selectedChapterIds.length == 3 && i == 2)
////                                params.put(Constant.chapterId3, selectedChapterIds[2]);
////                        }
////                    }
//                }
//            }
//        });

        mBinding.subjectsChipGrp.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int checkedId) {
                Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
                if (chip != null) {
                    Log.d(TAG, "initViews : " + chip.getTag().toString());
                    if (chip.getTag().toString() != null || chip.getTag().toString().isEmpty())
                        params.put(Constant.subjectId, chip.getTag().toString());
                    else
                        params.put(Constant.subjectId, null);

                    params.put(Constant.CASE, "U");
                    params.put(Constant.chapterId1, "");
                    params.put(Constant.chapterId2, "");
                    params.put(Constant.chapterId3, "");
                    fetchUpdatePreferences(params);
                }
            }
        });

        mViewModel.getPreferences().observe(getViewLifecycleOwner(), new Observer<UserPreferenceResponse>() {
            @Override
            public void onChanged(UserPreferenceResponse userPreferences) {
                if (userPreferences != null) {
                    response = userPreferences;
                    //Log.d(TAG, "onChanged University : " + userPreferences.getSubjectName());
                    //Log.d(TAG, "onChanged Institute : " + userPreferences.getSubjectName());
                    //mBinding.tvUniversity.setText(userPreferences.getSubjectId());
                    //mBinding.tvInstitute.setText(userPreferences.getSubjectName());

                    saveString(getActivity(), Constant.subjectName, userPreferences.getSubjectName());
                    saveString(getActivity(), Constant.chapterName1, userPreferences.getChapterName1());
                    saveString(getActivity(), Constant.chapterName2, userPreferences.getChapterName2());
                    saveString(getActivity(), Constant.chapterName3, userPreferences.getChapterName3());

                    if (userPreferences.getSubjectList() != null)
                        prepareSubjectChips(userPreferences.getSubjectList());
                    if (userPreferences.getChapterList() != null)
                        prepareChapterChips(userPreferences.getChapterList());


                    params.put(Constant.selected_ue_id, userPreferences.getSelectedUeId());
                    params.put(Constant.subjectId, userPreferences.getSubjectId());
                    params.put(Constant.chapterId1, userPreferences.getChapterId1());
                    params.put(Constant.chapterId2, userPreferences.getChapterId2());
                    params.put(Constant.chapterId3, userPreferences.getChapterId3());
                }
            }
        });

        mBinding.rootLayout.setOnClickListener(v -> {
            new EducationListDialog(getActivity(), response.getSelectedUeId(), this)
                    .show();
        });
    }

    private String getSelectedChapters() {
        String res = "";
        for (int i = 0; i < mBinding.chapterChipGrp.getChildCount(); i++) {
            Chip chip = (Chip) mBinding.chapterChipGrp.getChildAt(i);
            if (chip != null) {
                if (chip.isChecked())
                    res = res + " " + chip.getTag().toString();
            }
        }
        Log.d(TAG, "getSelectedChapters: " + res.trim());
        if (res != null)
            return res.trim();
        else
            return "";
    }

    private void prepareSubjectChips(List<SyllabusSubject> list) {
        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_new, mBinding.subjectsChipGrp, false);
            chip.setText(list.get(i).getSubjectName());
            chip.setTag(list.get(i).getSubjectId());
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            if (response.getSubjectId().equals(list.get(i).getSubjectId())) {
                Log.d(TAG, "prepareSubjectChips Checked : " + response.getSubjectName());
                chip.setChecked(true);
            }
            mBinding.subjectsChipGrp.addView(chip);
        }
    }

    private void prepareChapterChips(List<SyllabusChapter> list) {
        mBinding.chapterChipGrp.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.chapterChipGrp.getContext()).inflate(R.layout.chip_new, mBinding.chapterChipGrp, false);
            chip.setText(list.get(i).getChapterName());
            chip.setTag(list.get(i).getChapterId());
            chip.setId(i);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            if (response.getChapterId1().equals(list.get(i).getChapterId()) ||
                    response.getChapterId2().equals(list.get(i).getChapterId()) ||
                    response.getChapterId3().equals(list.get(i).getChapterId()))
                chip.setChecked(true);
            mBinding.chapterChipGrp.addView(chip);
        }
    }

//    private void saveUserSettingsPreferences(HashMap<String, String> params) {
//        Log.d(TAG, "saveUserSettingsPreferences: " + params);
//        ProgressDialog.getInstance().show(requireActivity());
//        Call<VerifyResponse> call = apiService.updateUserSettings(
//                params.get(Constant.u_user_id),
//                params.get(Constant.device_id),
//                params.get(Constant.appName),
//                params.get(Constant.CASE),
//                params.get(Constant.ubm_id),
//                params.get(Constant.iom_id),
//                params.get(Constant.co_id),
//                params.get(Constant.dm_id)
//        );
//        call.enqueue(new Callback<VerifyResponse>() {
//            @Override
//            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
//                ProgressDialog.getInstance().dismiss();
//                if (response.body() != null && response.body().getResponse().equals("200")) {
//                    mViewModel.setUserPreference(response.body());
//                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_test_my);
//                    //MainActivity.navController.navigate(R.id.nav_test_my);
//                } else {
//                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<VerifyResponse> call, Throwable t) {
//                ProgressDialog.getInstance().dismiss();
//                showToast("Something went wrong!!");
//                t.printStackTrace();
//                apiCallFailureDialog(t);
//            }
//        });
//    }


    private void fetchUpdatePreferences(HashMap<String, String> params) {
        Log.d(TAG, "fetchUpdatePreferences PARAMS : " + params);
        ProgressDialog.getInstance().show(requireActivity());
        Call<UserPreferenceResponse> call = apiService.fetchUserSyllabus(
                params.get(Constant.u_user_id),
                params.get(Constant.device_id),
                params.get(Constant.appName),
                params.get(Constant.CASE),
                params.get(Constant.selected_ue_id),
                params.get(Constant.subjectId),
                params.get(Constant.chapterId1),
                params.get(Constant.chapterId2),
                params.get(Constant.chapterId3)
        );
        call.enqueue(new Callback<UserPreferenceResponse>() {
            @Override
            public void onResponse(Call<UserPreferenceResponse> call, Response<UserPreferenceResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode() == 200) {
                    mViewModel.setUserPreference(response.body());
                } else {
                    showErrorDialog(requireActivity(), "" + response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<UserPreferenceResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }


//    private void fetchSubjectList(String scr_co_id) {
//        Log.d(TAG, "fetchSubjectList scr_co_id : " + scr_co_id);
//        ProgressDialog.getInstance().show(requireActivity());
//        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(Constant.SCR_CO_ID);
//        call.enqueue(new Callback<FetchSubjectResponseList>() {
//            @Override
//            public void onResponse(Call<FetchSubjectResponseList> call, Response<FetchSubjectResponseList> response) {
//                ProgressDialog.getInstance().dismiss();
//                mViewModel.setAllSubjectList(response.body().getFetchSubjectResponseList());
//            }
//
//            @Override
//            public void onFailure(Call<FetchSubjectResponseList> call, Throwable t) {
//                ProgressDialog.getInstance().dismiss();
//                t.printStackTrace();
//            }
//        });
//    }

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


    /*private void setCheckedChip(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setTextColor(Color.BLACK);
            }
        }
    }*/

//    private void setSelectedExamChips(Chip chip) {
//        showToast("Selected : " + chip.getText().toString());
//        Chip selectedChip = mapExamChips.put(chip.getId(), chip);
//        for (int i = 0; i < mapExamChips.size(); i++) {
//            if (mapExamChips.get(i).isChecked()) {
//                mapExamChips.get(i).setTextColor(Color.WHITE);
//            } else {
//                mapExamChips.get(i).setTextColor(Color.BLACK);
//            }
//        }
//    }


    @Override
    public void onClick(View v) {
        if (v != null) {
            List<String> selectedChapterId = new ArrayList<>();
            Chip chip = (Chip) v;
            int Counter = 0;
            String[] selectedIds = getSelectedChapters().split(" ", -1);
            Log.d(TAG, "onClick: " + selectedIds);
            for (int i = 0; i < selectedIds.length; i++) {
                if (selectedIds[i] != null) {
                    selectedIds[i].trim();
                    if (!selectedIds[i].isEmpty()) {
                        Counter++;
                        selectedChapterId.add(selectedIds[i]);
                    }
                }
            }
            if (Counter > 3) {
                chip.setChecked(false);
                showToast("You can select only 3 chapters.");
            } else {
                //update chapter ids
                for (int i = 0; i < selectedChapterId.size(); i++) {
                    Log.d(TAG, "\n onClick Id : " + selectedChapterId.get(i));
                }
                if (selectedChapterId.size() == 1) {
                    params.put(Constant.chapterId1, selectedChapterId.get(0));
                    params.put(Constant.chapterId2, null);
                    params.put(Constant.chapterId3, null);
                }
                if (selectedChapterId.size() == 2) {
                    params.put(Constant.chapterId1, selectedChapterId.get(0));
                    params.put(Constant.chapterId2, selectedChapterId.get(1));
                    params.put(Constant.chapterId3, null);
                }
                if (selectedChapterId.size() == 3) {
                    params.put(Constant.chapterId1, selectedChapterId.get(0));
                    params.put(Constant.chapterId2, selectedChapterId.get(1));
                    params.put(Constant.chapterId3, selectedChapterId.get(2));
                }
                params.put(Constant.CASE, "U");
                fetchUpdatePreferences(params);
            }
        }
    }

    @Override
    public void onSaveButtonClick(Education education) {
        mBinding.tvUniversity.setText(education.getUbm_board_name());
        mBinding.tvInstitute.setText(education.getIom_name());
        mBinding.tvDegree.setText(education.getDm_degree_name());
        mBinding.tvCourse.setText(education.getCo_name());
        mBinding.tvCourseYear.setText(education.getUe_cy_num());
        mBinding.tvStartDate.setText(DateUtils.getFormattedDate(education.getUe_startdate()));
        mBinding.tvEndDate.setText(DateUtils.getFormattedDate(education.getUe_enddate()));
        params.put(Constant.CASE, "U");
        params.put(Constant.selected_ue_id, education.getUe_id());
        params.put(Constant.subjectId, "");
        params.put(Constant.chapterId1, "");
        params.put(Constant.chapterId2, "");
        params.put(Constant.chapterId3, "");
        fetchUpdatePreferences(params);
        showToast("Education Preference Updated.");
    }

    @Override
    public void onAddEduClick() {
        AddEduDialog addEduDialog = new AddEduDialog(getActivity(), null, false, this);
        addEduDialog.show();
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "Educatio Added Successfully.");
    }
}
