package com.jangletech.qoogol.ui.create_test;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CreatedTestAdapter;
import com.jangletech.qoogol.databinding.FragmentCreatedTestBinding;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.List;

public class CreatedTestFragment extends BaseFragment implements CreatedTestAdapter.CreatedTestListener {

    private static final String TAG = "CreateTestFragment";
    private FragmentCreatedTestBinding mBinding;
    private CreatedTestAdapter mAdapter;
    private CreateTestViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_created_test, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateTestViewModel.class);
        mViewModel.fetchTestList();

        mViewModel.getTestListResponse().observe(getViewLifecycleOwner(), new Observer<TestListResponse>() {
            @Override
            public void onChanged(TestListResponse testListResponse) {
                if (testListResponse != null) {
                    Log.d(TAG, "onChanged: " + testListResponse.getTestList().size());
                    if (testListResponse.getTestList().size() > 0) {
                        mBinding.tvNoTest.setVisibility(View.GONE);
                        setCreatedTestList(testListResponse.getTestList());
                    } else {
                        //NO Tests Found
                        mBinding.tvNoTest.setText("No Tests Created");
                        mBinding.tvNoTest.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mBinding.btnCreateNewTest.setOnClickListener(v -> {
            navigationFromCreateTest(R.id.nav_create_test_basic_details, Bundle.EMPTY);
            //NavHostFragment.findNavController(getParentFragment()).navigate(R.id.nav_create_test_basic_details, Bundle.EMPTY);
        });
    }

//    private void fetchTestList() {
//        ProgressDialog.getInstance().show(getActivity());
//        Call<TestListResponse> call = getApiService().fetchCreatedTestList(AppUtils.getUserId());
//        call.enqueue(new Callback<TestListResponse>() {
//            @Override
//            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
//                ProgressDialog.getInstance().dismiss();
//                if (response.body() != null) {
//                    if (response.body().getResponse().equals("200")) {
//                        setCreatedTestList(response.body().getTestList());
//                    } else if (response.body().getResponse().equals("501")) {
//                        resetSettingAndLogout();
//                    } else {
//                        showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TestListResponse> call, Throwable t) {
//                ProgressDialog.getInstance().dismiss();
//                AppUtils.showToast(getActivity(), t, "");
//                apiCallFailureDialog(t);
//                t.printStackTrace();
//            }
//        });
//    }

    private void setCreatedTestList(List<TestModelNew> createdTestList) {
        Log.i(TAG, "setCreatedTestList Size : " + createdTestList.size());
        mAdapter = new CreatedTestAdapter(getActivity(), createdTestList, this);
        mBinding.testRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.testRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onValidateClick(TestModelNew testModelNew) {
        Log.i(TAG, "onValidateClick : " + testModelNew.getTm_id());
    }

    @Override
    public void onEditClick(TestModelNew testModelNew) {
        Log.i(TAG, "onEditClick : " + testModelNew.getTm_id());
        navigationFromCreateTest(R.id.nav_create_test_basic_details, Bundle.EMPTY);
        //NavHostFragment.findNavController(getParentFragment()).navigate(R.id.nav_create_test_basic_details, Bundle.EMPTY);
    }
}
