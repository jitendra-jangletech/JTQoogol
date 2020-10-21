package com.jangletech.qoogol.ui.create_test;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.jangletech.qoogol.util.TinyDB;

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
    private int qPosition = -1;
    private int secPos = -1;
    private int testTotalMarks = 0;

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
        testTotalMarks = Integer.parseInt(TinyDB.getInstance(getActivity()).getString(Constant.tm_tot_marks));
        mBinding.tvTotalMarks.setText("Test Total Marks : " + testTotalMarks);

        try {
            if (getArguments().getString(Constant.tm_id) != null) {
                tmId = getArguments().getString(Constant.tm_id);
                addTestQuestApi(null, "L", -1, -1, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBinding.btnAddSection.setOnClickListener(v -> {
            try {
                DialogFragment addSectionDialog = new AddNewSectionDialog(this, Constant.section, 0);
                addSectionDialog.show(getParentFragmentManager(), "dialog");
            } catch (Exception e) {
                e.printStackTrace();
            }
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

        mBinding.btnSubmitTest.setOnClickListener(v -> {
            try {
                if (learningQuestionsNewList0.isEmpty() && learningQuestionsNewList1.isEmpty()) {
                    //showToast("Add Questions to Section.", Toast.LENGTH_LONG);
                    showAlert("Add Questions to Section.");
                    return;
                } else if (getAllSectionMarks() > testTotalMarks) {
                    //showToast("All section Questions Marks should be equal to test total marks", Toast.LENGTH_LONG);
                    showAlert("All section Questions Marks should be equal to test total marks");
                    return;
                } else if (getAllSectionMarks() < testTotalMarks) {
                    //showToast("All section Questions Marks should be equal to test total marks", Toast.LENGTH_LONG);
                    showAlert("All section Questions Marks should be equal to test total marks");
                    return;
                } else {
                    //call api to submit Questions
                    List<LearningQuestionsNew> finalTestQuestList = new ArrayList<>();
                    if (!learningQuestionsNewList0.isEmpty()) {
                        finalTestQuestList.addAll(learningQuestionsNewList0);
                    }
                    if (!learningQuestionsNewList1.isEmpty()) {
                        finalTestQuestList.addAll(learningQuestionsNewList1);
                    }
                    if (!learningQuestionsNewList2.isEmpty()) {
                        finalTestQuestList.addAll(learningQuestionsNewList2);
                    }
                    if (!learningQuestionsNewList3.isEmpty()) {
                        finalTestQuestList.addAll(learningQuestionsNewList3);
                    }
                    addTestQuest(finalTestQuestList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private float getAllSectionMarks() {
        return getTotalMarks(learningQuestionsNewList0) + getTotalMarks(learningQuestionsNewList1) +
                getTotalMarks(learningQuestionsNewList2) + getTotalMarks(learningQuestionsNewList3);
    }

    private float getTotalMarks(List<LearningQuestionsNew> list) {
        float totMarks = 0;
        for (LearningQuestionsNew listItem : list) {
            totMarks = totMarks + Float.parseFloat(listItem.getMarks());
        }
        return totMarks;
    }

    @Override
    public void onNewSectionSaveClick(String name, float marks, int pos) {
        Log.i(TAG, "onNewSectionSaveClick Position : " + pos);
        Log.i(TAG, "onSaveClick Section Name : " + name);
        Log.i(TAG, "onSaveClick Section Marks : " + marks);
        if (!name.equalsIgnoreCase("Marks")) {
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
        } else {
            //marks code part
            Log.i(TAG, "onNewSectionSaveClick Section Position: " + secPos);
            Log.i(TAG, "onNewSectionSaveClick Question Position: " + qPosition);
            if (secPos == 1) {
                Log.i(TAG, "onNewSectionSaveClick Quest Pos : " + qPosition);
                LearningQuestionsNew item = learningQuestionsNewList1.get(qPosition);
                item.setMarks(String.valueOf(marks));
                section0QuestAdapter.updateMarks(item, qPosition);
            } else if (secPos == 2) {
                Log.i(TAG, "onNewSectionSaveClick Quest Pos : " + qPosition);
                LearningQuestionsNew item = learningQuestionsNewList2.get(qPosition);
                item.setMarks(String.valueOf(marks));
                section2QuestAdapter.updateMarks(item, qPosition);
            } else if (secPos == 3) {
                Log.i(TAG, "onNewSectionSaveClick Quest Pos : " + qPosition);
                LearningQuestionsNew item = learningQuestionsNewList3.get(qPosition);
                item.setMarks(String.valueOf(marks));
                section3QuestAdapter.updateMarks(item, qPosition);
            } else if (secPos == 0) {
                Log.i(TAG, "onNewSectionSaveClick Quest Pos : " + qPosition);
                LearningQuestionsNew item = learningQuestionsNewList0.get(qPosition);
                item.setMarks(String.valueOf(marks));
                addTestQuestionAdapter1.updateMarks(item, qPosition);
            }
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

    private boolean isAlreadyAdded(List<LearningQuestionsNew> list, LearningQuestionsNew searchItem) {
        boolean isAdded = false;
        for (LearningQuestionsNew item : list) {
            if (item.getQuestion_id() == searchItem.getQuestion_id()) {
                isAdded = true;
                break;
            }
        }
        Log.i(TAG, "isAlreadyAdded : " + isAdded);
        return isAdded;
    }


    @Override
    public void onTestQuestSaveClick(List<LearningQuestionsNew> learningQuestionsNewList, int pos) {
        Log.i(TAG, "onSaveClick  Position: " + pos);
        //List<LearningQuestionsNew> newTempList = new ArrayList<>();
        for (LearningQuestionsNew questionsNew : learningQuestionsNewList) {
            if (questionsNew.isSelected() && pos == 1 && !isAlreadyAdded(learningQuestionsNewList1, questionsNew)) {
                learningQuestionsNewList1.add(questionsNew);
            } else if (questionsNew.isSelected() && pos == 2 && !learningQuestionsNewList2.contains(questionsNew)) {
                learningQuestionsNewList2.add(questionsNew);
            } else if (questionsNew.isSelected() && pos == 3 && !learningQuestionsNewList3.contains(questionsNew)) {
                learningQuestionsNewList3.add(questionsNew);
            } else if (questionsNew.isSelected() && pos == 0 && !isAlreadyAdded(learningQuestionsNewList0, questionsNew)) {
                learningQuestionsNewList0.add(questionsNew);
            }
        }

        if (pos == 1) {
            //addTestQuest(newTempList);
            //learningQuestionsNewList1 = newTempList;
            showHideSubmitButton(learningQuestionsNewList1);
            //calculateMarksValidation(learningQuestionsNewList1, 0);
            section0QuestAdapter = new Section0QuestAdapter(getActivity(), learningQuestionsNewList1, false, this);
            mBinding.section1Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section1Recycler.setHasFixedSize(true);
            mBinding.section1Recycler.setAdapter(section0QuestAdapter);
        } else if (pos == 2) {
            //addTestQuest(newTempList);
            //learningQuestionsNewList2 = newTempList;
            showHideSubmitButton(learningQuestionsNewList2);
            //calculateMarksValidation(learningQuestionsNewList2, 0);
            section2QuestAdapter = new Section2QuestAdapter(getActivity(), learningQuestionsNewList2, false, this);
            mBinding.section2Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section2Recycler.setHasFixedSize(true);
            mBinding.section2Recycler.setAdapter(section2QuestAdapter);
        } else if (pos == 3) {
            //addTestQuest(newTempList);
            //learningQuestionsNewList3 = newTempList;
            showHideSubmitButton(learningQuestionsNewList3);
            //calculateMarksValidation(learningQuestionsNewList3, 0);
            section3QuestAdapter = new Section3QuestAdapter(getActivity(), learningQuestionsNewList3, false, this);
            mBinding.section3Recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.section3Recycler.setHasFixedSize(true);
            mBinding.section3Recycler.setAdapter(section3QuestAdapter);
        } else {
            //learningQuestionsNewList0 = newTempList;
            showHideSubmitButton(learningQuestionsNewList0);
            //calculateMarksValidation(learningQuestionsNewList0, 0);
            Log.i(TAG, "onTestQuestSaveClick List Size : " + learningQuestionsNewList0.size());
            addTestQuestionAdapter1 = new AddTestQuestionAdapter(getActivity(), learningQuestionsNewList0, false, this);
            mBinding.directQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.directQuestions.setHasFixedSize(true);
            mBinding.directQuestions.setAdapter(addTestQuestionAdapter1);
        }
    }

    private void showHideSubmitButton(List<LearningQuestionsNew> list) {
        if (list.size() > 0) {
            mBinding.btnSubmitTest.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnSubmitTest.setVisibility(View.GONE);
        }
    }

    private void calculateMarksValidation(List<LearningQuestionsNew> list, int sectionGivenMarks) {
        try {
            float testTotalMarks = Float.parseFloat(TinyDB.getInstance(getActivity()).getString(Constant.tm_tot_marks));
            float totalSectionMarks = 0;
            for (LearningQuestionsNew learningQuestionsNew : list) {
                totalSectionMarks = totalSectionMarks + Float.parseFloat(learningQuestionsNew.getMarks());
            }

            String msg = "";

            if (totalSectionMarks > testTotalMarks) {
                msg = "Test total marks exceeded by the section marks. please re-arrange marks on tapping each question.";
            }

            if (totalSectionMarks > sectionGivenMarks) {
                msg = "Added question marks are exceeding given section marks.";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Alert")
                    .setMessage(msg)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTestQuest(List<LearningQuestionsNew> newTempList) {
        SubmitTest submitTest = new SubmitTest();
        int seq = 1;

        List<TestQuestionNew> testQuestionNewList = new ArrayList<>();
        try {
            for (LearningQuestionsNew learningQuestionsNew : newTempList) {
                TestQuestionNew testQuestionNew = new TestQuestionNew();
                testQuestionNew.setTq_id(String.valueOf(learningQuestionsNew.getQuestion_id()));
                testQuestionNew.setTq_q_id(learningQuestionsNew.getQuestion_id());
                testQuestionNew.setTq_marks(Double.parseDouble(learningQuestionsNew.getMarks()));
                testQuestionNew.setQ_quest(learningQuestionsNew.getQuestion());
                testQuestionNew.setQ_quest_desc(learningQuestionsNew.getQuestiondesc());
                testQuestionNew.setTq_quest_seq_num(seq);
                testQuestionNew.setTq_duration(learningQuestionsNew.getRecommended_time());
                testQuestionNewList.add(testQuestionNew);
                seq++;
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
                    }
                    if (strCase.equalsIgnoreCase("D")) {
                        if (sectionPos == 1) {
                            section0QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
                        } else if (sectionPos == 2) {
                            section2QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
                        } else if (sectionPos == 3) {
                            section3QuestAdapter.deleteTestQuest(questPos, learningQuestionsNew);
                        } else {
                            addTestQuestionAdapter1.deleteTestQuest(questPos, learningQuestionsNew);
                        }
                    }
                }
                if (strCase.equalsIgnoreCase("I")) {
                    //showToast("Test Created Successfully", Toast.LENGTH_LONG);
                    String mode = TinyDB.getInstance(getActivity()).getString(Constant.test_mode);
                    if (mode.equalsIgnoreCase(Constant.test_mode_edit)) {
                        showToast("Test Updated Successfully.", Toast.LENGTH_LONG);
                    } else {
                        showToast("Test Created Successfully.", Toast.LENGTH_LONG);
                    }
                    navigationFromCreateTest(R.id.nav_create, Bundle.EMPTY);
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
            //List<LearningQuestionsNew> list = new ArrayList<>();
            for (TestQuestionNew testQuestionNew : testQuestionNewList) {
                LearningQuestionsNew obj = new LearningQuestionsNew();
                obj.setQuestion_id(testQuestionNew.getTq_q_id());
                obj.setMarks(String.valueOf(testQuestionNew.getTq_marks()));
                learningQuestionsNewList0.add(obj);
            }
            Log.d(TAG, "setQuestAdapter: " + learningQuestionsNewList0.size());

            if (learningQuestionsNewList0.size() > 0) {
                addTestQuestionAdapter1 = new AddTestQuestionAdapter(getActivity(), learningQuestionsNewList0, false, this);
                mBinding.directQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
                mBinding.directQuestions.setHasFixedSize(true);
                mBinding.directQuestions.setAdapter(addTestQuestionAdapter1);
            } else {
                showToast("No Questions Added.", Toast.LENGTH_SHORT);
                mBinding.section0Layout.setVisibility(View.GONE);
            }
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
        removeTestQuestApi(learningQuestionsNew, questPos, 0);
    }

    @Override
    public void onSectionMarks(LearningQuestionsNew learningQuestionsNew, int quesPos) {
        setOnQuestSelected(0, quesPos, learningQuestionsNew);
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
    public void onSection2Marks(LearningQuestionsNew learningQuestionsNew, int quesPos) {
        setOnQuestSelected(2, quesPos, learningQuestionsNew);
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
    public void onSection3Marks(LearningQuestionsNew learningQuestionsNew, int quesPos) {
        setOnQuestSelected(3, quesPos, learningQuestionsNew);
    }

    @Override
    public void onSection0QuestSelected(List<LearningQuestionsNew> learningQuestionsNewList) {

    }

    @Override
    public void onSection0RemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        removeTestQuestApi(learningQuestionsNew, questPos, 1);
    }

    @Override
    public void onSection0MarksClick(LearningQuestionsNew learningQuestionsNew, int questPos) {
        setOnQuestSelected(1, questPos, learningQuestionsNew);
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

    private void setOnQuestSelected(int section, int pos, LearningQuestionsNew item) {
        try {
            Log.i(TAG, "onSection0MarksClick Quest Pos : " + pos);
            secPos = section;
            qPosition = pos;
            Log.i(TAG, "onSection0MarksClick Marks : " + item.getMarks());
            float marks = Float.parseFloat(item.getMarks());
            DialogFragment addSectionDialog = new AddNewSectionDialog(this, Constant.mrks, marks);
            addSectionDialog.show(getParentFragmentManager(), "dialog_marks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
