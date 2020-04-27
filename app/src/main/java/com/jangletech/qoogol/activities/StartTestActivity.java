package com.jangletech.qoogol.activities;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.jangletech.qoogol.databinding.ActivityCourseBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.SubmitTestDialog;
import com.jangletech.qoogol.enums.QuestionFilterType;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.enums.QuestionType;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.CommonResponseObject;
import com.jangletech.qoogol.model.Exams;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.model.TestQuestion;
import com.jangletech.qoogol.model.TestingQuestionNew;
import com.jangletech.qoogol.model.TestingRequestDto;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartTestActivity extends AppCompatActivity implements QuestionPaletAdapter.QuestClickListener,
        SubmitTestDialog.SubmitDialogClickListener, QueViewClick {

    private static final String TAG = "CourseActivity";
    private StartTestViewModel startTestViewModel;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ActivityCourseBinding mBinding;
    public static ViewPager2 viewPager;
    TabLayout resulttabs;
    public static int pos;
    Button btnSubmitTest;
    TextView tvTimer, tvPause, tvTitle;
    QuestionPaletAdapter questionPaletAdapter;
    public static List<TestQuestion> testQuestionList = new ArrayList<>();
    CountDownTimer timer;
    Long milliLeft, min, sec;
    SubmitTestDialog submitTestDialog;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course);
        viewPager = (ViewPager2) findViewById(R.id.course_viewpager);
        resulttabs = findViewById(R.id.result_tabs);
        btnSubmitTest = findViewById(R.id.btnSubmitTest);
        //startTestViewModel = new ViewModelProvider(this).get(StartTestViewModel.class);
        //setQuestionList();
        /*startTestViewModel.getAllQuestions().observe(this, new Observer<List<TestQuestion>>() {
            @Override
            public void onChanged(@Nullable final List<TestQuestion> tests) {
                Log.d(TAG, "onChanged Size : "+tests.size());
                for (int i = 0; i < tests.size(); i++) {
                    Log.d(TAG, "onChanged: "+tests.get(i).getQuestionDesc());
                }
                //setMyTestList(tests);
            }
        });*/

        setQuestionList();
        viewPager.setAdapter(createCardAdapter());
        configureActionBar();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TestQuestion testQuestion = testQuestionList.get(position);
                testQuestion.setVisited(true);
                //Toast.makeText(CourseActivity.this, "Selected Page : " + position, Toast.LENGTH_SHORT).show();
                Log.e("Selected_Page", String.valueOf(position));
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
            //questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,QuestionFilterType.ATTEMPTED.toString()));
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
            //questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,QuestionFilterType.MARKED.toString()));
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
            //questionPaletAdapter.setPaletFilterResults(sortQuestListByFilter(testQuestionList,QuestionFilterType.UNATTEMPTED.toString()));
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
            mBinding.radioAll.setChecked(false);
            mBinding.radioUnattempted.setChecked(false);
            mBinding.radioAttempted.setChecked(false);
            mBinding.radioMarked.setChecked(false);
        });

        btnSubmitTest.setOnClickListener(v -> {
            drawerLayout.closeDrawer(Gravity.LEFT);
            submitTestDialog = new SubmitTestDialog(this, this, milliLeft);
            submitTestDialog.show();
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //PreferenceManager preferenceManager = new PreferenceManager(CourseActivity.this);
                prepareQuestPaletGridList(new PreferenceManager(getApplicationContext()).getString(Constant.FILTER_APPLIED));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                //Toast.makeText(CourseActivity.this, "Drawer is Closed", Toast.LENGTH_SHORT).show();
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

    public void startTimer(long timeLengthMilli) {
        timer = new CountDownTimer(timeLengthMilli, 1000) {

            @Override
            public void onTick(long milliTillFinish) {
                milliLeft = milliTillFinish;
                min = (milliTillFinish / (1000 * 60));
                sec = ((milliTillFinish / 1000) - min * 60);
                String time = String.format("%02d:%02d", min, sec);
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
        actionBarDrawerToggle.syncState();
    }



    private ViewPagerAdapterNew createCardAdapter() {
        ViewPagerAdapterNew adapter = new ViewPagerAdapterNew(this);
        return adapter;
    }

    @Override
    public void onQuestionSelected(int questId) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        viewPager.setCurrentItem(questId, true);
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<TestQuestion> setQuestionList() {
        testQuestionList.clear();
        TestQuestion testQuestion = new TestQuestion(0, 1, "MCQ",
                "Web Pages are saved in which of the following format?", "http://", "HTML", "DOC", "URL", "BMP");

        TestQuestion testQuestion1 = new TestQuestion(1, 2, "SCQ",
                "System software acts as a bridge between the hardware and _____ software", "Management", "Processing",
                "Utility", "Application", "Embedded");


        TestQuestion testQuestion2 = new TestQuestion(2, 3, "SCQ",
                "Who is appointed as the brand ambassador of VISA recently?",
                "Ram Sing Yadav", "Arpinder Singh", "PV Sindhu", "Sania Mirza", "Sachin Tendulkar");

        TestQuestion testQuestion3 = new TestQuestion(3, 4, "MCQ",
                "Who built Jama Masjid at Delhi?",
                "Jahangir", "Qutub-ud-din-Albak", "Akbar", "Birbal", "Aurangjeb");
        TestQuestion testQuestion4 = new TestQuestion(4, 5, QuestionType.MCQ.toString(),
                "Which of the following helps in the blood clotting?", "Vitamin A", "Vitamin D", "Vitamin K", "Folic acid",
                "Calcium");

        TestQuestion testQuestion5 = new TestQuestion(5, 6, QuestionType.TRUE_FALSE.toString(),
                "Narendra Modi is Prime Minister of india?",
                "", "", "", "", "");

        TestQuestion testQuestion6 = new TestQuestion(6, 7, QuestionType.FILL_THE_BLANKS.toString(),
                "____ is origin of COVID-19 outbreak?",
                "", "", "", "", "");

        TestQuestion testQuestion7 = new TestQuestion(7, 8, QuestionType.MULTI_LINE_ANSWER.toString(),
                "Explain Android Activity Lifecycle?",
                "", "", "", "", "");

        testQuestionList.add(testQuestion);
        testQuestionList.add(testQuestion1);
        testQuestionList.add(testQuestion2);
        testQuestionList.add(testQuestion3);
        testQuestionList.add(testQuestion4);
        testQuestionList.add(testQuestion5);
        testQuestionList.add(testQuestion6);
        testQuestionList.add(testQuestion7);
        return testQuestionList;
    }


    private void configureActionBar() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.app_bar, null);
        tvTimer = (TextView) mCustomView.findViewById(R.id.tvtimer);
        tvPause = (TextView) mCustomView.findViewById(R.id.tvPause);
        tvTitle = (TextView) mCustomView.findViewById(R.id.tvTitle);
        tvTitle.setText("Demo Test");
        startTimer(60 * 60 * 1000);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    private void prepareQuestPaletGridList(String filterType) {
        new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED,filterType);
        List<TestQuestion> QuestionList = sortQuestListByFilter(testQuestionList, filterType);
        if(QuestionList.size() > 0){
            mBinding.tvNoQuestions.setVisibility(View.GONE);
        }else{
            mBinding.tvNoQuestions.setVisibility(View.VISIBLE);
        }
        mBinding.questionsPaletListRecyclerView.setVisibility(View.GONE);
        mBinding.questionsPaletGridRecyclerView.setVisibility(View.VISIBLE);
        mBinding.questionsPaletGridRecyclerView.setNestedScrollingEnabled(false);
        mBinding.questionsPaletGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        questionPaletAdapter = new QuestionPaletAdapter(QuestionList, this, QuestionSortType.GRID.toString(), this);
        mBinding.questionsPaletGridRecyclerView.setAdapter(questionPaletAdapter);
        questionPaletAdapter.notifyDataSetChanged();
    }

    private void prepareQuestPaletList(String filterType) {
       // PreferenceManager preferenceManager = new PreferenceManager(this);
        new PreferenceManager(getApplicationContext()).saveString(Constant.FILTER_APPLIED,filterType);
        List<TestQuestion> QuestionList = sortQuestListByFilter(testQuestionList, filterType);
        if(QuestionList.size() > 0){
            mBinding.tvNoQuestions.setVisibility(View.GONE);
        }else{
            mBinding.tvNoQuestions.setVisibility(View.VISIBLE);
        }
        mBinding.questionsPaletGridRecyclerView.setVisibility(View.GONE);
        mBinding.questionsPaletListRecyclerView.setVisibility(View.VISIBLE);
        mBinding.questionsPaletListRecyclerView.setNestedScrollingEnabled(false);
        questionPaletAdapter = new QuestionPaletAdapter(QuestionList, this, QuestionSortType.LIST.toString(), this);
        mBinding.questionsPaletListRecyclerView.setHasFixedSize(true);
        mBinding.questionsPaletListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.questionsPaletListRecyclerView.setAdapter(questionPaletAdapter);
        questionPaletAdapter.notifyDataSetChanged();
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


    /*public class TabsPagerAdapter extends FragmentStatePagerAdapter {
        private final List<TestFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabsPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        public void destroyItemState(int position) {
            mFragmentList.remove(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
            if (getItemPosition(object) == POSITION_NONE) {
                destroyItemState(position);
            }
        }

        @Override
        public TestFragment getItem(int position) {
            //Toast.makeText(CourseActivity.this, "Get Item " + position, Toast.LENGTH_SHORT).show();
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(TestFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }*/



    private List<TestQuestion> sortQuestListByFilter(List<TestQuestion> questions, String questFilterType) {
        List<TestQuestion> questFilterList = new ArrayList<>();
        for (TestQuestion question : questions) {

            if (questFilterType.equals(QuestionFilterType.ATTEMPTED.toString()) && question.isAttempted()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.UNATTEMPTED.toString())
                    && question.isVisited() && !question.isAttempted()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.UNSEEN.toString()) && !question.isAttempted()
                    && !question.isVisited() && !question.isMarked()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.MARKED.toString()) && question.isMarked()) {
                questFilterList.add(question);
            }
            if (questFilterType.equals(QuestionFilterType.ALL.toString())) {
                questFilterList = questions;
            }
        }

        return questFilterList;
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