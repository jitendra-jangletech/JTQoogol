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
import com.jangletech.qoogol.adapter.SectionAdapter;
import com.jangletech.qoogol.databinding.FragmentTestSectionBinding;
import com.jangletech.qoogol.dialog.AddNewSectionDialog;
import com.jangletech.qoogol.dialog.AddQuestionDialog;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.TestSection;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TestSectionFragment extends BaseFragment implements AddNewSectionDialog.AddNewSectionClickListener, SectionAdapter.SectionClickListener, AddQuestionDialog.AddQuestionDialogClickListener {

    private static final String TAG = "TestSectionFragment";
    private FragmentTestSectionBinding mBinding;
    private TestSectionViewModel mViewModel;
    private SectionAdapter mAdapter;
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
    }

    @Override
    public void onSaveClick(String name, int marks) {
        Log.i(TAG, "onSaveClick Section Name : " + name);
        Log.i(TAG, "onSaveClick Section Marks : " + marks);
        List<LearningQuestionsNew> questionsNewList = new ArrayList<>();
        testSectionList.add(new TestSection(name, marks, questionsNewList));
        mAdapter.addSectionToList(testSectionList);
    }

    private void setSectionAdapter() {
        mAdapter = new SectionAdapter(getActivity(), testSectionList, this);
        mBinding.directQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.directQuestions.setAdapter(mAdapter);
    }

    @Override
    public void onAddQuestionClick(TestSection testSection,int pos) {
        new AddQuestionDialog(getActivity(),testSection,pos,this)
                .show();
    }

    @Override
    public void onSaveClick(TestSection testSection,int pos) {
        mAdapter.notifyItemChanged(pos,testSection);
    }
}
