package com.jangletech.qoogol.ui.preference;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.chip.Chip;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentPreferableExamBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.CommonResponseObject;
import com.jangletech.qoogol.model.Exams;
import com.jangletech.qoogol.model.FetchPreferableExamsResponseDto;
import com.jangletech.qoogol.model.UserSelectedExams;
import com.jangletech.qoogol.model.UserSelectedExamsData;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreferenceFragment extends Fragment {

    private static final String TAG = "PreferenceFragment";
    private PreferenceViewModel mViewModel;
    private FragmentPreferableExamBinding mBinding;
    ApiInterface apiService = ApiClient.getInstance().getApi();


    public static PreferenceFragment newInstance() {
        return new PreferenceFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_preferable_exam, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PreferenceViewModel.class);
        Log.d(TAG, "onActivityCreated PreferenceFragment : ");
        fetchApplicableExams();

        mBinding.btnSave.setOnClickListener(v -> {
            getSelectedChips();
        });
    }

    private void getSelectedChips() {
        List<Exams> examsList = new ArrayList<>();
        for (int i = 0; i < mBinding.examChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) mBinding.examChipGroup.getChildAt(i);
            if (chip.isChecked()) {
                Exams exams = new Exams();
                exams.setDispText(chip.getText().toString());
                exams.setValue(chip.getTag().toString());
                examsList.add(exams);
            }
        }
        Log.d(TAG, "getSelectedChips: "+examsList.size());
        saveUserSelectedExams(examsList);
    }


    private void setPreferenceChips(List<Exams> chipTextList, List<UserSelectedExamsData> selectedExamList) {
        for (int i = 0; i < chipTextList.size(); i++) {
            Chip chip = new Chip(getActivity());
            if (selectedExamList != null) {
                for (int j = 0; j < selectedExamList.size(); j++) {
                    if (chipTextList.get(i).getDispText().equals(selectedExamList.get(j).getExamName())
                            && chipTextList.get(i).getValue().equals(selectedExamList.get(j).getUserExamId())) {
                        chip.setChecked(true);
                    }
                }
            }
            chip.setText(chipTextList.get(i).getDispText());
            chip.setTag(chipTextList.get(i).getValue());
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setCheckable(true);
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(getActivity(), R.color.chip_bg_state));
            mBinding.examChipGroup.addView(chip);
        }
        ProgressDialog.getInstance().dismiss();
    }

    private void fetchUserSelectedExams(List<Exams> chipTextList) {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, Integer> arguments = new HashMap<>();
        arguments.put(Constant.user_id, Integer.parseInt(new PreferenceManager(getContext()).getUserId()));
        Call<UserSelectedExams> call = apiService.getUserSelectedExams(arguments);
        call.enqueue(new Callback<UserSelectedExams>() {
            @Override
            public void onResponse(Call<UserSelectedExams> call, Response<UserSelectedExams> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.isSuccessful()) {
                    List<UserSelectedExamsData> userSelectedExamsData = response.body().getObject();
                    setPreferenceChips(chipTextList, userSelectedExamsData);
                } else {
                    Log.e(TAG, "Response Failed:");
                    setPreferenceChips(chipTextList, null);
                }
            }

            @Override
            public void onFailure(Call<UserSelectedExams> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
                setPreferenceChips(chipTextList, null);
            }
        });
    }

    public void saveUserSelectedExams(List<Exams> selectedExamsList) {
        ProgressDialog.getInstance().show(getActivity());
        Call<CommonResponseObject> call = apiService.saveUserSelectedExams(Integer.parseInt(new PreferenceManager(getContext()).getUserId()), selectedExamsList);
        call.enqueue(new Callback<CommonResponseObject>() {
            @Override
            public void onResponse(Call<CommonResponseObject> call, Response<CommonResponseObject> response) {
                ProgressDialog.getInstance().dismiss();
                Log.d(TAG, "onResponse Exams : " + response.body().getStatusCode());
                if (response.body() != null && response.body().getStatusCode() == 1) {
                    Log.d(TAG, "onResponse: "+response.body().getExamsList());
                    setPreferenceChips(response.body().getExamsList(),null);
                    Toast.makeText(getActivity(), ""+response.body().getExamsList(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Preferences updated.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommonResponseObject> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }


    public void fetchApplicableExams() {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        arguments.put(Constant.courseId, "1");

        Call<FetchPreferableExamsResponseDto> call = apiService.getApplicableExams(arguments);
        call.enqueue(new Callback<FetchPreferableExamsResponseDto>() {
            @Override
            public void onResponse(Call<FetchPreferableExamsResponseDto> call, Response<FetchPreferableExamsResponseDto> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getStatusCode().equals("1")) {
                    FetchPreferableExamsResponseDto fetchPreferableExamsResponseDto = (FetchPreferableExamsResponseDto) response.body();
                    fetchUserSelectedExams(fetchPreferableExamsResponseDto.getObject());
                } else {
                    Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FetchPreferableExamsResponseDto> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }
}
