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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.EducationAdapter;
import com.jangletech.qoogol.databinding.FragmentSyllabusBinding;
import com.jangletech.qoogol.dialog.EducationListDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.FetchEducationResponse;
import com.jangletech.qoogol.model.SyllabusChapter;
import com.jangletech.qoogol.model.SyllabusSubject;
import com.jangletech.qoogol.model.UserPreferenceResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.educational_info.AddEduDialog;
import com.jangletech.qoogol.ui.educational_info.EducationInfoViewModel;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyllabusFragment extends BaseFragment implements View.OnClickListener, EducationListDialog.EducationDialogClickListener, AddEduDialog.ApiCallListener, EducationAdapter.EducationItemClickListener {

    private static final String TAG = "SettingsFragment";
    private MyTestViewModel mViewModel;
    private EducationInfoViewModel educationInfoViewModel;
    private boolean flag = false;
    private EducationAdapter educationAdapter;
    private List<Education> educationList;
    private FragmentSyllabusBinding mBinding;
    private UserPreferenceResponse response;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private HashMap<String, String> params = new HashMap<>();
    private Call<FetchEducationResponse> call;

    public static SyllabusFragment newInstance() {
        return new SyllabusFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_syllabus, container, false);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        educationInfoViewModel = ViewModelProviders.of(this).get(EducationInfoViewModel.class);
        Log.d(TAG, "onCreateView UEID : " + TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id));
        params.put(Constant.selected_ue_id, TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id));
        params.put(Constant.appName, Constant.APP_NAME);
        params.put(Constant.u_user_id, getUserId(getActivity()));
        params.put(Constant.device_id, getDeviceId(getActivity()));
        params.put(Constant.CASE, "L");
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
        fetchEducationDetails();
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

        educationInfoViewModel.getAllEducations(getUserId(getContext())).observe(getViewLifecycleOwner(), educations -> {
            Log.d(TAG, "onChanged Education List Size : " + educations.size());
            if (educations != null) {
                educationList = educations;
                if (educations.size() > 0) {
                    mBinding.mainLayout.setVisibility(View.VISIBLE);
                    mBinding.btnAddEdu.setVisibility(View.GONE);
                    mBinding.btnSave.setVisibility(View.VISIBLE);
                    setEducationListAdapter(educations);
                } else {
                    mBinding.mainLayout.setVisibility(View.GONE);
                    mBinding.btnAddEdu.setVisibility(View.VISIBLE);
                    mBinding.btnSave.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.getPreferences().observe(getViewLifecycleOwner(), new Observer<UserPreferenceResponse>() {
            @Override
            public void onChanged(UserPreferenceResponse userPreferences) {
                if (userPreferences != null) {
                    response = userPreferences;

                    Log.d(TAG, "onChanged UeId : " + userPreferences.getSelectedUeId());

                    saveString(getActivity(), Constant.selected_ue_id, userPreferences.getSelectedUeId());
                    saveString(getActivity(), Constant.subjectName, userPreferences.getSubjectName());
                    saveString(getActivity(), Constant.chapterName1, userPreferences.getChapterName1());
                    saveString(getActivity(), Constant.chapterName2, userPreferences.getChapterName2());
                    saveString(getActivity(), Constant.chapterName3, userPreferences.getChapterName3());

                    mBinding.subjectsChipGrp.removeAllViews();
                    mBinding.chapterChipGrp.removeAllViews();

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

        mBinding.btnSave.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_test_my, Bundle.EMPTY);
        });

        mBinding.btnAddEdu.setOnClickListener(v -> {
            AddEduDialog addEduDialog = new AddEduDialog(getActivity(), null, false, this, 0);
            addEduDialog.show();
        });

        /*mBinding.rootLayout.setOnClickListener(v -> {
            if (response != null) {
                new EducationListDialog(getActivity(), response.getSelectedUeId(), this)
                        .show();
            } else {
                new EducationListDialog(getActivity(), "", this)
                        .show();
            }
        });*/
    }

    private void setEducationListAdapter(List<Education> educationList) {
        Log.d(TAG, "setEducationListAdapter: " + educationList.size());
        educationAdapter = new EducationAdapter(requireActivity(), educationList, this, Module.Syllabus.toString(), TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id));
        mBinding.recyclerview.setHasFixedSize(true);
        mBinding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true));
        mBinding.recyclerview.setAdapter(educationAdapter);
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
        if (list.size() > 0) {
            mBinding.subjectLayout.setVisibility(View.VISIBLE);
            mBinding.chapterLayout.setVisibility(View.VISIBLE);
        } else {
            mBinding.subjectLayout.setVisibility(View.GONE);
            mBinding.chapterLayout.setVisibility(View.GONE);
        }
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
                saveString(getActivity(), Constant.subjectName, response.getSubjectName());
                chip.setChecked(true);
            }
            mBinding.subjectsChipGrp.addView(chip);
        }
    }

    private void prepareChapterChips(List<SyllabusChapter> list) {
//        if (list.size() > 0) {
//            mBinding.chapterLayout.setVisibility(View.VISIBLE);
//        } else {
//            mBinding.chapterLayout.setVisibility(View.GONE);
//        }
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

    private void fetchEducationDetails() {
        ProgressDialog.getInstance().show(getActivity());
        call = apiService.fetchUserEdu(getUserId(getActivity()), "L", getDeviceId(getActivity()), Constant.APP_NAME);
        call.enqueue(new Callback<FetchEducationResponse>() {
            @Override
            public void onResponse(Call<FetchEducationResponse> call, Response<FetchEducationResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null &&
                        response.body().getResponseCode().equals("200")) {
                    Log.d(TAG, "onResponse List : " + response.body().getEducationList());
                    educationInfoViewModel.insert(response.body().getEducationList());
                } else {
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<FetchEducationResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
                apiCallFailureDialog(t);
            }
        });
    }

    private void fetchUpdatePreferences(HashMap<String, String> params) {
        Log.d(TAG, "fetchUpdatePreferences PARAMS : " + params);
        Log.d(TAG, "UEID FMK : " + TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id));
        ProgressDialog.getInstance().show(requireActivity());
        Call<UserPreferenceResponse> call = apiService.fetchUserSyllabus(
                params.get(Constant.u_user_id),
                params.get(Constant.device_id),
                params.get(Constant.appName),
                params.get(Constant.CASE),
                TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id),
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
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserPreferenceResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
                AppUtils.showToast(getActivity(), t, "");
            }
        });
    }

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
        if (education != null) {
            Log.d(TAG, "onSaveButtonClick UEID : " + education.getUe_id());
            saveString(getActivity(), Constant.selected_ue_id, education.getUe_id());
            TinyDB.getInstance(getActivity()).putString(Constant.selected_ue_id, education.getUe_id());
            /*mBinding.tvUniversity.setText(education.getUbm_board_name());
            mBinding.tvInstitute.setText(education.getIom_name());
            mBinding.tvDegree.setText(education.getDm_degree_name());
            mBinding.tvCourse.setText(education.getCo_name());
            mBinding.tvCourseYear.setText(education.getUe_cy_num());
            mBinding.tvStartDate.setText(DateUtils.getFormattedDate(education.getUe_startdate()));
            mBinding.tvEndDate.setText(DateUtils.getFormattedDate(education.getUe_enddate()));*/
            params.put(Constant.CASE, "U");
            params.put(Constant.selected_ue_id, education.getUe_id());
            params.put(Constant.subjectId, "");
            params.put(Constant.chapterId1, "");
            params.put(Constant.chapterId2, "");
            params.put(Constant.chapterId3, "");
            fetchUpdatePreferences(params);
            showToast("Education Preference Updated.");
        } else {
            Log.d(TAG, "onSaveButtonClick Education Null: ");
        }
    }

    @Override
    public void onAddEduClick() {
        AddEduDialog addEduDialog = new AddEduDialog(getActivity(), null, false, this, 0);
        addEduDialog.show();
    }

    @Override
    public void onSuccess() {
        Log.d(TAG, "Education Added Successfully.");
        fetchEducationDetails();
        fetchUpdatePreferences(params);
    }

    @Override
    public void onDialogEduDelete(Education education, int pos) {

    }

    @Override
    public void onItemClick(Education education, int position) {
        TinyDB.getInstance(getActivity()).putString(Constant.selected_ue_id, education.getUe_id());
        params.put(Constant.CASE, "U");
        params.put(Constant.selected_ue_id, education.getUe_id());
        params.put(Constant.subjectId, "");
        params.put(Constant.chapterId1, "");
        params.put(Constant.chapterId2, "");
        params.put(Constant.chapterId3, "");
        fetchUpdatePreferences(params);
    }

    @Override
    public void onDeleteClick(Education education, int position) {

    }
}
