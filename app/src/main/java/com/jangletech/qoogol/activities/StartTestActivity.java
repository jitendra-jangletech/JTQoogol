package com.jangletech.qoogol.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.QuestionPaletAdapter;
import com.jangletech.qoogol.adapter.StartTestAdapter;
import com.jangletech.qoogol.databinding.ActivityStartTestBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.SubmitTestDialog;
import com.jangletech.qoogol.enums.QuestionFilterType;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.FillBlanksFragment;
import com.jangletech.qoogol.ui.LongAnsFragment;
import com.jangletech.qoogol.ui.McqFragment;
import com.jangletech.qoogol.ui.OneLineAnsFragment;
import com.jangletech.qoogol.ui.ScqFragment;
import com.jangletech.qoogol.ui.ShortAnsFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.EndDrawerToggle;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartTestActivity extends BaseActivity implements QuestionPaletAdapter.QuestClickListener,
        SubmitTestDialog.SubmitDialogClickListener, QueViewClick {

    private static final String TAG = "CourseActivity";
    private StartTestViewModel mViewModel;
    private boolean isDrawerItemClicked = false;
    ActivityStartTestBinding mBinding;
    public static ViewPager2 viewPager;
    TabLayout resulttabs;
    public int pos;
    Button btnSubmitTest;
    QuestionPaletAdapter questionPaletAdapter;
    public static List<TestQuestionNew> testQuestionList = new ArrayList<>();
    CountDownTimer timer;
    Long milliLeft, min, sec, hrs;
    SubmitTestDialog submitTestDialog;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private TextView tvTitle, tvTimer, tvPause;
    private ImageView imgNavIcon;
    private Toolbar toolbar;
    private EndDrawerToggle endDrawerToggle;
    private int tmId;
    StartResumeTestResponse startResumeTestResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_start_test);
        viewPager = (ViewPager2) findViewById(R.id.startTestViewPager);
        resulttabs = findViewById(R.id.result_tabs);
        btnSubmitTest = findViewById(R.id.btnSubmitTest);
        toolbar = findViewById(R.id.toolbar);
        tvPause = findViewById(R.id.tvPause);
        tvTimer = findViewById(R.id.tvTimer);
        tvTitle = findViewById(R.id.tvTitle);
        imgNavIcon = findViewById(R.id.imgNavIcon);
        setupNavigationDrawer();
        setMargins(mBinding.marginLayout);
        tmId = getIntent().getIntExtra(Constant.TM_ID, 0);
        tvTitle.setText("Demo Test");
        mViewModel = new ViewModelProvider(this).get(StartTestViewModel.class);
        fetchTestQA();
        mViewModel.getTestQuestAnsList().observe(this, new Observer<List<TestQuestionNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestQuestionNew> tests) {
                Log.d(TAG, "onChanged Size : " + tests.size());
                testQuestionList = tests;
                viewPager.setAdapter(createCardAdapter(testQuestionList));
                startTimer(Integer.parseInt(startResumeTestResponse.getTm_duration()) * 60 * 1000);
            }
        });

        imgNavIcon.setOnClickListener(v -> {
            if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                mBinding.drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                mBinding.drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        mBinding.chipGrpQuestionFilter.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                setSelectedChip();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                //printList();
                testQuestionList.get(position).setTtqa_visited(true);
            }
        });

        mBinding.chipAll.setOnClickListener(v -> {
            setFilterRadio(QuestionFilterType.ALL.toString());
        });
        mBinding.chipAttempted.setOnClickListener(v -> {
            setFilterRadio(QuestionFilterType.ATTEMPTED.toString());
        });
        mBinding.chipMarked.setOnClickListener(v -> {
            setFilterRadio(QuestionFilterType.MARKED.toString());
        });

        mBinding.chipUnattempted.setOnClickListener(v -> {
            setFilterRadio(QuestionFilterType.UNATTEMPTED.toString());
        });
        mBinding.chipUnseen.setOnClickListener(v -> {
            setFilterRadio(QuestionFilterType.UNSEEN.toString());
        });

        btnSubmitTest.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.RIGHT);
            submitTestDialog = new SubmitTestDialog(this, this, milliLeft);
            submitTestDialog.show();
        });

        mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                if (new PreferenceManager(getApplicationContext()).getString(Constant.FILTER_APPLIED).equals(QuestionSortType.LIST.toString())) {
                    prepareQuestPaletList();
                } else {
                    prepareQuestPaletGridList();
                }
                setFilterRadio(new PreferenceManager(getApplicationContext()).getString(Constant.SORT_APPLIED));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (isDrawerItemClicked) {
                    isDrawerItemClicked = false;
                    testQuestionList.get(pos).setTtqa_visited(true);
                    viewPager.setCurrentItem(pos,true);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        tvPause.setOnClickListener(v -> {
            pauseTimer();
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setMessage("Are you sure you want to pause the Test?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    resumeTimer();
                    dialog.dismiss();
                }
            }).show();
        });


        mBinding.gridView.setOnClickListener(v -> {
            new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED, QuestionSortType.GRID.toString());
            prepareQuestPaletGridList();
        });

        mBinding.listView.setOnClickListener(v -> {
            new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED, QuestionSortType.LIST.toString());
            prepareQuestPaletList();
        });
    }



    /*private void resetRadioButtons(RadioButton radioButton) {
        if (radioButton.getText().toString().equalsIgnoreCase("Attempted")) {
            mBinding.radioAttempted.setChecked(true);
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioMarked.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
        } else if (radioButton.getText().toString().equalsIgnoreCase("UnAttempted")) {
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioMarked.setChecked(false);
            mBinding.radioUnattempted.setChecked(true);
        } else if (radioButton.getText().toString().equalsIgnoreCase("Unseen")) {
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnseen.setChecked(true);
            mBinding.radioMarked.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
        } else if (radioButton.getText().toString().equalsIgnoreCase("Marked For Review")) {
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioMarked.setChecked(true);
            mBinding.radioUnattempted.setChecked(false);
        } else {
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioAll.setChecked(true);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioMarked.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
        }
    }*/

   /* private void setStartTestAdapter(List<TestQuestionNew> testQuestionList) {
        startTestAdapter = new StartTestAdapter(testQuestionList, this, this);
        viewPager.setAdapter(startTestAdapter);
    }*/

    public void printList() {
        for (int i = 0; i < testQuestionList.size(); i++) {
            Log.d(TAG, testQuestionList.get(i).getTq_quest_disp_num() + " = "
                    + testQuestionList.get(i).getTtqa_sub_ans() + " = " + testQuestionList.get(i).isTtqa_marked() + " = "
                    + testQuestionList.get(i).isTtqa_attempted());
        }
    }

    public void startTimer(long timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 1000) {
            @Override
            public void onTick(long milliTillFinish) {
                milliLeft = milliTillFinish;
                hrs = (milliTillFinish / (1000 * 60 * 60));
                min = ((milliTillFinish / (1000 * 60)) - hrs * 60);
                sec = ((milliTillFinish / 1000) - min * 60);
                String time = String.format("%02d:%02d:%02d", hrs, min, sec);
                tvTimer.setText(time);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }


    private void pauseTimer() {
        timer.cancel();
    }

    private void resumeTimer() {
        startTimer(milliLeft);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        endDrawerToggle.syncState();
    }

    private void stopTimer() {
        if (StartTestAdapter.countDownTimer != null) {
            StartTestAdapter.countDownTimer.cancel();
        }
    }


    @Override
    public void onQuestionSelected(TestQuestionNew questionNew) {
        showToast("Position : " + questionNew.getTq_quest_seq_num());
        isDrawerItemClicked = true;
        mBinding.drawerLayout.closeDrawer(Gravity.RIGHT);
        pos = Integer.parseInt(questionNew.getTq_quest_seq_num()) - 1;
    }

    private void prepareQuestPaletGridList() {
        mBinding.questionsPaletListRecyclerView.setVisibility(View.GONE);
        mBinding.questionsPaletGridRecyclerView.setVisibility(View.VISIBLE);
        mBinding.questionsPaletGridRecyclerView.setNestedScrollingEnabled(false);
        mBinding.questionsPaletGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        questionPaletAdapter = new QuestionPaletAdapter(testQuestionList, this, QuestionSortType.GRID.toString(), this);
        mBinding.questionsPaletGridRecyclerView.setAdapter(questionPaletAdapter);
        questionPaletAdapter.notifyDataSetChanged();
    }

    private void prepareQuestPaletList() {
        mBinding.questionsPaletGridRecyclerView.setVisibility(View.GONE);
        mBinding.questionsPaletListRecyclerView.setVisibility(View.VISIBLE);
        mBinding.questionsPaletListRecyclerView.setNestedScrollingEnabled(false);
        questionPaletAdapter = new QuestionPaletAdapter(testQuestionList, this, QuestionSortType.LIST.toString(), this);
        mBinding.questionsPaletListRecyclerView.setHasFixedSize(true);
        mBinding.questionsPaletListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.questionsPaletListRecyclerView.setAdapter(questionPaletAdapter);
        questionPaletAdapter.notifyDataSetChanged();
    }

    public static int getCurrentItemPos() {
        return viewPager.getCurrentItem();
    }

    @Override
    public void onYesClick() {
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        submitTestDialog.dismiss();
    }

    @Override
    public void onNoClick() {
        submitTestDialog.dismiss();
    }

    @Override
    public void getQueViewClick(String strQuestTag, int position) {

    }

    @Override
    public void onTabClickClick(int queNo, String strQuestTag, int position) {

    }

    @Override
    public void onTabPositionChange(int position) {

    }

    private void setFilterRadio(String sortType) {
        if (sortType.equalsIgnoreCase(QuestionFilterType.ATTEMPTED.toString())) {
            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.ATTEMPTED.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList, QuestionFilterType.ATTEMPTED.toString()));
            mBinding.chipAttempted.setChecked(true);
        } else if (sortType.equalsIgnoreCase(QuestionFilterType.UNATTEMPTED.toString())) {
            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.UNATTEMPTED.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList, QuestionFilterType.UNATTEMPTED.toString()));
            mBinding.chipUnattempted.setChecked(true);
        } else if (sortType.equalsIgnoreCase(QuestionFilterType.MARKED.toString())) {
            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.MARKED.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList, QuestionFilterType.MARKED.toString()));
            mBinding.chipMarked.setChecked(true);
        } else if (sortType.equalsIgnoreCase(QuestionFilterType.UNSEEN.toString())) {
            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.UNSEEN.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList, QuestionFilterType.UNSEEN.toString()));
            mBinding.chipUnseen.setChecked(true);
        } else {
            new PreferenceManager(getApplicationContext()).saveString(Constant.SORT_APPLIED, QuestionFilterType.ALL.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList, QuestionFilterType.ALL.toString()));
            mBinding.chipAll.setChecked(true);
        }
    }


    private List<TestQuestionNew> sortQuestListByFilter(List<TestQuestionNew> questions, String questFilterType) {
        List<TestQuestionNew> questFilterList = new ArrayList<>();
        for (TestQuestionNew question : questions) {

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
                questFilterList = questions;
            }
        }
        if (questFilterList.size() > 0) {
            mBinding.tvNoQuestions.setVisibility(View.GONE);
        } else {
            mBinding.tvNoQuestions.setVisibility(View.VISIBLE);
        }

        return questFilterList;
    }

    private void fetchTestQA() {
        ProgressDialog.getInstance().show(this);
        Call<StartResumeTestResponse> call = apiService.fetchTestQuestionAnswers(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID), tmId);
        call.enqueue(new Callback<StartResumeTestResponse>() {
            @Override
            public void onResponse(Call<StartResumeTestResponse> call, Response<StartResumeTestResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    startResumeTestResponse = response.body();
                    mViewModel.setTestQuestAnsList(response.body().getTestQuestionNewList());
                } else {
                    Log.e(TAG, "onResponse Error : " + response.body().getResponseCode());
                    showErrorDialog(StartTestActivity.this, response.body().getResponseCode(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<StartResumeTestResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();

            }
        });
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
        onBackPressed();
        return true;
    }

    /*@Override
    public void onPreviousClick(int pos) {
        int currentPos = viewPager.getCurrentItem();
        testQuestionList.get(getCurrentItemPos()).setTtqa_visited(true);
        viewPager.setCurrentItem(currentPos - 1, true);
    }

    @Override
    public void onNextClick(int pos) {
        int currentPos = viewPager.getCurrentItem();
        Toast.makeText(this, "Current Pos is : " + currentPos, Toast.LENGTH_SHORT).show();
        testQuestionList.get(getCurrentItemPos()).setTtqa_visited(true);
        viewPager.setCurrentItem(currentPos + 1, true);
    }

    @Override
    public void onReportClick(int pos) {
        questReportDialog = new QuestReportDialog(this, this);
        questReportDialog.show();
    }*/

   /* @Override
    public void onReportQuestSubmitClick() {
        questReportDialog.dismiss();
        Toast.makeText(this, "Question Reported.", Toast.LENGTH_SHORT).show();
    }*/

    private ViewPagerAdapter createCardAdapter(List<TestQuestionNew> testQuestionNewList) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, testQuestionNewList);
        return adapter;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Confirm")
                .setMessage("Are you sure you want to pause this test?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        private List<TestQuestionNew> testQuestionNewList;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<TestQuestionNew> testQuestionNewList) {
            super(fragmentActivity);
            this.testQuestionNewList = testQuestionNewList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            TestQuestionNew testQuestionNew = testQuestionNewList.get(position);
            if (testQuestionNew.getQ_type().equals(Constant.SCQ)) {
                return ScqFragment.newInstance(position);
            } else if (testQuestionNew.getQ_type().equals(Constant.MCQ)) {
                return McqFragment.newInstance(position);
            } else if (testQuestionNew.getQ_type().equals(Constant.SHORT_ANSWER)) {
                return ShortAnsFragment.newInstance(position);
            } else if (testQuestionNew.getQ_type().equals(Constant.LONG_ANSWER)) {
                return LongAnsFragment.newInstance(position);
            } else if (testQuestionNew.getQ_type().equals(Constant.ONE_LINE_ANSWER)) {
                return OneLineAnsFragment.newInstance(position);
            } else if (testQuestionNew.getQ_type().equals(Constant.Fill_THE_BLANKS_TEST)) {
                return FillBlanksFragment.newInstance(position);
            } else {
                return null;
            }
        }

        @Override
        public int getItemCount() {
            return testQuestionNewList.size();
        }
    }

    private void setSelectedChip() {
        for (int i = 0; i < mBinding.chipGrpQuestionFilter.getChildCount(); i++) {
            Chip chip = (Chip) mBinding.chipGrpQuestionFilter.getChildAt(i);
            if (chip.isChecked()) {
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setTextColor(Color.BLACK);
            }
        }
    }

    /*

    public void getQuestNo(QueViewClick queViewClick) {
        int position = resulttabs.getSelectedTabPosition();
        String strQuestTag = resulttabs.getTabAt(position).getTag().toString();
        queViewClick.getQueViewClick(strQuestTag, position);
    }

   public void setEmptyView() {
        int position = resulttabs.getSelectedTabPosition();
        TabLayout.Tab tab = resulttabs.getTabAt(position);
        tab.setCustomView(null);
    }

   public void setVisitedView() {
        int position = resulttabs.getSelectedTabPosition();
        Log.d("", "setVisitedView Tag : " + resulttabs.getTabAt(position).getTag());
        if (resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Default")
                || resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")) {
            setEmptyView();
            resulttabs.getTabAt(position).setCustomView(R.layout.layout_tab_visited);
            resulttabs.getTabAt(position).setTag("Visited");
            View linearLayout = resulttabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (resulttabs.getTabAt(position).getPosition() + 1));
        }
    }

      public void setDefaultQuestView() {
        for (int i = 0; i < resulttabs.getTabCount(); i++) {
            resulttabs.getTabAt(i).setCustomView(R.layout.layout_tab_default);
            resulttabs.getTabAt(i).setTag("Default");
            View linearLayout = resulttabs.getTabAt(i).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (resulttabs.getTabAt(i).getPosition() + 1));
        }
    }

    public void setVisitedInit() {
        setEmptyView();
        resulttabs.getTabAt(0).setCustomView(R.layout.layout_tab_visited);
        resulttabs.getTabAt(0).setTag("Visited");
        View linearLayout = resulttabs.getTabAt(0).getCustomView();
        TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
        tv.setText("1");
    }

    public void setCorrectView() {
        int position = resulttabs.getSelectedTabPosition();
        if (resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")) {
            setEmptyView();
            resulttabs.getTabAt(position).setCustomView(R.layout.layout_tab_correct);
            resulttabs.getTabAt(position).setTag("Correct");
            View linearLayout = resulttabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (resulttabs.getTabAt(position).getPosition() + 1));
        }
    }

   public void setInCorrectView() {
        int position = resulttabs.getSelectedTabPosition();
        if (resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")) {
            setEmptyView();
            resulttabs.getTabAt(position).setCustomView(R.layout.layout_tab_incorrect);
            resulttabs.getTabAt(position).setTag("Incorrect");
            View linearLayout = resulttabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (resulttabs.getTabAt(position).getPosition() + 1));
        }
    }*/

    /*private void setupViewPager(ViewPager viewPager) {
        questionList = setQuestionList();
        TabLayout tabLayout = findViewById(R.id.result_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(this, setQuestionList()));
    }*/

    /*private void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        List<TestQuestion> questTypeList = setQuestionList();
        for (int i = 0; i < questTypeList.size(); i++) {
            adapter.addFragment(new TestFragment(questTypeList, i, this), ""+i);
        }
        viewPager.setAdapter(adapter);
    }
*/

}