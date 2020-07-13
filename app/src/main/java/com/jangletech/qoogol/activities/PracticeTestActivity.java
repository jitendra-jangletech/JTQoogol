package com.jangletech.qoogol.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.jangletech.qoogol.dialog.SubmitTestDialog;
import com.jangletech.qoogol.enums.QuestionFilterType;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.SubmitTest;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.EndDrawerToggle;
import com.jangletech.qoogol.util.PreferenceManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeTestActivity extends BaseActivity implements
        PracticeTestQuestPaletAdapter.QuestClickListener,
        SubmitTestDialog.SubmitDialogClickListener,
        PractiseViewPagerAdapter.ViewPagerClickListener {

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
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_practice_test);
        mViewModel = new ViewModelProvider(this).get(StartTestViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvPracticeTitle);
        practiceViewPager = findViewById(R.id.practice_viewpager);
        gson = new Gson();
        setupNavigationDrawer();
        setMargins(mBinding.marginLayout);

        if (getIntent() != null && getIntent().getStringExtra("FLAG") != null
                && getIntent().getStringExtra("FLAG").equalsIgnoreCase("ATTEMPTED")) {
            flag = "ATTEMPTED";
        }

        fetchTestQA();
        mViewModel.getTestQuestAnsList().observe(this, new Observer<List<TestQuestionNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestQuestionNew> practiceQAList) {
                if (practiceQAList != null) {
                    Log.d(TAG, "onChanged: ");
                    questionsNewList = practiceQAList;
                    practiceViewPager.setOffscreenPageLimit(practiceQAList.size());
                    setQuestPaletAdapter();
                }
            }
        });

        practiceViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

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
                    startTestResponse = startResumeTestResponse;
                    //tvTitle.setText(startResumeTestResponse.getTm_name());
                    setTitle(startTestResponse.getTm_name());
                    mBinding.tvTestTitle.setText(startTestResponse.getTm_name());
                    questionsNewList = startResumeTestResponse.getTestQuestionNewList();
                    setupViewPager(startResumeTestResponse);
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
               /* if (isQuestionSelected) {
                    isQuestionSelected = false;
                    practiceViewPager.setCurrentItem(questPos, true);
                }*/
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void submitTestQuestions() {
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        submitTest.setTm_id(startTestResponse.getTm_id());
        submitTest.setTt_id(String.valueOf(startTestResponse.getTtId()));
        for (TestQuestionNew question : questionsNewList) {
            if (question.isAnsweredRight())
                question.setTtqa_obtain_marks(question.getQ_marks());
            else
                question.setTtqa_obtain_marks("0");

            if (question.getQue_option_type().equalsIgnoreCase(Constant.FILL_THE_BLANKS) ||
                    question.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                    question.getType().equalsIgnoreCase(Constant.LONG_ANSWER) ||
                    question.getType().equalsIgnoreCase(Constant.ONE_LINE_ANSWER) ||
                    question.getType().equalsIgnoreCase(Constant.FILL_THE_BLANKS)) {
                String encoded = Base64.encodeToString(question.getTtqa_sub_ans().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                String encodedAns = StringUtils.stripAccents(encoded);
                Log.d(TAG, "Encoded Ans : " + encodedAns);
                question.setTtqa_sub_ans(encodedAns);
            } else {
                question.setTtqa_sub_ans(question.getTtqa_sub_ans());
            }
            submitTestQuestionList.add(question);
        }

        submitTest.setTestQuestionNewList(submitTestQuestionList);
        String json = gson.toJson(submitTest);
        Log.d(TAG, "submitTestQuestions JSON : " + json);
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(this).getInt(Constant.USER_ID)));
        params.put(Constant.DataList, json);

        Log.d(TAG, "submitTestQuestions Params : " + params);
        ProgressDialog.getInstance().show(this);
        Call<VerifyResponse> call = apiService.submitTestQuestion(
                params.get(Constant.DataList),
                params.get(Constant.u_user_id));
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    //showToast("Te Submitted");
                    finish();
                } else {
                    Log.e(TAG, "submitTestQuestions Error : " + response.body().getResponse());
                    showToast(response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchTestQA() {
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
                if (response.body() != null && response.body().getResponseCode() != null
                        && response.body().getResponseCode().equals("200")) {
                    mViewModel.setStartResumeTestResponse(response.body());
                    mViewModel.setTestQuestAnsList(response.body().getTestQuestionNewList());
                } else {
                    showErrorDialog(PracticeTestActivity.this, response.body().getResponseCode(), "Something went wrong");
                    Log.e(TAG, "onResponse Error : " + response.body().getResponseCode());
                }
            }

            @Override
            public void onFailure(Call<StartResumeTestResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    private void setupViewPager(StartResumeTestResponse startResumeTestResponse) {
        for (TestQuestionNew testQuestionNew : startResumeTestResponse.getTestQuestionNewList()) {
            if (testQuestionNew.getA_sub_ans().trim().equalsIgnoreCase(testQuestionNew.getTtqa_sub_ans().trim())
                    || testQuestionNew.getType().equals("5") || testQuestionNew.getType().equals("6")) {
                testQuestionNew.setAnsweredRight(true);
            }
        }
        practiseViewPagerAdapter = new PractiseViewPagerAdapter(PracticeTestActivity.this, this, startResumeTestResponse, flag);
        practiceViewPager.setAdapter(practiseViewPagerAdapter);
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
                            finish();
                            //submitTestQuestions();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            //startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onQuestionSelected(TestQuestionNew selectedQuest, int position) {
        mBinding.drawerLayout.closeDrawers();
        questPos = selectedQuest.getTq_quest_seq_num() - 1;
        practiceViewPager.setCurrentItem(questPos, true);
    }

    @Override
    public void onYesClick() {
        //submitTestQuestions();
        submitTestQuestions();
    }


   /* private void submitTestQuestions() {
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        submitTest.setTm_id(startTestResponse.getTm_id());
        submitTest.setTt_id(String.valueOf(startTestResponse.getTtId()));
        for (TestQuestionNew question : questionsNewList) {
            submitTestQuestionList.add(question);
        }
        submitTest.setTestQuestionNewList(submitTestQuestionList);
        String json = gson.toJson(submitTest);
        Log.d(TAG, "submitTestQuestions JSON : " + json);

        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.tt_id, String.valueOf(startTestResponse.getTtId()));
        params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(this).getInt(Constant.USER_ID)));
        params.put(Constant.DataList, json);

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
                    showToast("Answer Submitted");
                    finish();
                } else {
                    Log.e(TAG, "onResponse Error : " + response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                showToast("Something went wrong!!");
                t.printStackTrace();
                apiCallFailureDialog(t);
            }
        });

    }*/

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
    public void onShareClick() {

    }

    @Override
    public void onSubmitClick() {
        questionGridAdapter.notifyDataSetChanged();
        questionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMarkQuestion(int pos) {
        questionGridAdapter.notifyItemChanged(pos);
        questionListAdapter.notifyItemChanged(pos);
    }

    @Override
    public void onFavouriteClick(boolean isChecked) {

    }

    private void setTimerToSelectedPage(int pos) {
        if (countDownTimer != null)
            countDownTimer.cancel();

        View view = practiceViewPager.findViewWithTag(pos);
        if (view != null) {
            TextView tvTimer = view.findViewById(R.id.tvtimer);

            countDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
                int timerCountSeconds = 0;
                int timerCountMinutes = 0;

                public void onTick(long millisUntilFinished) {
                    // timer.setText(new SimpleDateFormat("mm:ss").format(new Date( millisUntilFinished)));
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
}
