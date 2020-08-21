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
import com.jangletech.qoogol.model.SyllabusChapter;
import com.jangletech.qoogol.model.SyllabusSubject;
import com.jangletech.qoogol.model.UserPreferenceResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.Constant;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyllabusFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SettingsFragment";
    private MyTestViewModel mViewModel;
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
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchUserPreferences();

        mBinding.chapterChipGrp.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int checkedId) {
                Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
                if (chip != null) {
                    Log.d(TAG, "initViews : " + chip.getTag().toString());
                    String[] selectedChapterIds = getSelectedChapters().split(" ", -1);
                    for (int i = 0; i < selectedChapterIds.length; i++) {
                        if (selectedChapterIds[i] != null && !selectedChapterIds[i].isEmpty()) {
                            if (selectedChapterIds.length == 1 && i == 0)
                                params.put(Constant.chapterId1, selectedChapterIds[0]);
                            if (selectedChapterIds.length == 2 && i == 1)
                                params.put(Constant.chapterId2, selectedChapterIds[1]);
                            if (selectedChapterIds.length == 3 && i == 2)
                                params.put(Constant.chapterId3, selectedChapterIds[2]);
                        }
                    }
                }
            }
        });

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
                }
            }
        });

        mViewModel.getPreferences().observe(getViewLifecycleOwner(), new Observer<UserPreferenceResponse>() {
            @Override
            public void onChanged(UserPreferenceResponse userPreferences) {
                if (userPreferences != null) {
                    response = userPreferences;
                    Log.d(TAG, "onChanged University : " + userPreferences.getSubjectName());
                    Log.d(TAG, "onChanged Institute : " + userPreferences.getSubjectName());
                    mBinding.tvUniversity.setText(userPreferences.getSubjectId());
                    mBinding.tvInstitute.setText(userPreferences.getSubjectName());
                    if (userPreferences.getSubjectList() != null)
                        prepareSubjectChips(userPreferences.getSubjectList());
                    if (userPreferences.getChapterList() != null)
                        prepareChapterChips(userPreferences.getChapterList());
                }
            }
        });

        mBinding.rootLayout.setOnClickListener(v -> {
            new EducationListDialog(getActivity())
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
        return res.trim();
    }

    private void prepareSubjectChips(List<SyllabusSubject> list) {
        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_new, mBinding.subjectsChipGrp, false);
            chip.setText(list.get(i).getSubjectName());
            chip.setTag(list.get(i).getSubjectId());
            chip.setId(i);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            if (response.getSubjectId().equals(list.get(i).getSubjectId()))
                chip.setChecked(true);
            mBinding.subjectsChipGrp.addView(chip);
        }
    }

    private void prepareChapterChips(List<SyllabusChapter> list) {
        mBinding.chapterChipGrp.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_new, mBinding.subjectsChipGrp, false);
            chip.setText(list.get(i).getChapterName());
            chip.setTag(list.get(i).getChapterId());
            chip.setId(i);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            if (response.getChapterId1().equalsIgnoreCase(list.get(i).getChapterId()) ||
                    response.getChapterId2().equalsIgnoreCase(list.get(i).getChapterId()) ||
                    response.getChapterId3().equalsIgnoreCase(list.get(i).getChapterId()))
                chip.setChecked(true);
            mBinding.subjectsChipGrp.addView(chip);
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


    private void fetchUserPreferences() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<UserPreferenceResponse> call = apiService.fetchUserSyllabus(
                getUserId(getActivity()),
                getDeviceId(getActivity()),
                Constant.APP_NAME,
                "L"
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

    }
}
