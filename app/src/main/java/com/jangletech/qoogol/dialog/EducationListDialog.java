package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.adapter.EducationAdapter;
import com.jangletech.qoogol.databinding.DialogEduListBinding;
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
    private DialogEduListBinding mBinding;
    private Context mContext;
    private List<Education> educations = new ArrayList<>();
    private EducationAdapter educationAdapter;
    private Call<FetchEducationResponse> call;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public EducationListDialog(@NonNull Context mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding.eduRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        educationAdapter = new EducationAdapter(mContext, educations, this, 0);
        mBinding.eduRecyclerView.setAdapter(educationAdapter);
        fetchEducationDetails();
    }


    private void fetchEducationDetails() {
        ProgressDialog.getInstance().show(mContext);
        call = apiService.fetchUserEdu(AppUtils.getUserId(), "L", AppUtils.getDeviceId(), Constant.APP_NAME);
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

    private void setEducationList(List<Education> educations) {
        educations.clear();
        educations.addAll(educations);
        educationAdapter.updateList(educations);
    }

    @Override
    public void onItemClick(Education education) {

    }

    @Override
    public void onDeleteClick(Education education, int position) {

    }
}
