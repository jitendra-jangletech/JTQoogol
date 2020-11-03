package com.jangletech.qoogol.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.EducationAdapter;
import com.jangletech.qoogol.databinding.FragmentSyllabusBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.FetchEducationResponse;
import com.jangletech.qoogol.model.SyllabusChapter;
import com.jangletech.qoogol.model.SyllabusSubject;
import com.jangletech.qoogol.model.TestSubjectChapterMaster;
import com.jangletech.qoogol.model.UserPreferenceResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
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


public class SyllabusDialog extends DialogFragment implements EducationAdapter.EducationItemClickListener, View.OnClickListener, AddEduDialog.ApiCallListener {


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
    private SyllabusClickListener listener;

    public SyllabusDialog(SyllabusClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_syllabus, container, false);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        educationInfoViewModel = ViewModelProviders.of(this).get(EducationInfoViewModel.class);
        Log.d(TAG, "onCreateView UEID : " + TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id));
        params.put(Constant.selected_ue_id, TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id));
        params.put(Constant.appName, Constant.APP_NAME);
        params.put(Constant.u_user_id, AppUtils.getUserId());
        params.put(Constant.device_id, AppUtils.getDeviceId());
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

        educationInfoViewModel.getAllEducations(AppUtils.getUserId()).observe(getViewLifecycleOwner(), educations -> {
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

                    TinyDB.getInstance(getActivity()).putString(Constant.subjectName, userPreferences.getSubjectName());
                    TinyDB.getInstance(getActivity()).putString(Constant.subjectId, userPreferences.getSubjectId());

                    //TinyDB.get
                    TestSubjectChapterMaster testSubjectChapterMaster = new TestSubjectChapterMaster();
                    testSubjectChapterMaster.setSections(userPreferences.getSections());
                    testSubjectChapterMaster.setSubjectName(userPreferences.getSubjectName());
                    testSubjectChapterMaster.setSubjectId(userPreferences.getSubjectId());
                    testSubjectChapterMaster.setChap1Name(userPreferences.getChapterName1());
                    testSubjectChapterMaster.setChap1Id(userPreferences.getChapterId1());
                    testSubjectChapterMaster.setChap2Name(userPreferences.getChapterName2());
                    testSubjectChapterMaster.setChap2Id(userPreferences.getChapterId2());
                    testSubjectChapterMaster.setChap3Name(userPreferences.getChapterName3());
                    testSubjectChapterMaster.setChap3Id(userPreferences.getChapterId3());
                    testSubjectChapterMaster.setUeId(userPreferences.getSelectedUeId());

                    Gson gson = new Gson();
                    String json = gson.toJson(testSubjectChapterMaster);
                    TinyDB.getInstance(getActivity()).putString(Constant.TEST_SUBJECT_CHAP, json);

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
            listener.onSyllabusSaveClick();
            dismiss();
            //Navigation.findNavController(requireActivity(), R.id.nav_host_create_test).navigate(R.id.nav_create_test_basic_details, Bundle.EMPTY);
        });

        mBinding.btnAddEdu.setOnClickListener(v -> {
            AddEduDialog addEduDialog = new AddEduDialog(getActivity(), null, false, this, 0);
            addEduDialog.show();
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
                //saveString(getActivity(), Constant.subjectName, response.getSubjectName());
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
                AppUtils.showToast(getActivity(), "You can select only 3 chapters.");
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
    public void onSuccess() {

    }

    @Override
    public void onDialogEduDelete(Education education, int pos) {

    }

    private void fetchEducationDetails() {
        ProgressDialog.getInstance().show(getActivity());
        call = apiService.fetchUserEdu(AppUtils.getUserId(), "L", AppUtils.getDeviceId(), Constant.APP_NAME);
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
            }
        });
    }

    public interface SyllabusClickListener {
        void onSyllabusSaveClick();
    }
}
