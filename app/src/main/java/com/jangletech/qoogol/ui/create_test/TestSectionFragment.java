package com.jangletech.qoogol.ui.create_test;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.AddTestQuestionAdapter;
import com.jangletech.qoogol.adapter.Section2QuestAdapter;
import com.jangletech.qoogol.adapter.Section3QuestAdapter;
import com.jangletech.qoogol.adapter.SectionAdapter;
import com.jangletech.qoogol.databinding.FragmentTestSectionBinding;
import com.jangletech.qoogol.dialog.AddNewSectionDialog;
import com.jangletech.qoogol.dialog.AddQuestionDialog;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.TestSection;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TestSectionFragment extends BaseFragment implements AddNewSectionDialog.AddNewSectionClickListener, SectionAdapter.SectionClickListener, AddQuestionDialog.AddQuestionDialogClickListener, AddTestQuestionAdapter.AddTestQuestionListener, Section2QuestAdapter.Section2TestQuestAdapterListener, Section3QuestAdapter.Section3TestQuestAdapterListener {

    private static final String TAG = "TestSectionFragment";
    private FragmentTestSectionBinding mBinding;
    private TestSectionViewModel mViewModel;
    private SectionAdapter mAdapter;
    private AddTestQuestionAdapter addTestQuestionAdapter1;

    private Section2QuestAdapter section2QuestAdapter;
    private Section3QuestAdapter section3QuestAdapter;
    private List<TestSection> testSectionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_section, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TestSectionViewModel.class);
        setSectionAdapter();
        mBinding.btnAddSection.setOnClickListener(v -> {
            DialogFragment addSectionDialog = new AddNewSectionDialog(this);
            addSectionDialog.show(getParentFragmentManager(), "dialog");
        });

        mBinding.tvAddQuestSection1.setOnClickListener(v -> {
            new AddQuestionDialog(getActivity(), 1, this)
                    .show();
        });

        mBinding.tvAddQuestSection2.setOnClickListener(v -> {
            new AddQuestionDialog(getActivity(), 2, this)
                    .show();
        });

        mBinding.tvAddQuestSection3.setOnClickListener(v -> {
            new AddQuestionDialog(getActivity(), 3, this)
                    .show();
        });
    }

    @Override
    public void onSaveClick(String name, int marks, int pos) {
        Log.i(TAG, "onSaveClick Section Name : " + name);
        Log.i(TAG, "onSaveClick Section Marks : " + marks);
//        List<LearningQuestionsNew> questionsNewList = new ArrayList<>();
//        testSectionList.add(new TestSection(name, marks, questionsNewList));
//        mAdapter.addSectionToList(testSectionList);
        if (pos == 1) {
            mBinding.section1Layout.setVisibility(View.VISIBLE);
            mBinding.tvSection1.setText(name + " (" + marks + " Marks)");
        } else if (pos == 2) {
            mBinding.section2Layout.setVisibility(View.VISIBLE);
            mBinding.tvSection2.setText(name + " (" + marks + " Marks)");
        } else if (pos == 3) {
            mBinding.section3Layout.setVisibility(View.VISIBLE);
            mBinding.tvSection3.setText(name + " (" + marks + " Marks)");
        } else {
            mBinding.section1Layout.setVisibility(View.GONE);
            mBinding.section2Layout.setVisibility(View.GONE);
            mBinding.section3Layout.setVisibility(View.GONE);

            mBinding.section0Layout.setVisibility(View.VISIBLE);
        }
    }

    private void setSectionAdapter() {
        mAdapter = new SectionAdapter(getActivity(), testSectionList, this);
        mBinding.directQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.directQuestions.setAdapter(mAdapter);
    }

    @Override
    public void onAddQuestionClick(TestSection testSection, int pos) {
        new AddQuestionDialog(getActivity(), pos, this)
                .show();
    }

    @Override
    public void onSaveClick(List<LearningQuestionsNew> learningQuestionsNewList, int pos) {
        if (pos == 1) {
            addTestQuestionAdapter1 = new AddTestQuestionAdapter(getActivity(), learningQuestionsNewList, false, this);
            mBinding.section1Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section1Recycler.setHasFixedSize(true);
            mBinding.section1Recycler.setAdapter(addTestQuestionAdapter1);
        } else if (pos == 2) {
            section2QuestAdapter = new Section2QuestAdapter(getActivity(), learningQuestionsNewList, false, this);
            mBinding.section2Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section2Recycler.setHasFixedSize(true);
            mBinding.section2Recycler.setAdapter(section2QuestAdapter);
        } else if (pos == 3) {
            section3QuestAdapter = new Section3QuestAdapter(getActivity(), learningQuestionsNewList, false, this);
            mBinding.section3Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section3Recycler.setHasFixedSize(true);
            mBinding.section3Recycler.setAdapter(section3QuestAdapter);
        }
    }

    @Override
    public void onQuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onRemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        Log.i(TAG, "onRemoveClick Position : " + questPos);
        addTestQuestionAdapter1.deleteTestQuest(questPos, learningQuestionsNew);
    }

    @Override
    public void onSection2QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onSection2RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        section2QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
    }

    @Override
    public void onSection3QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onSection3RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        section3QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
    }
}
