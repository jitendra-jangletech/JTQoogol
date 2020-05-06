package com.jangletech.qoogol.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.QuestionPaletAdapter;
import com.jangletech.qoogol.adapter.StartTestAdapter;
import com.jangletech.qoogol.databinding.ActivityStartTestBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.QuestReportDialog;
import com.jangletech.qoogol.dialog.SubmitTestDialog;
import com.jangletech.qoogol.enums.QuestionFilterType;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.EndDrawerToggle;
import com.jangletech.qoogol.util.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartTestActivity extends AppCompatActivity implements QuestionPaletAdapter.QuestClickListener,
        SubmitTestDialog.SubmitDialogClickListener, QueViewClick, StartTestAdapter.StartAdapterButtonClickListener,
        QuestReportDialog.QuestReportDialogListener {

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
    Long milliLeft, min, sec,hrs;
    SubmitTestDialog submitTestDialog;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    StartTestAdapter startTestAdapter;
    QuestReportDialog questReportDialog;
    private TextView tvTitle,tvTimer,tvPause;
    private ImageView imgNavIcon;
    private Toolbar toolbar;
    private EndDrawerToggle endDrawerToggle;
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
        //startTimer(60*60*1000);
        //Toast.makeText(this, ""+getStatusBarHeight(), Toast.LENGTH_SHORT).show();
        tvTitle.setText("Demo Test");
        mViewModel = new ViewModelProvider(this).get(StartTestViewModel.class);
        fetchTestQA();
        mViewModel.getTestQuestAnsList().observe(this, new Observer<List<TestQuestionNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestQuestionNew> tests) {
                Log.d(TAG, "onChanged Size : " + tests.size());
                testQuestionList = tests;
                setStartTestAdapter(testQuestionList);
                startTimer(Integer.parseInt(startResumeTestResponse.getTm_duration())*60*1000);
            }
        });

        imgNavIcon.setOnClickListener(v->{
            if(mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)){
                mBinding.drawerLayout.closeDrawer(GravityCompat.END);
            }else{
                mBinding.drawerLayout.openDrawer(GravityCompat.END);
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
               testQuestionList.get(getCurrentItemPos()).setTtqa_visited(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        mBinding.radioAll.setOnClickListener(v -> {
            if (mBinding.questionsPaletGridRecyclerView.getVisibility() == View.VISIBLE) {
                prepareQuestPaletGridList(QuestionFilterType.ALL.toString());
            } else {
                prepareQuestPaletList(QuestionFilterType.ALL.toString());
            }
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioMarked.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
        });
        mBinding.radioAttempted.setOnClickListener(v -> {
            if (mBinding.questionsPaletGridRecyclerView.getVisibility() == View.VISIBLE) {
                prepareQuestPaletGridList(QuestionFilterType.ATTEMPTED.toString());
            } else {
                prepareQuestPaletList(QuestionFilterType.ATTEMPTED.toString());
            }
            new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED,QuestionFilterType.ATTEMPTED.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,QuestionFilterType.ATTEMPTED.toString()));
            questionPaletAdapter.notifyDataSetChanged();
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioMarked.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
        });
        mBinding.radioMarked.setOnClickListener(v -> {

            if (mBinding.questionsPaletGridRecyclerView.getVisibility() == View.VISIBLE) {
                prepareQuestPaletGridList(QuestionFilterType.MARKED.toString());
            } else {
                prepareQuestPaletList(QuestionFilterType.MARKED.toString());
            }
            new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED,QuestionFilterType.MARKED.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,QuestionFilterType.MARKED.toString()));
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
        });

        mBinding.radioUnattempted.setOnClickListener(v -> {
            if (mBinding.questionsPaletGridRecyclerView.getVisibility() == View.VISIBLE) {
                prepareQuestPaletGridList(QuestionFilterType.UNATTEMPTED.toString());
            } else {
                prepareQuestPaletList(QuestionFilterType.UNATTEMPTED.toString());
            }
            new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED,QuestionFilterType.UNATTEMPTED.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,QuestionFilterType.UNATTEMPTED.toString()));
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnseen.setChecked(false);
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioMarked.setChecked(false);
        });
        mBinding.radioUnseen.setOnClickListener(v -> {
            if (mBinding.questionsPaletGridRecyclerView.getVisibility() == View.VISIBLE) {
                prepareQuestPaletGridList(QuestionFilterType.UNSEEN.toString());
            } else {
                prepareQuestPaletList(QuestionFilterType.UNSEEN.toString());
            }
            new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED,QuestionFilterType.UNSEEN.toString());
            questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,QuestionFilterType.UNSEEN.toString()));
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioMarked.setChecked(false);
        });

        btnSubmitTest.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
            submitTestDialog = new SubmitTestDialog(this, this, milliLeft);
            submitTestDialog.show();
        });

        mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Log.d(TAG, "onDrawerOpened: "+new PreferenceManager(getApplicationContext()).getString(Constant.FILTER_APPLIED));
                prepareQuestPaletList(new PreferenceManager(getApplicationContext()).getString(Constant.FILTER_APPLIED));
                questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,new PreferenceManager(getApplicationContext()).getString(Constant.FILTER_APPLIED)));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (isDrawerItemClicked) {
                    isDrawerItemClicked = false;
                    testQuestionList.get(pos).setTtqa_visited(true);
                    viewPager.setCurrentItem(pos);
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
            prepareQuestPaletGridList(new PreferenceManager(getApplicationContext()).getString(Constant.FILTER_APPLIED));
            //Toast.makeText(this, "Grid View Selected", Toast.LENGTH_SHORT).show();
        });

        mBinding.listView.setOnClickListener(v -> {
            prepareQuestPaletList(new PreferenceManager(getApplicationContext()).getString(Constant.FILTER_APPLIED));
            //Toast.makeText(this, "List View Selected", Toast.LENGTH_SHORT).show();
        });
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private void setStartTestAdapter(List<TestQuestionNew> testQuestionList) {
        startTestAdapter = new StartTestAdapter(testQuestionList, this, this);
        viewPager.setAdapter(startTestAdapter);
    }

    public void startTimer(long timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 1000) {
            @Override
            public void onTick(long milliTillFinish) {
                milliLeft = milliTillFinish;
                hrs = (milliTillFinish/(1000*60*60));
                min = ((milliTillFinish / (1000 * 60)) - hrs * 60);
                sec = ((milliTillFinish / 1000) - min * 60);
                String time = String.format("%02d:%02d:%02d",hrs,min, sec);
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


    private ViewPagerAdapterNew createCardAdapter(List<TestQuestionNew> testQuestionNewList) {
        ViewPagerAdapterNew adapter = new ViewPagerAdapterNew(this, testQuestionNewList);
        return adapter;
    }

    @Override
    public void onQuestionSelected(int questId) {
        isDrawerItemClicked = true;
        mBinding.drawerLayout.closeDrawer(Gravity.RIGHT);
        pos = questId;
    }

    private void prepareQuestPaletGridList(String filterType) {
        mBinding.questionsPaletListRecyclerView.setVisibility(View.GONE);
        mBinding.questionsPaletGridRecyclerView.setVisibility(View.VISIBLE);
        mBinding.questionsPaletGridRecyclerView.setNestedScrollingEnabled(false);
        mBinding.questionsPaletGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        questionPaletAdapter = new QuestionPaletAdapter(testQuestionList, this, QuestionSortType.GRID.toString(), this);
        mBinding.questionsPaletGridRecyclerView.setAdapter(questionPaletAdapter);
        questionPaletAdapter.notifyDataSetChanged();
    }

    private void prepareQuestPaletList(String filterType) {
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

        return questFilterList;
    }

    private void fetchTestQA() {
        ProgressDialog.getInstance().show(this);
        Call<StartResumeTestResponse> call = apiService.fetchTestQuestionAnswers(1002, 1);
        call.enqueue(new Callback<StartResumeTestResponse>() {
            @Override
            public void onResponse(Call<StartResumeTestResponse> call, Response<StartResumeTestResponse> response) {
                ProgressDialog.getInstance().dismiss();
                Log.d(TAG, "onResponse List : " + response.body().getTestQuestionNewList());
                startResumeTestResponse = response.body();
                mViewModel.setTestQuestAnsList(response.body().getTestQuestionNewList());
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

    @Override
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
    }

    @Override
    public void onReportQuestSubmitClick() {
        questReportDialog.dismiss();
        Toast.makeText(this, "Question Reported.", Toast.LENGTH_SHORT).show();
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