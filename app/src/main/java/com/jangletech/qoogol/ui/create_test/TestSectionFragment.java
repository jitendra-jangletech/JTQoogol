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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.AddTestQuestionAdapter;
import com.jangletech.qoogol.adapter.Section0QuestAdapter;
import com.jangletech.qoogol.adapter.Section2QuestAdapter;
import com.jangletech.qoogol.adapter.Section3QuestAdapter;
import com.jangletech.qoogol.adapter.SectionAdapter;
import com.jangletech.qoogol.databinding.FragmentTestSectionBinding;
import com.jangletech.qoogol.dialog.AddNewSectionDialog;
import com.jangletech.qoogol.dialog.AddQuestionDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.CreateTestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.SubmitTest;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.model.TestSection;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestSectionFragment extends BaseFragment implements AddNewSectionDialog.AddNewSectionClickListener, SectionAdapter.SectionClickListener, AddQuestionDialog.AddQuestionDialogClickListener, AddTestQuestionAdapter.AddTestQuestionListener, Section2QuestAdapter.Section2TestQuestAdapterListener, Section3QuestAdapter.Section3TestQuestAdapterListener, Section0QuestAdapter.Section0TestQuestAdapterListener {

    private static final String TAG = "TestSectionFragment";
    private FragmentTestSectionBinding mBinding;
    private TestSectionViewModel mViewModel;
    private SectionAdapter mAdapter;
    private AddTestQuestionAdapter addTestQuestionAdapter1;
    private FragmentManager fragmentManager;
    private Section0QuestAdapter section0QuestAdapter;
    private Section2QuestAdapter section2QuestAdapter;
    private Section3QuestAdapter section3QuestAdapter;
    private List<TestSection> testSectionList = new ArrayList<>();
    private List<LearningQuestionsNew> learningQuestionsNewList0 = new ArrayList<>();
    private List<LearningQuestionsNew> learningQuestionsNewList1 = new ArrayList<>();
    private List<LearningQuestionsNew> learningQuestionsNewList2 = new ArrayList<>();
    private List<LearningQuestionsNew> learningQuestionsNewList3 = new ArrayList<>();
    private String tmId = "";

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
        fragmentManager = getParentFragmentManager();
        setSectionAdapter();

        if (getArguments().getString(Constant.tm_id) != null) {
            tmId = getArguments().getString(Constant.tm_id);
            addTestQuestApi(null, "L", -1, -1, null);
        }


        mBinding.btnAddSection.setOnClickListener(v -> {
            DialogFragment addSectionDialog = new AddNewSectionDialog(this);
            addSectionDialog.show(getParentFragmentManager(), "dialog");
        });

        mBinding.tvAddQuestSection1.setOnClickListener(v -> {
            new AddQuestionDialog(getActivity(), 1, fragmentManager, this)
                    .show();
        });

        mBinding.tvAddQuestSection2.setOnClickListener(v -> {
            new AddQuestionDialog(getActivity(), 2, fragmentManager, this)
                    .show();
        });

        mBinding.tvAddQuestSection3.setOnClickListener(v -> {
            new AddQuestionDialog(getActivity(), 3, fragmentManager, this)
                    .show();
        });

        mBinding.tvSection0.setOnClickListener(v -> {
            new AddQuestionDialog(getActivity(), 0, fragmentManager, this)
                    .show();
        });
    }

    @Override
    public void onNewSectionSaveClick(String name, int marks, int pos) {
        Log.i(TAG, "onSaveClick Section Name : " + name);
        Log.i(TAG, "onSaveClick Section Marks : " + marks);
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
            //mBinding.section1Layout.setVisibility(View.GONE);
            //mBinding.section2Layout.setVisibility(View.GONE);
            //mBinding.section3Layout.setVisibility(View.GONE);

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
        new AddQuestionDialog(getActivity(), pos, fragmentManager, this)
                .show();
    }

    @Override
    public void onTestQuestSaveClick(List<LearningQuestionsNew> learningQuestionsNewList, int pos) {
        Log.i(TAG, "onSaveClick  Position: " + pos);
        List<LearningQuestionsNew> newTempList = new ArrayList<>();
        for (LearningQuestionsNew questionsNew : learningQuestionsNewList) {
            if (questionsNew.isSelected()) {
                newTempList.add(questionsNew);
            }
        }

        if (pos == 1) {
            addTestQuest(newTempList);
            learningQuestionsNewList1 = newTempList;
            addTestQuestionAdapter1 = new AddTestQuestionAdapter(getActivity(), newTempList, false, this);
            mBinding.section1Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section1Recycler.setHasFixedSize(true);
            mBinding.section1Recycler.setAdapter(addTestQuestionAdapter1);
        } else if (pos == 2) {
            addTestQuest(newTempList);
            learningQuestionsNewList2 = newTempList;
            section2QuestAdapter = new Section2QuestAdapter(getActivity(), newTempList, false, this);
            mBinding.section2Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section2Recycler.setHasFixedSize(true);
            mBinding.section2Recycler.setAdapter(section2QuestAdapter);
        } else if (pos == 3) {
            addTestQuest(newTempList);
            learningQuestionsNewList3 = newTempList;
            section3QuestAdapter = new Section3QuestAdapter(getActivity(), newTempList, false, this);
            mBinding.section3Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section3Recycler.setHasFixedSize(true);
            mBinding.section3Recycler.setAdapter(section3QuestAdapter);
        } else {
            learningQuestionsNewList0 = newTempList;
            section0QuestAdapter = new Section0QuestAdapter(getActivity(), newTempList, false, this);
            mBinding.directQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.directQuestions.setHasFixedSize(true);
            mBinding.directQuestions.setAdapter(section0QuestAdapter);
        }
    }

    private void addTestQuest(List<LearningQuestionsNew> newTempList) {
        SubmitTest submitTest = new SubmitTest();

        List<TestQuestionNew> testQuestionNewList = new ArrayList<>();
        try {
            for (LearningQuestionsNew learningQuestionsNew : newTempList) {
                TestQuestionNew testQuestionNew = new TestQuestionNew();
                testQuestionNew.setTq_id(String.valueOf(learningQuestionsNew.getQuestion_id()));
                testQuestionNew.setTq_q_id(learningQuestionsNew.getQuestion_id());
                testQuestionNew.setTq_marks(Double.parseDouble(learningQuestionsNew.getMarks()));
                testQuestionNew.setQ_quest(learningQuestionsNew.getQuestion());
                testQuestionNew.setQ_quest_desc(learningQuestionsNew.getQuestiondesc());
                testQuestionNewList.add(testQuestionNew);
            }

            submitTest.setTestQuestionNewList(testQuestionNewList);
            Gson gson = new Gson();
            String json = gson.toJson(submitTest);
            Log.i(TAG, "addTestQuest: " + json);
            addTestQuestApi(json, "I", -1, -1, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTestQuestApi(String json, String strCase, int questPos, int sectionPos, LearningQuestionsNew learningQuestionsNew) {
        ProgressDialog.getInstance().show(getActivity());
        Call<CreateTestResponse> call = ApiClient.getInstance().getApi()
                .createModifyTestQuest(
                        AppUtils.getUserId(),
                        AppUtils.getDeviceId(),
                        Constant.APP_NAME,
                        tmId,
                        strCase,
                        json
                );
        call.enqueue(new Callback<CreateTestResponse>() {
            @Override
            public void onResponse(Call<CreateTestResponse> call, Response<CreateTestResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body().getResponse() == 200) {
                    Log.i(TAG, "onResponse: " + response.body());
                    if (strCase.equalsIgnoreCase("L")) {
                        setQuestAdapter(response.body().getTestQuestionNewList());
                    } else if (strCase.equalsIgnoreCase("D")) {
                        if (sectionPos == 1) {
                            addTestQuestionAdapter1.deleteTestQuest(questPos, learningQuestionsNew);
                        } else if (sectionPos == 2) {
                            section2QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
                        } else if (sectionPos == 3) {
                            section3QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
                        } else {
                            section0QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
                        }
                    }
                } else {
                    AppUtils.showToast(getActivity(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<CreateTestResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(getActivity(), t, "");
                apiCallFailureDialog(t);
                t.printStackTrace();
            }
        });
    }

    private void setQuestAdapter(List<TestQuestionNew> testQuestionNewList) {
        mBinding.section0Layout.setVisibility(View.VISIBLE);

        try {
            List<LearningQuestionsNew> list = new ArrayList<>();
            for (TestQuestionNew testQuestionNew : testQuestionNewList) {
                LearningQuestionsNew obj = new LearningQuestionsNew();
                obj.setQuestion_id(testQuestionNew.getTq_q_id());
                obj.setMarks(String.valueOf(testQuestionNew.getTq_marks()));
                list.add(obj);
            }
            Log.d(TAG, "setQuestAdapter: " + list.size());
            addTestQuestionAdapter1 = new AddTestQuestionAdapter(getActivity(), list, false, this);
            mBinding.section1Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section1Recycler.setHasFixedSize(true);
            mBinding.section1Recycler.setAdapter(addTestQuestionAdapter1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* private void setFetchedApiQuestion(List<TestQuestionNew> testQuestionNewList) {
        section0QuestAdapter = new Section0QuestAdapter(getActivity(), response.body().getTestQuestionNewList(), false, this);
        mBinding.directQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.directQuestions.setHasFixedSize(true);
        mBinding.directQuestions.setAdapter(section0QuestAdapter);
    }*/

    @Override
    public void onQuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onRemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        Log.i(TAG, "onRemoveClick Position : " + questPos);
        //addTestQuestionAdapter1.deleteTestQuest(questPos, learningQuestionsNew);
        removeTestQuestApi(learningQuestionsNew, questPos, 1);
    }

    @Override
    public void onSection2QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onSection2RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        //section2QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
        removeTestQuestApi(learningQuestionsNew, questPos, 2);
    }

    @Override
    public void onSection3QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onSection3RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        //section3QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
        removeTestQuestApi(learningQuestionsNew, questPos, 3);
    }

    @Override
    public void onSection0QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onSection0RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        removeTestQuestApi(learningQuestionsNew, questPos, 0);
    }

    private void removeTestQuestApi(LearningQuestionsNew learningQuestionsNew, int questPos, int sectionPos) {
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> testQuestionNewList = new ArrayList<>();

        TestQuestionNew testQuestionNew = new TestQuestionNew();
        testQuestionNew.setTq_id(String.valueOf(learningQuestionsNew.getQuestion_id()));
        testQuestionNew.setTq_q_id(learningQuestionsNew.getQuestion_id());
        testQuestionNew.setTq_marks(1.0);
        testQuestionNewList.add(testQuestionNew);

        submitTest.setTestQuestionNewList(testQuestionNewList);
        Gson gson = new Gson();
        String json = gson.toJson(submitTest);
        Log.i(TAG, "deleteTestQuest: " + json);
        addTestQuestApi(json, "D", questPos, sectionPos, learningQuestionsNew);
    }

}
