package com.jangletech.qoogol.ui.test_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.QuestionAdapter;
import com.jangletech.qoogol.databinding.TestDetailsFragmentBinding;
import com.jangletech.qoogol.model.Question;

import java.util.ArrayList;
import java.util.List;

public class TestDetailsFragment extends Fragment {

    private TestDetailsViewModel mViewModel;
    private TestDetailsFragmentBinding mBinding;

    public static TestDetailsFragment newInstance() {
        return new TestDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.test_details_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestDetailsViewModel.class);
        setLeastScoredQuestionList();
        setTopScoredQuestionList();
        recentlyAddedQuestionList();
    }

    public void setLeastScoredQuestionList(){

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.leastScoredQuestRecyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        mBinding.leastScoredQuestRecyclerView.addItemDecoration(dividerItemDecoration);

        List<Question> questionList = new ArrayList<>();
        questionList.clear();
        Question question = new Question(1,"which country owns Alaska Region?","Scored (5/40)");
        Question question1 = new Question(2,"which country is exporter of titanium?","Scored (10/60)");
        questionList.add(question);
        questionList.add(question1);
        QuestionAdapter testAdapter = new QuestionAdapter(getActivity(), questionList);
        mBinding.leastScoredQuestRecyclerView.setHasFixedSize(true);
        mBinding.leastScoredQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.leastScoredQuestRecyclerView.setAdapter(testAdapter);
    }

    public void setTopScoredQuestionList(){
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.topScoredQuestRecyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        mBinding.topScoredQuestRecyclerView.addItemDecoration(dividerItemDecoration);
        List<Question> questionList = new ArrayList<>();
        questionList.clear();
        Question question = new Question(1,"Who is Prime Minister of United Kingdom?","Scored (10/20)");
        Question question1 = new Question(2,"What is story line for parasite movie?","Scored (95/100)");
        questionList.add(question);
        questionList.add(question1);
        QuestionAdapter testAdapter = new QuestionAdapter(getActivity(), questionList);
        mBinding.topScoredQuestRecyclerView.setHasFixedSize(true);
        mBinding.topScoredQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.topScoredQuestRecyclerView.setAdapter(testAdapter);
    }

    public void recentlyAddedQuestionList(){
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mBinding.recentlyAddedQuestRecyclerView.getContext(),
                new LinearLayoutManager(getContext()).getOrientation());
        mBinding.recentlyAddedQuestRecyclerView.addItemDecoration(dividerItemDecoration);
        List<Question> questionList = new ArrayList<>();
        questionList.clear();
        Question question = new Question(1,"What is Hanta Virus?","Published (27 Mar 2020)");
        Question question1 = new Question(2,"Is there any vaccine available for corona virus?","Published (26 Mar 2020)");
        questionList.add(question);
        questionList.add(question1);
        QuestionAdapter testAdapter = new QuestionAdapter(getActivity(), questionList);
        mBinding.recentlyAddedQuestRecyclerView.setHasFixedSize(true);
        mBinding.recentlyAddedQuestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recentlyAddedQuestRecyclerView.setAdapter(testAdapter);
    }

}
