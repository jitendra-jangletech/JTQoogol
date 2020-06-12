package com.jangletech.qoogol.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.PracticeTestQuestPaletAdapter;
import com.jangletech.qoogol.adapter.PractiseViewPagerAdapter;
import com.jangletech.qoogol.databinding.ActivityPracticeTestBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.SubmitTestDialog;
import com.jangletech.qoogol.enums.QuestionFilterType;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.EndDrawerToggle;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

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
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private EndDrawerToggle endDrawerToggle;
    private Toolbar toolbar;
    private ViewPager practiceViewPager;
    private SubmitTestDialog submitTestDialog;
    private PracticeTestQuestPaletAdapter questionListAdapter, questionGridAdapter;
    private PractiseViewPagerAdapter practiseViewPagerAdapter;
    private int currentPos = 0;
    private int tmId = 0;
    private int questPos = 0;
    private boolean isQuestionSelected = false;
    private StartResumeTestResponse startResumeTestResponse;
    private String flag = "";
    private boolean isDialogItemClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_practice_test);
        mViewModel = new ViewModelProvider(this).get(StartTestViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        practiceViewPager = findViewById(R.id.practice_viewpager);
        questionsNewList = new ArrayList<>();
        setupNavigationDrawer();
        setMargins(mBinding.marginLayout);
        setTitle("Practice Test");

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
                Toast.makeText(this, "This is last page", Toast.LENGTH_SHORT).show();
            }
        });

        mBinding.appBarTest.btnPrevious.setOnClickListener(v -> {
            currentPos = practiceViewPager.getCurrentItem();
            if (currentPos > 0) {
                practiceViewPager.setCurrentItem(currentPos - 1, true);
            } else {
                Toast.makeText(this, "This is first Page", Toast.LENGTH_SHORT).show();
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

        mBinding.btnSubmitTest.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.RIGHT);
            submitTestDialog = new SubmitTestDialog(this, this, 0);
            submitTestDialog.show();
        });


        /*practiceViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });*/

        mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if(isDialogItemClicked){
                    isDialogItemClicked = false;
                    mBinding.listView.performClick();
                    switch (flag){
                        case "ATTEMPTED":
                            mBinding.chipAttempted.performClick();
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
                practiceViewPager.setCurrentItem(questPos, true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

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
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode() != null
                        && response.body().getResponseCode().equals("200")) {
                    mViewModel.setStartResumeTestResponse(response.body());
                    mViewModel.setTestQuestAnsList(response.body().getTestQuestionNewList());
                } else {
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
        practiseViewPagerAdapter = new PractiseViewPagerAdapter(PracticeTestActivity.this,
                startResumeTestResponse.getTestQuestionNewList(), this, startResumeTestResponse.getTm_id(), flag);
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

    //    private void prepareQuestPaletGridList() {
    //        //mBinding.questionsPaletListRecyclerView.setVisibility(View.GONE);
    //        //mBinding.questionsPaletGridRecyclerView.setVisibility(View.VISIBLE);
    //        //mBinding.questionsPaletGridRecyclerView.setNestedScrollingEnabled(false);
    //        mBinding.questionsPaletGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
    //        adapter = new PracticeTestQuestPaletAdapter(this, questionsNewList, this, QuestionSortType.GRID.toString());
    //        mBinding.questionsPaletGridRecyclerView.setAdapter(adapter);
    //        adapter.notifyDataSetChanged();
    //    }

    //    private void setFilterRadio(String sortType) {
    ////        if (sortType.equalsIgnoreCase(QuestionFilterType.ATTEMPTED.toString())) {
    ////            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.ATTEMPTED.toString());
    ////            adapter.setPaletFilterResults(sortQuestListByFilter(questionsNewList, QuestionFilterType.ATTEMPTED.toString()));
    ////            mBinding.chipAttempted.setChecked(true);
    ////        } else if (sortType.equalsIgnoreCase(QuestionFilterType.UNATTEMPTED.toString())) {
    ////            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.UNATTEMPTED.toString());
    ////            adapter.setPaletFilterResults(sortQuestListByFilter(questionsNewList, QuestionFilterType.UNATTEMPTED.toString()));
    ////            mBinding.chipUnattempted.setChecked(true);
    ////        } else if (sortType.equalsIgnoreCase(QuestionFilterType.MARKED.toString())) {
    ////            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.MARKED.toString());
    ////            adapter.setPaletFilterResults(sortQuestListByFilter(questionsNewList, QuestionFilterType.MARKED.toString()));
    ////            mBinding.chipMarked.setChecked(true);
    ////        } else if (sortType.equalsIgnoreCase(QuestionFilterType.UNSEEN.toString())) {
    ////            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.UNSEEN.toString());
    ////            adapter.setPaletFilterResults(sortQuestListByFilter(questionsNewList, QuestionFilterType.UNSEEN.toString()));
    ////            mBinding.chipUnseen.setChecked(true);
    ////        } else {
    ////            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.ALL.toString());
    ////            adapter.setPaletFilterResults(sortQuestListByFilter(questionsNewList, QuestionFilterType.ALL.toString()));
    ////            mBinding.chipAll.setChecked(true);
    ////        }
    ////    }

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
            if (questFilterType.equals(QuestionFilterType.UNSEEN.toString()) && !question.isTtqa_attempted()
                    && !question.isTtqa_visited() && !question.isTtqa_marked()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.MARKED.toString()) && question.isTtqa_marked()) {
                questFilterList.add(question);
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
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
            finish();
            //startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onQuestionSelected(TestQuestionNew selectedQuest, int position) {
        mBinding.drawerLayout.closeDrawers();
        questPos = selectedQuest.getTq_quest_seq_num() - 1;
    }

    @Override
    public void onYesClick() {
        submitTestDialog.dismiss();
    }

    @Override
    public void onNoClick() {
        submitTestDialog.dismiss();
    }

    @Override
    public void onAttemptedClick() {
        mBinding.drawerLayout.openDrawer(Gravity.RIGHT);
        flag = "ATTEMPTED";
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
    public void onFavouriteClick(boolean isChecked) {

    }

    private void processQuestionAPI(HashMap<String, Integer> params) {
        Log.d(TAG, "processQuestionAPI Params : " + params);
        ProgressDialog.getInstance().show(this);
        Call<ProcessQuestion> call;

        if (params.get("CALL_FROM") == 1)
            call = apiService.likeApi(params.get(Constant.u_user_id), String.valueOf(params.get(Constant.ttqa_id))
                    , "I", params.get("Flag"));
        else
            call = apiService.favApi(params.get(Constant.u_user_id), String.valueOf(params.get(Constant.ttqa_id))
                    , "I", params.get("Flag"));

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    questionsNewList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        //updatePage(params.get("Flag"));
                    } else {
                        Toast.makeText(getApplicationContext(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialog.getInstance().dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void updatePage(int flag, int likeCount) {
        int currentPos = practiceViewPager.getCurrentItem();
        View view = practiceViewPager.findViewWithTag(currentPos);
        TextView tvLikeCount = view.findViewById(R.id.like_value);
        tvLikeCount.setText(String.valueOf(likeCount));
        /*int prevLikeCount = Integer.parseInt(tvLikeCount.getText().toString());
        if (flag == 1) {
            tvLikeCount.setText("" + (prevLikeCount + 1));
        } else {
            if (prevLikeCount == 0) {
                tvLikeCount.setText("0");
            } else {
                tvLikeCount.setText("" + (prevLikeCount - 1));
            }
        }
        practiseViewPagerAdapter.notifyDataSetChanged();*/
    }
}
