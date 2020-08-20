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
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.fetch_loged_in_user;
import static com.jangletech.qoogol.util.Constant.fetch_other_user;

public class EducationInfoFragment extends BaseFragment implements EducationAdapter.EducationItemClickListener
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
    private PreferenceManager mSettings;
    Call<FetchEducationResponse> call;

    public static EducationInfoFragment newInstance() {
        return new EducationInfoFragment();
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
    }

    private void initViews() {
        mSettings = new PreferenceManager(getActivity());
        userid = mSettings.getProfileFetchId();
        if (userid.equalsIgnoreCase(mSettings.getUserId())) {
            fetchEducationDetails(fetch_loged_in_user);
            mBinding.addedu.setOnClickListener(this);
        } else {
            fetchEducationDetails(fetch_other_user);
            mBinding.addedu.setVisibility(View.GONE);
        }

        mViewModel.getAllEducations(getUserId(getContext())).observe(getViewLifecycleOwner(), educations -> {
            Log.d(TAG, "onChanged Education List Size : " + educations.size());
            if (educations != null) {
                educationList = educations;
                if (userid.equalsIgnoreCase(mSettings.getUserId()))
                    setEducationListAdapter(educations, fetch_loged_in_user);
                else
                    setEducationListAdapter(educations, fetch_other_user);
            }
        });

        /*mViewModel.getDeleteResponse().observe(getViewLifecycleOwner(), new Observer<VerifyResponse>() {
            @Override
            public void onChanged(VerifyResponse verifyResponse) {
                if(verifyResponse!=null){
                    //todo delete edu from list
                }
            }
        });*/
    }

    private void setEducationListAdapter(List<Education> educationList, int call_from) {
        Log.d(TAG, "setEducationListAdapter: " + educationList.size());
        educationAdapter = new EducationAdapter(requireActivity(), educationList, this, call_from);
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
                    //mViewModel.setDeleteResponse(response.body());
                    mViewModel.delete(params.get(Constant.ue_id));
                    educationAdapter.deleteEducation(pos);
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }


    private void fetchEducationDetails(int call_from) {
        ProgressDialog.getInstance().show(getActivity());

        if (call_from == fetch_loged_in_user)
            call = apiService.fetchUserEdu(userid, "L", getDeviceId(getActivity()), Constant.APP_NAME);
        else
            call = apiService.fetchOtherUSersUserEdu(mSettings.getUserId(), "L", getDeviceId(getActivity()), Constant.APP_NAME, userid);

        call.enqueue(new Callback<FetchEducationResponse>() {
            @Override
            public void onResponse(Call<FetchEducationResponse> call, Response<FetchEducationResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    //mViewModel.delete();
                    mViewModel.insert(response.body().getEducationList());
                    //mViewModel.setEducationList(response.body().getEducationList());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<FetchEducationResponse> call, Throwable t) {
                showToast("Something went wrong!!");
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    private void showEducationDialog(Education education) {
        AddEduDialog addEduDialog = new AddEduDialog(getActivity(), education, false,this);
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
    public void onDetach() {
        super.onDetach();
        educationAdapter = null;
    }
}
