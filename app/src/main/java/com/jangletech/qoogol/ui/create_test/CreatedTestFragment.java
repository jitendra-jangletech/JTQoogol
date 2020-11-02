package com.jangletech.qoogol.ui.create_test;

import android.content.Intent;
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
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.adapter.CreatedTestAdapter;
import com.jangletech.qoogol.databinding.FragmentCreatedTestBinding;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

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
                        mBinding.tvNoTest.setText("No Tests Created");
                        mBinding.tvNoTest.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mBinding.btnCreateNewTest.setOnClickListener(v -> {
            TinyDB.getInstance(getActivity()).putString(Constant.test_mode, Constant.test_mode_new);
            navigationFromCreateTest(R.id.nav_create_test_basic_details, Bundle.EMPTY);
        });
    }

    private void setCreatedTestList(List<TestModelNew> createdTestList) {
        Log.i(TAG, "setCreatedTestList Size : " + createdTestList.size());
        mAdapter = new CreatedTestAdapter(getActivity(), createdTestList, this);
        mBinding.testRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.testRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onValidateClick(TestModelNew testModelNew) {
        Log.i(TAG, "onValidateClick : " + testModelNew.getTm_id());
        Intent intent = new Intent(getActivity(), PracticeTestActivity.class);
        intent.putExtra(Constant.TM_ID, testModelNew.getTm_id());
        intent.putExtra(Constant.PREVIEW_FLAG, Constant.PREVIEW_FLAG);
        startActivity(intent);
    }

    @Override
    public void onEditClick(TestModelNew testModelNew) {
        Log.i(TAG, "onEditClick : " + testModelNew.getTm_id());
        TinyDB.getInstance(getActivity()).putString(Constant.test_mode, Constant.test_mode_edit);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CREATED_TEST", testModelNew);
        navigationFromCreateTest(R.id.nav_create_test_basic_details, bundle);
    }
}
