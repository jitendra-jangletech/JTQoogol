package com.jangletech.qoogol.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.PracticeTestQuestPaletAdapter;
import com.jangletech.qoogol.adapter.PractiseViewPagerAdapter;
import com.jangletech.qoogol.databinding.ActivityPracticeTestBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.ShareQuestionDialog;
import com.jangletech.qoogol.dialog.SubmitTestDialog;
import com.jangletech.qoogol.enums.QuestionFilterType;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.SubmitTest;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.EndDrawerToggle;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeTestActivity extends BaseActivity implements
        PracticeTestQuestPaletAdapter.QuestClickListener,
        SubmitTestDialog.SubmitDialogClickListener,
        PractiseViewPagerAdapter.ViewPagerClickListener,
        ShareQuestionDialog.ShareDialogListener {

    private static final String TAG = "PracticeTestActivity";
    public static List<TestQuestionNew> questionsNewList = new ArrayList<>();
    private StartTestViewModel mViewModel;
    private ActivityPracticeTestBinding mBinding;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private EndDrawerToggle endDrawerToggle;
    private Toolbar toolbar;
    private Gson gson;
    private ViewPager practiceViewPager;
    private SubmitTestDialog submitTestDialog;
    private PracticeTestQuestPaletAdapter questionListAdapter, questionGridAdapter;
    private PractiseViewPagerAdapter practiseViewPagerAdapter;
    private int currentPos = 0;
    private int tmId = 0;
    private int pageSelectedPos = 0;
    private int questPos = 0;
    private boolean isQuestionSelected = false;
    private StartResumeTestResponse startTestResponse;
    private String flag = "";
    private boolean isDialogItemClicked = false;
    private String testName = "";
    static CountDownTimer countDownTimer;
    private CountDownTimer testCountDownTimer;
    private TextView tvTestTitle, tvTestTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_practice_test);
        mViewModel = new ViewModelProvider(this).get(StartTestViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        tvTestTitle = findViewById(R.id.tvPracticeTitle);
        tvTestTimer = findViewById(R.id.tvPracticeTimer);
        practiceViewPager = findViewById(R.id.practice_viewpager);
        gson = new Gson();

        try {
            setupNavigationDrawer();
            setMargins(mBinding.marginLayout);
            ImageView img = findViewById(R.id.drawerIcon);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBinding.drawerLayout.openDrawer(Gravity.RIGHT);
                }
            });

            tvTestTimer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PracticeTestActivity.this, R.style.AlertDialogStyle);
                    builder.setTitle("Pause Test")
                            .setMessage("Are you sure, you want to pause this test?")
                            .setPositiveButton("Pause", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    testCountDownTimer.cancel();
                                    submitTestQuestions("P", "0");
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            });

            if (getIntent() != null && getIntent().getStringExtra("FLAG") != null
                    && getIntent().getStringExtra("FLAG").equalsIgnoreCase("ATTEMPTED")) {
                flag = "ATTEMPTED";
            }

            fetchTestQA();
            mViewModel.getTestQuestAnsList().observe(this, new Observer<List<TestQuestionNew>>() {
                @Override
                public void onChanged(@Nullable final List<TestQuestionNew> practiceQAList) {
                    if (practiceQAList != null) {
                        Log.d(TAG, "onChanged Test Duration : " + startTestResponse.getTm_duration());
                        questionsNewList = practiceQAList;
                        practiceViewPager.setOffscreenPageLimit(practiceQAList.size());
                        setQuestPaletAdapter();
                    }
                }
            });

            practiceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.d(TAG, "onPageScrolled: " + position);
                }

                @Override
                public void onPageSelected(int position) {
                    Log.d(TAG, "onPageSelected: " + position);
                    pageSelectedPos = position;
                    setTimerToSelectedPage(position);
                    questionsNewList.get(position).setTtqa_visited(true);
                    questionGridAdapter.notifyItemChanged(position);
                    questionListAdapter.notifyItemChanged(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            mViewModel.getStartResumeTestResponse().observe(this, new Observer<StartResumeTestResponse>() {
                @Override
                public void onChanged(StartResumeTestResponse startResumeTestResponse) {
                    if (startResumeTestResponse != null) {
                        Log.d(TAG, "onChanged getStartResumeTestResponse: ");
                        startTestResponse = startResumeTestResponse;
                        tvTestTitle.setText(startResumeTestResponse.getTm_name());
                        mBinding.tvTestTitle.setText(startTestResponse.getTm_name());
                        questionsNewList = startResumeTestResponse.getTestQuestionNewList();
                        try {
                            setupViewPager(startResumeTestResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            mBinding.gridView.setOnClickListener(v -> {
                mBinding.questionsPaletListRecyclerView.setVisibility(View.GONE);
                mBinding.questionsPaletGridRecyclerView.setVisibility(View.VISIBLE);
                //questionGridAdapter.notifyDataSetChanged();
            });

            mBinding.listView.setOnClickListener(v -> {
                mBinding.questionsPaletListRecyclerView.setVisibility(View.VISIBLE);
                mBinding.questionsPaletGridRecyclerView.setVisibility(View.GONE);
                //questionListAdapter.notifyDataSetChanged();
            });

            mBinding.appBarTest.btnNext.setOnClickListener(v -> {
                currentPos = practiceViewPager.getCurrentItem();
                if (currentPos < questionsNewList.size() - 1) {
                    practiceViewPager.setCurrentItem(currentPos + 1, true);
                } else {
                    Toast.makeText(this, "This is last Question", Toast.LENGTH_SHORT).show();
                }
            });

            mBinding.appBarTest.btnPrevious.setOnClickListener(v -> {
                currentPos = practiceViewPager.getCurrentItem();
                if (currentPos > 0) {
                    practiceViewPager.setCurrentItem(currentPos - 1, true);
                } else {
                    Toast.makeText(this, "This is first Question", Toast.LENGTH_SHORT).show();
                }
            });

            mBinding.chipAll.setOnClickListener(v -> {
                sortQuestListByFilter(QuestionFilterType.ALL.toString());
            });
            mBinding.chipAttempted.setOnClickListener(v -> {
                sortQuestListByFilter(QuestionFilterType.ATTEMPTED.toString());
            });
            mBinding.chipMarked.setOnClickListener(v -> {
                sortQuestListByFilter(QuestionFilterType.MARKED.toString());
            });

            mBinding.chipUnattempted.setOnClickListener(v -> {
                sortQuestListByFilter(QuestionFilterType.UNATTEMPTED.toString());
            });
            mBinding.chipUnseen.setOnClickListener(v -> {
                sortQuestListByFilter(QuestionFilterType.UNSEEN.toString());
            });
            mBinding.chipWrong.setOnClickListener(v -> {
                sortQuestListByFilter(QuestionFilterType.WRONG.toString());
            });

            mBinding.btnSubmitTest.setOnClickListener(v -> {
                mBinding.drawerLayout.closeDrawer(Gravity.RIGHT);
                submitTestDialog = new SubmitTestDialog(this, this, this, startTestResponse);
                submitTestDialog.show();
            });


            mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(@NonNull View drawerView) {
                    if (isDialogItemClicked) {
                        isDialogItemClicked = false;
                        mBinding.listView.performClick();
                        mBinding.scrollView.fullScroll(ScrollView.FOCUS_UP);
                        switch (flag) {
                            case "WRONG":
                                mBinding.chipWrong.performClick();
                                break;
                            case "UNATTEMPTED":
                                mBinding.chipUnattempted.performClick();
                                break;
                            case "MARKED":
                                mBinding.chipMarked.performClick();
                                break;
                        }
                    }
                }

                @Override
                public void onDrawerClosed(@NonNull View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchTestQA() {
        try {
            ProgressDialog.getInstance().show(this);
            tmId = getIntent().getIntExtra(Constant.TM_ID, 0);
            Call<StartResumeTestResponse> call;
            if (getIntent() != null && getIntent().getStringExtra("FLAG") != null &&
                    getIntent().getStringExtra("FLAG").equalsIgnoreCase("ATTEMPTED"))
                call = apiService.fetchAttemptedTests(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID), tmId);
            else
                call = apiService.fetchTestQuestionAnswers(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID), tmId);

            call.enqueue(new Callback<StartResumeTestResponse>() {
                @Override
                public void onResponse(Call<StartResumeTestResponse> call, Response<StartResumeTestResponse> response) {
                    Log.d(TAG, "onResponse Code : " + response.body().getResponseCode());
                    if (response.body() != null && response.body().getResponseCode() != null
                            && response.body().getResponseCode().equals("200")) {
                        mViewModel.setStartResumeTestResponse(response.body());
                        Log.d(TAG, "onResponse Question List Size : " + response.body().getTestQuestionNewList().size());
                        mViewModel.setTestQuestAnsList(response.body().getTestQuestionNewList());
                    } else {
                        showErrorDialog(PracticeTestActivity.this, response.body().getResponseCode(), "Something went wrong");
                        Log.e(TAG, "onResponse Error : " + response.body().getResponseCode());
                    }
                }

                @Override
                public void onFailure(Call<StartResumeTestResponse> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                    noInternetError(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(StartResumeTestResponse startResumeTestResponse) {
        practiseViewPagerAdapter = new PractiseViewPagerAdapter(PracticeTestActivity.this, this, startResumeTestResponse, flag);
        practiceViewPager.setAdapter(practiseViewPagerAdapter);
        setTimerToSelectedPage(0);
        if (startResumeTestResponse.getTt_duration_taken() != null
                && !startResumeTestResponse.getTt_duration_taken().isEmpty()) {
            Log.i(TAG, "setupViewPager Test Taken Duration : " + startResumeTestResponse.getTt_duration_taken());
            setTestTimer(tvTestTimer, startResumeTestResponse.getTt_duration_taken());
        } else {
            Log.i(TAG, "setupViewPager Test Master Duration : " + startResumeTestResponse.getTm_duration());
            setTestTimer(tvTestTimer, startResumeTestResponse.getTm_duration());
        }
    }

    private void setQuestPaletAdapter() {
        ProgressDialog.getInstance().show(this);
        mBinding.questionsPaletGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        questionGridAdapter = new PracticeTestQuestPaletAdapter(this, questionsNewList, this, QuestionSortType.GRID.toString());
        mBinding.questionsPaletGridRecyclerView.setAdapter(questionGridAdapter);
        questionGridAdapter.notifyDataSetChanged();

        questionListAdapter = new PracticeTestQuestPaletAdapter(this, questionsNewList, this, QuestionSortType.LIST.toString());
        mBinding.questionsPaletListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.questionsPaletListRecyclerView.setAdapter(questionListAdapter);
        questionListAdapter.notifyDataSetChanged();

        ProgressDialog.getInstance().dismiss();
    }

    private void sortQuestListByFilter(String questFilterType) {
        List<TestQuestionNew> questFilterList = new ArrayList<>();
        for (TestQuestionNew question : questionsNewList) {

            if (questFilterType.equals(QuestionFilterType.ATTEMPTED.toString()) && question.isTtqa_attempted()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.UNATTEMPTED.toString())
                    && question.isTtqa_visited() && !question.isTtqa_attempted()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.UNSEEN.toString())
                    && !question.isTtqa_visited()) {
                if (question.isTtqa_marked() || question.isTtqa_attempted()) {
                } else {
                    questFilterList.add(question);
                }
            }
            if (questFilterType.equals(QuestionFilterType.MARKED.toString()) && question.isTtqa_marked()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.WRONG.toString())
                    && question.isTtqa_attempted()) {
                if (!question.getTtqa_sub_ans().equalsIgnoreCase(question.getA_sub_ans())) {
                    questFilterList.add(question);
                }
            }
            if (questFilterType.equals(QuestionFilterType.ALL.toString())) {
                questFilterList = questionsNewList;
            }
        }
        if (questFilterList.size() > 0) {
            mBinding.tvNoQuestions.setVisibility(View.GONE);
        } else {
            mBinding.tvNoQuestions.setVisibility(View.VISIBLE);
        }
        questionListAdapter.setSortedQuestList(questFilterList);
        questionGridAdapter.setSortedQuestList(questFilterList);
    }

    private void setupNavigationDrawer() {
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        endDrawerToggle = new EndDrawerToggle(this, mBinding.drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(endDrawerToggle);
        endDrawerToggle.syncState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Pause")
                    .setMessage("Are you sure, you want to pause this test?")
                    .setPositiveButton("Pause", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            testCountDownTimer.cancel();
                            submitTestQuestions("P", "0");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public void onQuestionSelected(TestQuestionNew selectedQuest, int position) {
        mBinding.drawerLayout.closeDrawers();
        questPos = selectedQuest.getTq_quest_seq_num() - 1;
        practiceViewPager.setCurrentItem(questPos, true);
    }


    @Override
    public void onYesClick(String obtainMarks) {
        Log.d(TAG, "onYesClick: " + obtainMarks);
        submitTestQuestions("C", obtainMarks);
    }

    private void submitTestQuestions(String testStatus, String obtainMarks) {
        Log.d(TAG, "submitTestQuestions Test Duration : " + tvTestTimer.getText());
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        submitTest.setTm_id(startTestResponse.getTm_id());
        submitTest.setTt_id(String.valueOf(startTestResponse.getTtId()));
        if (tvTestTimer.getText().toString().contains("-")) {
            submitTest.setTt_duration_taken("00:00:00");
        } else {
            submitTest.setTt_duration_taken(tvTestTimer.getText().toString());
        }

        int count = 0;
        for (TestQuestionNew question : questionsNewList) {
            View view = practiceViewPager.findViewWithTag(count);
            if (view != null) {
                TextView tvTimer = view.findViewById(R.id.tvtimer);
                Log.d(TAG, "TestQuestion : " + tvTimer.getText().toString());
                question.setTq_duration(tvTimer.getText().toString());
            }
            submitTestQuestionList.add(question);
            count++;
        }
        submitTest.setTestQuestionNewList(submitTestQuestionList);
        String json = gson.toJson(submitTest);
        Log.d(TAG, "submitTestQuestions JSON : " + json);

        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.tt_id, String.valueOf(startTestResponse.getTtId()));
        params.put(Constant.tt_obtain_marks, obtainMarks);
        params.put(Constant.tt_status, testStatus);
        params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(this).getInt(Constant.USER_ID)));
        params.put(Constant.DataList, json);

        Log.d(TAG, "submitSubjectiveAnsToServer Params : " + params);
        ProgressDialog.getInstance().show(this);
        Call<VerifyResponse> call = apiService.submitTestQuestion(
                params.get(Constant.DataList),
                params.get(Constant.u_user_id)
        );
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {
                    if (testStatus.equalsIgnoreCase("C")) {
                        showToast("Test Submitted Successfully.");
                    }
                    finish();
                } else {
                    AppUtils.showToast(getApplicationContext(), null, response.body().getErrorMsg());
                    discardTest();
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                apiCallFailureTestDialog(t);
            }
        });
    }

    @Override
    public void onNoClick() {
        //submitTestDialog.dismiss();
    }

    @Override
    public void onWrongClick() {
        mBinding.drawerLayout.openDrawer(Gravity.RIGHT);
        flag = "WRONG";
        isDialogItemClicked = true;
    }

    @Override
    public void onUnAttemptedClick() {
        mBinding.drawerLayout.openDrawer(Gravity.RIGHT);
        flag = "UNATTEMPTED";
        isDialogItemClicked = true;
    }

    @Override
    public void onMarkedClick() {
        mBinding.drawerLayout.openDrawer(Gravity.RIGHT);
        flag = "MARKED";
        isDialogItemClicked = true;
    }

    @Override
    public void onLikeClick(boolean isChecked, int likeCount) {
        int currentPos = practiceViewPager.getCurrentItem();
        TestQuestionNew testQuestion = questionsNewList.get(currentPos);
        HashMap<String, Integer> params = new HashMap<>();
        params.put(Constant.u_user_id, new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID));
        params.put(Constant.q_id, testQuestion.getTtqa_id());
        updatePage(isChecked ? 1 : 0, likeCount);
    }

    @Override
    public void onCommentClick() {

    }

    @Override
    public void onCommentBack(int count) {
        updatePageCount("COMMENT", count);
    }


    @Override
    public void onShareClick() {
        Log.d(TAG, "onShareClick: ");
        TestQuestionNew testQuestionNew = questionsNewList.get(practiceViewPager.getCurrentItem());
        String qId = String.valueOf(testQuestionNew.getTtqa_id());
        new ShareQuestionDialog(this, qId, AppUtils.getUserId()
                , getDeviceId(), "Q", this)
                .show();
    }

    @Override
    public void onSubmitClick() {
        questionGridAdapter.notifyDataSetChanged();
        questionListAdapter.notifyDataSetChanged();
        stopTimerPage();
    }

    private void stopTimerPage() {
        int pos = practiceViewPager.getCurrentItem();
        questionsNewList.get(pos).setAnsSubmitted(true);
        countDownTimer.cancel();
    }

    @Override
    public void onMarkQuestion(int pos) {
        notifyPaletAdapters(pos);
        markAQuestion(pos);
    }

    @Override
    public void onFullScreenAns(String strAns) {
        if (!strAns.isEmpty()) {
            submitQuestion(strAns);
            setFullScreenAns(AppUtils.decodedString(strAns));
        }
    }

    private void submitQuestion(String ans) {
        int currentPos = practiceViewPager.getCurrentItem();
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        TestQuestionNew testQuestionNew = questionsNewList.get(currentPos);
        testQuestionNew.setTtqa_sub_ans(ans);
        testQuestionNew.setTtqa_attempted(true);
        testQuestionNew.setTtqa_visited(false);
        Log.d(TAG, "markAQuestion Submit Position : " + currentPos);
        Log.d(TAG, "markAQuestion Submit Question Desc : " + testQuestionNew.getQ_quest());
        Log.d(TAG, "markAQuestion Submit Marked  : " + testQuestionNew.isTtqa_marked());
        Log.d(TAG, "markAQuestion Submit Ans  : " + testQuestionNew.getTtqa_sub_ans());
        submitTest.setTm_id(startTestResponse.getTm_id());
        submitTest.setTt_id(String.valueOf(startTestResponse.getTtId()));

        submitTestQuestionList.add(testQuestionNew);
        submitTest.setTestQuestionNewList(submitTestQuestionList);
        String json = gson.toJson(submitTest);
        Log.d(TAG, "markAQuestion JSON : " + json);
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, AppUtils.getUserId());
        params.put(Constant.DataList, json);
        Log.d(TAG, "markAQuestion UserId : " + AppUtils.getUserId());
        Log.d(TAG, "submitSubjectiveAnsToServer Params : " + params);
        ProgressDialog.getInstance().show(this);
        Call<VerifyResponse> call = apiService.submitTestQuestion(
                params.get(Constant.DataList),
                params.get(Constant.u_user_id));
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {

                } else {
                    AppUtils.showToast(getApplicationContext(), null, response.body().getErrorMsg());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(getApplicationContext(), t, "");
            }
        });
    }

    private void notifyPaletAdapters(int pos) {
        TestQuestionNew testQuestionNew = questionsNewList.get(pos);
        questionGridAdapter.notifyItemChanged(pos, testQuestionNew);
        questionListAdapter.notifyItemChanged(pos, testQuestionNew);
    }

    private void discardTest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PracticeTestActivity.this, R.style.AlertDialogStyle);
        builder.setTitle("Discard Test")
                .setMessage("We are not able to save changes, due to some technical issue. Since, closing this test.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testCountDownTimer.cancel();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void markAQuestion(int markedPos) {
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        TestQuestionNew testQuestionNew = questionsNewList.get(markedPos);
        Log.d(TAG, "markAQuestion Marked Position : " + markedPos);
        Log.d(TAG, "markAQuestion Question Desc : " + testQuestionNew.getQ_quest());
        Log.d(TAG, "markAQuestion Marked  : " + testQuestionNew.isTtqa_marked());
        Log.d(TAG, "markAQuestion Ans  : " + testQuestionNew.getTtqa_sub_ans());
        submitTest.setTm_id(startTestResponse.getTm_id());
        submitTest.setTt_id(String.valueOf(startTestResponse.getTtId()));

        submitTestQuestionList.add(testQuestionNew);
        submitTest.setTestQuestionNewList(submitTestQuestionList);
        String json = gson.toJson(submitTest);
        Log.d(TAG, "markAQuestion JSON : " + json);
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, AppUtils.getUserId());
        params.put(Constant.DataList, json);
        Log.d(TAG, "markAQuestion UserId : " + AppUtils.getUserId());
        Log.d(TAG, "submitSubjectiveAnsToServer Params : " + params);
        ProgressDialog.getInstance().show(this);
        Call<VerifyResponse> call = apiService.submitTestQuestion(
                params.get(Constant.DataList),
                params.get(Constant.u_user_id));
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {

                } else {
                    AppUtils.showToast(getApplicationContext(), null, response.body().getErrorMsg());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(getApplicationContext(), t, "");
            }
        });
    }

    @Override
    public void onFavouriteClick(boolean isChecked) {

    }

    private void setFullScreenAns(String ans) {
        View view = practiceViewPager.findViewWithTag(practiceViewPager.getCurrentItem());
        if (view != null) {
            EditText etAns = view.findViewById(R.id.multi_line);
            etAns.setText(ans);
        }
    }

    private void setTestTimer(TextView timer, String duration) {

        try {

            String timerDuration[] = duration.split(":", -1);
            int hours = Integer.parseInt(timerDuration[0]);
            int minutes = Integer.parseInt(timerDuration[1]);
            int seconds = Integer.parseInt(timerDuration[2]);

            if (testCountDownTimer != null)
                testCountDownTimer.cancel();

            testCountDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
                int timerCountSeconds = seconds;
                int timerCountMinutes = minutes;
                int timerCountHours = hours;

                public void onTick(long millisUntilFinished) {
                    if (timerCountSeconds > 0) {
                        timerCountSeconds--;
                    } else {
                        timerCountSeconds = 59;
                        if (timerCountMinutes > 1) {
                            timerCountMinutes--;
                        } else {
                            timerCountMinutes = 59;
                            timerCountHours--;
                        }
                    }

                    if (timerCountMinutes < 10) {
                        if (timerCountSeconds < 10) {
                            timer.setText(String.valueOf("0" + timerCountHours + ":0" + timerCountMinutes + ":0" + timerCountSeconds));
                        } else {
                            timer.setText(String.valueOf("0" + timerCountHours + ":0" + timerCountMinutes + ":" + timerCountSeconds));
                        }
                    } else {
                        if (timerCountSeconds < 10) {
                            timer.setText(String.valueOf("0" + timerCountHours + ":" + timerCountMinutes + ":0" + timerCountSeconds));
                        } else {
                            timer.setText(String.valueOf("0" + timerCountHours + ":" + timerCountMinutes + ":" + timerCountSeconds));
                        }
                    }
                }

                public void onFinish() {
                    timer.setText("00:00:00");
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTimerToSelectedPage(int pos) {
        if (!questionsNewList.get(pos).isAnsSubmitted()) {
            int prevSeconds = 0;
            int prevMinutes = 0;
            if (countDownTimer != null)
                countDownTimer.cancel();
            View view = practiceViewPager.findViewWithTag(pos);
            if (view != null) {
                TextView tvTimer = view.findViewById(R.id.tvtimer);
                String[] time = tvTimer.getText().toString().split(":", -1);
                prevSeconds = Integer.parseInt(time[1]);
                prevMinutes = Integer.parseInt(time[0]);

                int finalPrevSeconds = prevSeconds;
                int finalPrevMinutes = prevMinutes;
                countDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
                    int timerCountSeconds = finalPrevSeconds;
                    int timerCountMinutes = finalPrevMinutes;

                    public void onTick(long millisUntilFinished) {
                        if (timerCountSeconds < 59) {
                            timerCountSeconds++;
                        } else {
                            timerCountSeconds = 0;
                            timerCountMinutes++;
                        }
                        if (timerCountMinutes < 10) {
                            if (timerCountSeconds < 10) {
                                tvTimer.setText(String.valueOf("0" + timerCountMinutes + ":0" + timerCountSeconds));
                            } else {
                                tvTimer.setText(String.valueOf("0" + timerCountMinutes + ":" + timerCountSeconds));
                            }
                        } else {
                            if (timerCountSeconds < 10) {
                                tvTimer.setText(String.valueOf(timerCountMinutes + ":0" + timerCountSeconds));
                            } else {
                                tvTimer.setText(String.valueOf(timerCountMinutes + ":" + timerCountSeconds));
                            }
                        }
                    }

                    public void onFinish() {
                        tvTimer.setText("00:00");
                    }
                }.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        questionsNewList = null;
        questionGridAdapter = null;
        questionListAdapter = null;
        practiseViewPagerAdapter = null;
    }

    private void updatePage(int flag, int likeCount) {
        View view = practiceViewPager.findViewWithTag(pageSelectedPos);
        TextView tvLikeCount = view.findViewById(R.id.like_value);
        tvLikeCount.setText(String.valueOf(likeCount));
    }

    private void updatePageCount(String flag, int count) {
        View view = practiceViewPager.findViewWithTag(pageSelectedPos);
        if (flag.equalsIgnoreCase("COMMENT")) {
            TextView tvCommentCount = view.findViewById(R.id.comment_value);
            int prevCount = Integer.parseInt(tvCommentCount.getText().toString()) + count;
            tvCommentCount.setText(String.valueOf(prevCount));
        } else {
            TextView tvShareValue = view.findViewById(R.id.share_value);
            int prevCount = Integer.parseInt(tvShareValue.getText().toString()) + count;
            tvShareValue.setText(String.valueOf(prevCount));
        }
    }

    @Override
    public void onSharedSuccess(int count) {
        updatePageCount("SHARE", count);
    }
}
