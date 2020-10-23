package com.jangletech.qoogol.ui.educational_info;

import android.content.Context;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.EducationAdapter;
import com.jangletech.qoogol.databinding.FragmentEducationInfoBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.FetchEducationResponse;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.fetch_loged_in_user;

public class EducationInfoFragment extends BaseFragment implements
        EducationAdapter.EducationItemClickListener
        , View.OnClickListener, AddEduDialog.ApiCallListener {

    private static final String TAG = "EducationInfoFragment";
    private EducationInfoViewModel mViewModel;
    private FragmentEducationInfoBinding mBinding;
    private EducationAdapter educationAdapter;
    private List<Education> educationList;
    private Context mContext;
    private List<FetchEducationResponse> fetchEducationResponseList = new ArrayList();
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private String userid = "";
    private String ueId = "";
    private PreferenceManager mSettings;
    private Call<FetchEducationResponse> call;
    private boolean isFragmentVisible = false;

    public static EducationInfoFragment newInstance() {
        return new EducationInfoFragment();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFragmentVisible) {
            fetchEducationDetails(fetch_loged_in_user);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_education_info, container, false);
        mBinding.setLifecycleOwner(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EducationInfoViewModel.class);
        initViews();
        if (isFragmentVisible && isFragmentVisible) {
            fetchEducationDetails(fetch_loged_in_user);
        }
    }

    private void initViews() {
        mSettings = new PreferenceManager(getActivity());
        if (!isFragmentVisible) {
            fetchEducationDetails(fetch_loged_in_user);
            mBinding.addedu.setOnClickListener(this);
        }

        mViewModel.getAllEducations(getUserId(getContext())).observe(getViewLifecycleOwner(), educations -> {
            Log.d(TAG, "onChanged Education List Size : " + educations.size());
            if (educations != null) {
                educationList = educations;
                if (educations.size() > 0) {
                    mBinding.emptytv.setVisibility(View.GONE);
                    setEducationListAdapter(educations, fetch_loged_in_user);
                } else {
                    mBinding.emptytv.setText("No Education Added.");
                    mBinding.emptytv.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setEducationListAdapter(List<Education> educationList, int call_from) {
        Log.d(TAG, "setEducationListAdapter: " + educationList.size());
        educationAdapter = new EducationAdapter(requireActivity(), educationList, this, "", "");
        mBinding.educationListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.educationListRecyclerView.setAdapter(educationAdapter);
    }

    private void deleteEdu(HashMap<String, String> params, int pos) {
        Log.d(TAG, "deleteEdu: " + params);
        ProgressDialog.getInstance().show(mContext);
        Call<VerifyResponse> call = apiService.deleteEdu(
                params.get(Constant.u_user_id),
                params.get(Constant.ue_id),
                params.get(Constant.CASE)
        );
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.delete(params.get(Constant.ue_id));
                    educationAdapter.deleteEducation(pos);
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    AppUtils.showToast(getActivity(), null, response.body().getErrorMsg());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                apiCallFailureDialog(t);
            }
        });
    }


    private void fetchEducationDetails(int call_from) {
        ProgressDialog.getInstance().show(getActivity());
        call = apiService.fetchUserEdu(getUserId(getActivity()), "L", getDeviceId(getActivity()), Constant.APP_NAME);
        call.enqueue(new Callback<FetchEducationResponse>() {
            @Override
            public void onResponse(Call<FetchEducationResponse> call, Response<FetchEducationResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null &&
                        response.body().getResponseCode().equals("200")) {
                    Log.d(TAG, "onResponse List : " + response.body().getEducationList());
                    mBinding.emptytv.setText("No Education Added.");
                    mBinding.emptytv.setVisibility(View.VISIBLE);
                    mViewModel.insert(response.body().getEducationList());
                } else {
                    //showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<FetchEducationResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                apiCallFailureDialog(t);
            }
        });
    }

    private void showEducationDialog(Education education, int pos) {
        AddEduDialog addEduDialog = new AddEduDialog(getActivity(), education, false,
                this, pos);
        addEduDialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addedu:
                showEducationDialog(null, 0);
                break;
        }
    }

    @Override
    public void onItemClick(Education education, int pos) {
        showEducationDialog(education, pos);
    }

    @Override
    public void onDeleteClick(Education education, int pos) {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
        builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete education?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put(Constant.u_user_id, getUserId(getActivity()));
                        params.put(Constant.ue_id, education.getUe_id());
                        params.put(Constant.CASE, "D");
                        deleteEdu(params, pos);
                    }
                }).setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onSuccess() {
        fetchEducationDetails(fetch_loged_in_user);
    }

    @Override
    public void onDialogEduDelete(Education education, int pos) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, getUserId(getActivity()));
        params.put(Constant.ue_id, education.getUe_id());
        params.put(Constant.CASE, "D");
        deleteEdu(params, pos);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        educationAdapter = null;
    }
}
