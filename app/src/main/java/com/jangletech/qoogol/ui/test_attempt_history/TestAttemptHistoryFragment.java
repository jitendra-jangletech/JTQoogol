package com.jangletech.qoogol.ui.test_attempt_history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.adapter.AttemptHistoryAdapter;
import com.jangletech.qoogol.databinding.FragmentTestAttemptHistoryBinding;
import com.jangletech.qoogol.model.AttemptedTest;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;

import java.util.List;


public class TestAttemptHistoryFragment extends BaseFragment implements AttemptHistoryAdapter.AttemptedTestClickListener {

    private FragmentTestAttemptHistoryBinding mBinding;
    private AttemptHistoryAdapter attemptHistoryAdapter;
    private List<AttemptedTest> attemptedTests;
    private TestModelNew testModelNew;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_attempt_history, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        if (getArguments() != null) {
            testModelNew = (TestModelNew) getArguments().getSerializable("PARAMS");
            attemptedTests = testModelNew.getAttemptedTests();
            if (attemptedTests != null && attemptedTests.size() > 0) {
                attemptHistoryAdapter = new AttemptHistoryAdapter(requireActivity(), attemptedTests, this);
                mBinding.attemptTestRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                mBinding.attemptTestRecyclerView.setAdapter(attemptHistoryAdapter);
            }else{
                mBinding.tvNoHistory.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemClick(AttemptedTest attemptedTest) {
        Intent intent = new Intent(getActivity(), PracticeTestActivity.class);
        intent.putExtra("FLAG","ATTEMPTED");
        intent.putExtra(Constant.TM_ID,Integer.parseInt(attemptedTest.getTt_id()));
        startActivity(intent);
    }
}
