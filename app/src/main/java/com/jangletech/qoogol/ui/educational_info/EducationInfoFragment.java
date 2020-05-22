package com.jangletech.qoogol.ui.educational_info;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.EducationAdapter;
import com.jangletech.qoogol.databinding.FragmentEducationInfoBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.FetchEducationResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EducationInfoFragment extends BaseFragment implements EducationAdapter.EducationItemClickListener
        , View.OnClickListener, AddEduDialog.ApiCallListener {

    private static final String TAG = "EducationInfoFragment";
    private EducationInfoViewModel mViewModel;
    private FragmentEducationInfoBinding mBinding;
    private EducationAdapter educationAdapter;
    private List<Education> educationList;
    private List<FetchEducationResponse> fetchEducationResponseList = new ArrayList();
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static EducationInfoFragment newInstance() {
        return new EducationInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(EducationInfoViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_education_info, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        mBinding.addedu.setOnClickListener(this);
        fetchEducationDetails();
        mViewModel.getEducationList().observe(getActivity(), new Observer<List<Education>>() {
            @Override
            public void onChanged(@Nullable final List<Education> educations) {
                Log.d(TAG, "onChanged Education List Size : " + educations.size());
                educationList  = educations;
                setEducationListAdapter(educations);
            }
        });
    }

    private void setEducationListAdapter(List<Education> educationList){
        educationAdapter = new EducationAdapter(requireActivity(),educationList,this);
        mBinding.educationListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.educationListRecyclerView.setAdapter(educationAdapter);
    }

    private void fetchEducationDetails() {
        ProgressDialog.getInstance().show(getActivity());
        Call<FetchEducationResponse> call = apiService.fetchUserEdu(new PreferenceManager(getActivity()).getInt(Constant.USER_ID),
                "L", getDeviceId(), Constant.APP_NAME);
        call.enqueue(new Callback<FetchEducationResponse>() {
            @Override
            public void onResponse(Call<FetchEducationResponse> call, Response<FetchEducationResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    mViewModel.setEducationList(response.body().getEducationList());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<FetchEducationResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void showEducationDialog(Education education){
        AddEduDialog addEduDialog = new AddEduDialog(getActivity(), education,this);
        addEduDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume: ");
        //fetchEducationDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addedu:
                showEducationDialog(null);
                break;
        }
    }

    @Override
    public void onItemClick(Education education) {
        showEducationDialog(education);
    }

    @Override
    public void onDeleteClick(Education education) {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
        builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete education?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    @Override
    public void onSuccess() {
        fetchEducationDetails();
    }
}
