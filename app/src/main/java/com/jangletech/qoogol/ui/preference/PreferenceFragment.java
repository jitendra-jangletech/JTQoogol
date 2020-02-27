package com.jangletech.qoogol.ui.preference;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.jangletech.qoogol.model.FetchPreferableExamsResponseDto;
import com.jangletech.qoogol.model.Exams;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;

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
        ProgressDialog.getInstance().show(getActivity());
        fetchApplicableExams();
    }

    public void setPreferenceChips(List<Exams> chipTextList) {

        for (int i = 0; i < chipTextList.size(); i++) {
            Chip chip = new Chip(getActivity());
            chip.setText(chipTextList.get(i).getDispText());
            chip.setClickable(true);
            chip.setFocusable(true);
            chip.setCheckable(true);
            chip.setChipBackgroundColor(AppCompatResources.getColorStateList(getActivity(), R.color.chip_bg_state));
            mBinding.examChipGroup.addView(chip);
        }

        ProgressDialog.getInstance().dismiss();
    }

    public void fetchApplicableExams() {
        Map<String, String> arguments = new HashMap<>();
        arguments.put(Constant.courseId, "1");

        Call<FetchPreferableExamsResponseDto> call = apiService.getApplicableExams(arguments);
        call.enqueue(new Callback<FetchPreferableExamsResponseDto>() {
            @Override
            public void onResponse(Call<FetchPreferableExamsResponseDto> call, Response<FetchPreferableExamsResponseDto> response) {
                if (response.isSuccessful()) {
                    FetchPreferableExamsResponseDto fetchPreferableExamsResponseDto = (FetchPreferableExamsResponseDto) response.body();
                    setPreferenceChips(fetchPreferableExamsResponseDto.getObject());
                    Log.d(TAG, "onResponse fetchPreferableExamsResponseDto : "+ fetchPreferableExamsResponseDto.getObject().get(0).getDispText());
                } else {
                    Log.e(TAG, "Response Failed:");
                }
            }

            @Override
            public void onFailure(Call<FetchPreferableExamsResponseDto> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
