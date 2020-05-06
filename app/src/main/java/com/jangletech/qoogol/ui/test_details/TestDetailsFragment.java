package com.jangletech.qoogol.ui.test_details;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.adapter.QuestionAdapter;
import com.jangletech.qoogol.databinding.TestDetailsFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.QsetType;
import com.jangletech.qoogol.model.QSet;
import com.jangletech.qoogol.model.TestDetailsResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestDetailsFragment extends BaseFragment {

    private static final String TAG = "TestDetailsFragment";
    private TestDetailsViewModel mViewModel;
    private TestDetailsFragmentBinding mBinding;
    private List<QSet> qSets;
    private TestModelNew testModelNew;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static TestDetailsFragment newInstance() {
        return new TestDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_details_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getArguments()!=null){
            testModelNew = (TestModelNew) getArguments().getSerializable(Constant.TEST_NAME);
        }
        mViewModel = ViewModelProviders.of(this).get(TestDetailsViewModel.class);
        fetchTestList();
        mViewModel.getQsetList().observe(getActivity(), new Observer<List<QSet>>() {
            @Override
            public void onChanged(@Nullable final List<QSet> qSetList) {
                Log.d(TAG, "onChanged Size : " + qSetList.size());
                qSets = qSetList;
                sortQSetList(qSetList);
            }
        });

        mBinding.btnStartTest.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), StartTestActivity.class));
        });
    }

   /* public void setLeastScoredQuestionList() {
        List<Question> questionList = new ArrayList<>();
        questionList.clear();
        Question question = new Question(1, "which country owns Alaska Region?", "Scored (5/40)");
        Question question1 = new Question(2, "which country is exporter of titanium?", "Scored (10/60)");
        questionList.add(question);
        questionList.add(question1);
        QuestionAdapter testAdapter = new QuestionAdapter(getActivity(), questionList);
        mBinding.leastScoredQuestRecyclerView.setHasFixedSize(true);
        mBinding.leastScoredQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.leastScoredQuestRecyclerView.setAdapter(testAdapter);
    }*/

   /* public void setTopScoredQuestionList() {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.topScoredQuestRecyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        mBinding.topScoredQuestRecyclerView.addItemDecoration(dividerItemDecoration);
        List<Question> questionList = new ArrayList<>();
        questionList.clear();
        Question question = new Question(1, "Who is Prime Minister of United Kingdom?", "Scored (10/20)");
        Question question1 = new Question(2, "What is story line for parasite movie?", "Scored (95/100)");
        questionList.add(question);
        questionList.add(question1);
        QuestionAdapter testAdapter = new QuestionAdapter(getActivity(), questionList);
        mBinding.topScoredQuestRecyclerView.setHasFixedSize(true);
        mBinding.topScoredQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.topScoredQuestRecyclerView.setAdapter(testAdapter);
    }*/

    public void sortQSetList(List<QSet> qSetList) {

        //set Test Title & Description
        mBinding.tvTestTitle.setText(testModelNew.getTm_name());
        mBinding.tvTestDescription.setText(testModelNew.getTest_description());

        List<QSet> qsetRecentList = new ArrayList<>();
        List<QSet> qsetTopScoreList = new ArrayList<>();
        List<QSet> qsetLowScoreList = new ArrayList<>();
        qsetRecentList.clear();
        qsetTopScoreList.clear();
        qsetLowScoreList.clear();
        for (QSet qset: qSetList) {
            if(qset.getqSet().equals(QsetType.Recent.toString())){
                qsetRecentList.add(qset);
            }

            if(qset.getqSet().equals(QsetType.LowScore.toString())){
                qsetLowScoreList.add(qset);
            }

            if(qset.getqSet().equals(QsetType.TopScore.toString())){
                qsetTopScoreList.add(qset);
            }
        }

        setRecentQsetAdapter(qsetRecentList);
        setTopScoredQsetAdapter(qsetTopScoreList);
        setLeastScoredQsetAdapter(qsetLowScoreList);

    }

    private void setRecentQsetAdapter(List<QSet> qSets){
        QuestionAdapter adapter = new QuestionAdapter(getActivity(), qSets);
        mBinding.recentlyAddedQuestRecyclerView.setHasFixedSize(true);
        mBinding.recentlyAddedQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recentlyAddedQuestRecyclerView.setAdapter(adapter);
    }

    private void setTopScoredQsetAdapter(List<QSet> qSets){
        QuestionAdapter adapter = new QuestionAdapter(getActivity(), qSets);
        mBinding.topScoredQuestRecyclerView.setHasFixedSize(true);
        mBinding.topScoredQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.topScoredQuestRecyclerView.setAdapter(adapter);
    }

    private void setLeastScoredQsetAdapter(List<QSet> qSets){
        QuestionAdapter adapter = new QuestionAdapter(getActivity(), qSets);
        mBinding.leastScoredQuestRecyclerView.setHasFixedSize(true);
        mBinding.leastScoredQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.leastScoredQuestRecyclerView.setAdapter(adapter);
    }

    private void fetchTestList() {
        ProgressDialog.getInstance().show(getActivity());
        Call<TestDetailsResponse> call = apiService.fetchTestDetails(1003, testModelNew.getTm_id());//todo change userId and tmIdd
        call.enqueue(new Callback<TestDetailsResponse>() {
            @Override
            public void onResponse(Call<TestDetailsResponse> call, Response<TestDetailsResponse> response) {
                ProgressDialog.getInstance().dismiss();
                mViewModel.setQsetList(response.body().getqSetList());
                Log.d(TAG, "onResponse: " + response.body().getqSetList());
                Log.d(TAG, "onResponse: " + response.body().getqSetList().size());
            }

            @Override
            public void onFailure(Call<TestDetailsResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!");
                t.printStackTrace();
            }
        });
    }

}
