package com.jangletech.qoogol.ui.educational_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.EducationAdapter;
import com.jangletech.qoogol.databinding.FragmentEducationInfoBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.FetchEducationsObject;
import com.jangletech.qoogol.model.FetchEducationsResponseDto;
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

public class EducationInfoFragment extends Fragment {

    private static final String TAG = "EducationInfoFragment";
    private EducationInfoViewModel mViewModel;
    private FragmentEducationInfoBinding mBinding;
    private List<FetchEducationsObject> fetchEducationsObjectList = new ArrayList();
    ApiInterface apiService = ApiClient.getInstance().getApi();


    public static EducationInfoFragment newInstance() {
        return new EducationInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel =
                ViewModelProviders.of(this).get(EducationInfoViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_education_info, container, false);
        fetchEducationDetails();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(EducationInfoViewModel.class);
        Log.d(TAG, "onActivityCreated EducationInfoFragment : ");

    }

    public void fetchEducationDetails() {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        Log.d(TAG, "fetchEducationDetails userId : " + new PreferenceManager(getContext()).getUserId());
        arguments.put(Constant.user_id, new PreferenceManager(getContext()).getUserId());
        Call<FetchEducationsResponseDto> call = apiService.getEducationDetails(arguments);
        call.enqueue(new Callback<FetchEducationsResponseDto>() {
            @Override
            public void onResponse(Call<FetchEducationsResponseDto> call, Response<FetchEducationsResponseDto> response) {
                if (response.body()!=null && response.body().getObject()!=null && response.body().getStatusCode() == 1) {
                    FetchEducationsResponseDto education = (FetchEducationsResponseDto) response.body();
                    ProgressDialog.getInstance().dismiss();
                    setEducationListUi(education);
                    Log.d(TAG, "onResponse Board: " + education.getObject().get(0).getUnivBrdName());
                }
            }

            @Override
            public void onFailure(Call<FetchEducationsResponseDto> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    public void setEducationListUi(FetchEducationsResponseDto education) {

        fetchEducationsObjectList.clear();
        fetchEducationsObjectList = education.getObject();

        Log.d(TAG, "setEducationListUi Size : " + fetchEducationsObjectList.size());

        for (int i = 0; i <fetchEducationsObjectList.size() ; i++) {
            Log.d(TAG, "fetchEducationsObjectList : "+fetchEducationsObjectList.get(i).getStateName());
        }

        EducationAdapter educationAdapter = new EducationAdapter(getActivity(), fetchEducationsObjectList);

        mBinding.educationListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.educationListRecyclerView.setAdapter(educationAdapter);

    }

}
