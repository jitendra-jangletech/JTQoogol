package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.EducationAdapter;
import com.jangletech.qoogol.databinding.DialogEduListingBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.FetchEducationResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EducationListDialog extends Dialog implements EducationAdapter.EducationItemClickListener {

    private static final String TAG = "EducationListDialog";
    private DialogEduListingBinding mBinding;
    private Context mContext;
    private EducationDialogClickListener listener;
    private Activity activity;
    private Education edu;
    private String ueId = "";
    private List<Education> educations = new ArrayList<>();
    private EducationAdapter educationAdapter;
    private Call<FetchEducationResponse> call;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public EducationListDialog(@NonNull Activity mContext, String ueId, EducationDialogClickListener listener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.listener = listener;
        this.ueId = ueId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_edu_listing, null, false);
        setContentView(mBinding.getRoot());
        educationAdapter = new EducationAdapter(mContext, educations, this, Module.Syllabus.toString(), ueId);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.recyclerView.setAdapter(educationAdapter);
        fetchEducationDetails();

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.tvSave.setOnClickListener(v -> {
            //AppUtils.showToast(mContext, "UEID : " + edu.getUe_id());
            /*Log.d(TAG, "onCreate University : " + edu.getUbm_board_name());
            Log.d(TAG, "onCreate Degree : " + edu.getDm_degree_name());
            Log.d(TAG, "onCreate Course : " + edu.getCo_name());
            Log.d(TAG, "onCreate Institute : " + edu.getIom_name());*/
            listener.onSaveButtonClick(edu);
            dismiss();
        });
    }

    private void fetchEducationDetails() {
        call = apiService.fetchUserEdu(
                AppUtils.getUserId(),
                "L",
                AppUtils.getDeviceId(),
                Constant.APP_NAME
        );
        call.enqueue(new Callback<FetchEducationResponse>() {
            @Override
            public void onResponse(Call<FetchEducationResponse> call, Response<FetchEducationResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    educations = response.body().getEducationList();
                    setEducationList(educations);
                } else {
                    AppUtils.showToast(mContext, "Error : " + response.body().getResponseCode());
                }
            }

            @Override
            public void onFailure(Call<FetchEducationResponse> call, Throwable t) {
                AppUtils.showToast(mContext, "Something went wrong!!");
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    private void setEducationList(List<Education> educationList) {
        Log.d(TAG, "setEducationList Size : " + educationList.size());
        educationAdapter.updateList(educationList);
    }

    @Override
    public void onItemClick(Education education) {
        edu = education;
        Log.d(TAG, "onItemClick : " + education.getCo_name());
        Log.d(TAG, "onItemClick : " + education.getDm_degree_name());
        Log.d(TAG, "onItemClick : " + education.getUbm_board_name());
    }

    @Override
    public void onDeleteClick(Education education, int position) {

    }

    public interface EducationDialogClickListener {
        void onSaveButtonClick(Education education);
    }
}
