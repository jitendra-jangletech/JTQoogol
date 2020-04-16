package com.jangletech.qoogol;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.Test.TestFragment;
import com.jangletech.qoogol.activities.ViewPagerAdapterNew;
import com.jangletech.qoogol.adapter.QuestionPaletAdapter;
import com.jangletech.qoogol.databinding.ActivityCourseBinding;
import com.jangletech.qoogol.dialog.SubmitTestDialog;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.enums.QuestionType;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.TestQuestion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseActivity extends AppCompatActivity implements QuestionPaletAdapter.QuestClickListener, SubmitTestDialog.SubmitDialogClickListener, QueViewClick {

    private static final String TAG = "CourseActivity";
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
    private HashMap<Integer, TestQuestion> mapQuestAnswer = new HashMap<>();
    Long milliLeft, min, sec;
    SubmitTestDialog submitTestDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course);
        viewPager = (ViewPager2) findViewById(R.id.course_viewpager);
        resulttabs = findViewById(R.id.result_tabs);
        btnSubmitTest = findViewById(R.id.btnSubmitTest);
        //resulttabs.setupWithViewPager(viewPager);
        //setupViewPager(viewPager);
        setQuestionList();
        viewPager.setAdapter(createCardAdapter());
        /*setDefaultQuestView();
        setVisitedInit();
        new TabLayoutMediator(resulttabs, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab " + (position + 1));
                        setVisitedView();
                        viewPager.setCurrentItem(resulttabs.getSelectedTabPosition(), true);
                    }
                }).attach();
*/
        configureActionBar();
        /*resulttabs.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                //pos = tab.getPosition();
                setVisitedView();
                pos = resulttabs.getSelectedTabPosition();
                Toast.makeText(CourseActivity.this, "Tab Selected "+pos, Toast.LENGTH_SHORT).show();


                //String strQuestTag = resulttabs.getTabAt(position).getTag().toString();
                viewPager.setCurrentItem(resulttabs.getSelectedTabPosition(), true);
                //viewPager.getCurrentItem();
                //Toast.makeText(CourseActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                //Toast.makeText(CourseActivity.this, ""+position, Toast.LENGTH_SHORT).show();
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
                prepareQuestPaletGridList();
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
            prepareQuestPaletGridList();
            //Toast.makeText(this, "Grid View Selected", Toast.LENGTH_SHORT).show();
        });

        mBinding.listView.setOnClickListener(v -> {
            prepareQuestPaletList();
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

   /* public void getQuestNo(QueViewClick queViewClick) {
        int position = resulttabs.getSelectedTabPosition();
        String strQuestTag = resulttabs.getTabAt(position).getTag().toString();
        queViewClick.getQueViewClick(strQuestTag, position);
    }*/

    private ViewPagerAdapterNew createCardAdapter() {
        ViewPagerAdapterNew adapter = new ViewPagerAdapterNew(this);
        return adapter;
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

    public void setEmptyView() {
        int position = resulttabs.getSelectedTabPosition();
        TabLayout.Tab tab = resulttabs.getTabAt(position);
        tab.setCustomView(null);
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

   /* public void setInCorrectView() {
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
    @Override
    public void onQuestionSelected(int questId) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        viewPager.setCurrentItem(questId, true);
        //Toast.makeText(this, "" + questId, Toast.LENGTH_SHORT).show();
    }




   /* public class TabsPagerAdapter extends FragmentStatePagerAdapter {
        private final List<TestFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabsPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public TestFragment getItem(int position) {
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

        mapQuestAnswer.put(0, testQuestion);
        mapQuestAnswer.put(1, testQuestion1);
        mapQuestAnswer.put(2, testQuestion2);
        mapQuestAnswer.put(3, testQuestion3);
        mapQuestAnswer.put(4, testQuestion4);
        mapQuestAnswer.put(5, testQuestion5);
        mapQuestAnswer.put(6, testQuestion6);
        mapQuestAnswer.put(7, testQuestion7);

        return testQuestionList;
    }

    /*private List<TestQuestion> setQuestionList() {
        List<TestQuestion> testQuestionList = new ArrayList();
        List<Answer> answersList = new ArrayList<>();
        Answer answer = new Answer(1, "http://");
        Answer answer1 = new Answer(2, "HTML");
        Answer answer2 = new Answer(3, "DOC");
        Answer answer3 = new Answer(4, "URL");
        Answer answer4 = new Answer(5, "BMP");
        answersList.add(answer);
        answersList.add(answer1);
        answersList.add(answer2);
        answersList.add(answer3);
        answersList.add(answer4);

        TestQuestion testQuestion = new TestQuestion(0, 1, "MCQ",
                "Web Pages are saved in which of the following format?", answersList);

        List<Answer> answersLis = new ArrayList<>();
        Answer answe = new Answer(1, "Management");
        Answer answe1 = new Answer(2, "Processing");
        Answer answe2 = new Answer(3, "Utility");
        Answer answe3 = new Answer(4, "Application");
        Answer answe4 = new Answer(5, "Embedded");
        answersLis.add(answe);
        answersLis.add(answe1);
        answersLis.add(answe2);
        answersLis.add(answe3);
        answersLis.add(answe4);

        TestQuestion testQuestion1 = new TestQuestion(1, 2, "SCQ",
                "System software acts as a bridge between the hardware and _____ software", answersLis);

        List<Answer> answersLi = new ArrayList<>();
        Answer answ = new Answer(1, "Ram Sing Yadav");
        Answer answ1 = new Answer(2, "Arpinder Singh");
        Answer answ2 = new Answer(3, "PV Sindhu");
        Answer answ3 = new Answer(4, "Sania Mirza");
        Answer answ4 = new Answer(5, "Sachin Tendulkar");
        answersLi.add(answ);
        answersLi.add(answ1);
        answersLi.add(answ2);
        answersLi.add(answ3);
        answersLi.add(answ4);


        TestQuestion testQuestion2 = new TestQuestion(2, 3, "SCQ",
                "Who is appointed as the brand ambassador of VISA recently?",
                answersLi);

        List<Answer> answersL = new ArrayList<>();
        Answer ans = new Answer(1, "Jahangir");
        Answer ans1 = new Answer(2, "Qutub-ud-din-Albak");
        Answer ans2 = new Answer(3, "Akbar");
        Answer ans3 = new Answer(4, "Birbal");
        Answer ans4 = new Answer(5, "Aurangjeb");
        answersL.add(ans);
        answersL.add(ans1);
        answersL.add(ans2);
        answersL.add(ans3);
        answersL.add(ans4);

        TestQuestion testQuestion3 = new TestQuestion(3, 4, "MCQ",
                "Who built Jama Masjid at Delhi?",
                answersL);

        List<Answer> answers = new ArrayList<>();
        Answer an = new Answer(1, "Vitamin A");
        Answer an1 = new Answer(2, "Vitamin D");
        Answer an2 = new Answer(3, "Vitamin K");
        Answer an3 = new Answer(4, "Folic acid");
        Answer an4 = new Answer(5, "Calcium");
        answers.add(an);
        answers.add(an1);
        answers.add(an2);
        answers.add(an3);
        answers.add(an4);

        TestQuestion testQuestion4 = new TestQuestion(4, 5, QuestionType.MCQ.toString(),
                "Which of the following helps in the blood clotting?",
                answers);


        TestQuestion testQuestion5 = new TestQuestion(5, 6, QuestionType.TRUE_FALSE.toString(),
                "Narendra Modi is Prime Minister of india?",
                answers);


        TestQuestion testQuestion6 = new TestQuestion(6, 7, QuestionType.FILL_THE_BLANKS.toString(),
                "____ is origin of COVID-19 outbreak?",
                answers);

        TestQuestion testQuestion7 = new TestQuestion(7, 8, QuestionType.MULTI_LINE_ANSWER.toString(),
                "Explain Android Activity Lifecycle?",
                answers);

        testQuestionList.add(testQuestion);
        testQuestionList.add(testQuestion1);
        testQuestionList.add(testQuestion2);
        testQuestionList.add(testQuestion3);
        testQuestionList.add(testQuestion4);
        testQuestionList.add(testQuestion5);
        testQuestionList.add(testQuestion6);
        testQuestionList.add(testQuestion7);

        mapQuestAnswer.put(0, testQuestion);
        mapQuestAnswer.put(1, testQuestion1);
        mapQuestAnswer.put(2, testQuestion2);
        mapQuestAnswer.put(3, testQuestion3);
        mapQuestAnswer.put(4, testQuestion4);
        mapQuestAnswer.put(5, testQuestion5);
        mapQuestAnswer.put(6, testQuestion6);
        mapQuestAnswer.put(7, testQuestion7);

        return testQuestionList;
    }*/

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

    private void prepareQuestPaletGridList() {
        Log.d(TAG, "prepareQuestPaletGridList size : " + testQuestionList.size());
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


    public class TabsPagerAdapter extends FragmentStatePagerAdapter {
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
    }


    /*public class ViewPagerAdapter extends PagerAdapter implements View.OnClickListener, QuestReportDialog.QuestReportDialogListener {

        private static final String TAG = "ViewPagerAdapter";
        private Context mContext;
        private List<TestQuestion> testQuestions;
        private SingleChoiceQuestionBinding singleChoiceQuestionBinding;
        private MultiChoiceQuestionBinding multiChoiceQuestionBinding;
        private FillInTheBlanksLayoutBinding fillInTheBlanksLayoutBinding;
        private TrueFalseLayoutBinding trueFalseLayoutBinding;
        private MultiLineQuestAnsBinding multiLineQuestAnsBinding;
        private HashMap<Integer, Chip> mapScqChips = new HashMap();
        private HashMap<Integer, Chip> mapMcqChips = new HashMap();
        private HashMap<Integer, Chip> mapTrueFalseChips = new HashMap();

        public ViewPagerAdapter(Context context, List<TestQuestion> testQuestionList) {
            mContext = context;
            testQuestions = testQuestionList;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return testQuestions.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            Toast.makeText(mContext, "Hello " + position, Toast.LENGTH_SHORT).show();
            if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.SCQ.toString())) {
                singleChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.single_choice_question, container, false);
                setScqQuestionLayout(singleChoiceQuestionBinding, position);
                Log.d(TAG, "After setScqQuestionLayout: ");
                container.addView(singleChoiceQuestionBinding.getRoot());
                return singleChoiceQuestionBinding.getRoot();
            }

            if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.MCQ.toString())) {
                multiChoiceQuestionBinding = DataBindingUtil.inflate(inflater, R.layout.multi_choice_question, container, false);
                setMcqQuestionLayout(multiChoiceQuestionBinding, position);
                container.addView(multiChoiceQuestionBinding.getRoot());
                return multiChoiceQuestionBinding.getRoot();
            }

            if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.TRUE_FALSE.toString())) {
                trueFalseLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.true_false_layout, container, false);
                setTrueFalseLayout(trueFalseLayoutBinding, position);
                container.addView(trueFalseLayoutBinding.getRoot());
                return trueFalseLayoutBinding.getRoot();
            }

            if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.FILL_THE_BLANKS.toString())) {
                fillInTheBlanksLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fill_in_the_blanks_layout, container, false);
                setFillTheBlanksLayout(fillInTheBlanksLayoutBinding, position);
                container.addView(fillInTheBlanksLayoutBinding.getRoot());
                return fillInTheBlanksLayoutBinding.getRoot();
            }

            if (testQuestions.get(position).getQuestType().equalsIgnoreCase(QuestionType.MULTI_LINE_ANSWER.toString())) {
                multiLineQuestAnsBinding = DataBindingUtil.inflate(inflater, R.layout.multi_line_quest_ans, container, false);
                setMultiLineQuestAnsLayout(multiLineQuestAnsBinding, position);
                container.addView(multiLineQuestAnsBinding.getRoot());
                return multiLineQuestAnsBinding.getRoot();
            }
            return null;
        }

        private void setMultiLineQuestAnsLayout(MultiLineQuestAnsBinding multiLineQuestAnsBinding, int position) {
            setTimer(multiLineQuestAnsBinding.tvtimer, 0, 0);
            answerCharCounter(multiLineQuestAnsBinding.etMultiLineAnswer, multiLineQuestAnsBinding.tvCharCounter, 200);
            multiLineQuestAnsBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
            multiLineQuestAnsBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
        }

        private void setFillTheBlanksLayout(FillInTheBlanksLayoutBinding fillInTheBlanksBinding, int position) {
            setTimer(fillInTheBlanksBinding.tvtimer, 0, 0);
            answerCharCounter(fillInTheBlanksBinding.etAnswer, fillInTheBlanksBinding.tvCharCounter, 10);
            fillInTheBlanksBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
            fillInTheBlanksBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
        }

        private void setTrueFalseLayout(TrueFalseLayoutBinding trueFalseLayoutBinding, int position) {
            setTimer(trueFalseLayoutBinding.tvQuestTimer, 0, 0);
            trueFalseLayoutBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
            trueFalseLayoutBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
            setTrueFalseAnswer(trueFalseLayoutBinding.trueFalseAnswerChipGrp);
            trueFalseLayoutBinding.trueFalseAnswerChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
                Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
                if (chip != null) {
                    mapTrueFalseChips.put(id, chip);
                    setCheckedChip(mapTrueFalseChips);
                }
            });
        }

        private void setScqQuestionLayout(SingleChoiceQuestionBinding mBinding, int position) {
            Log.d(TAG, "setScqQuestionLayout: ");
            setTimer(mBinding.tvQuestTimer, 0, 0);
            mBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
            mBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
            if (testQuestions.get(position).isVisited()) {
                setAnswers(mBinding.singleChoiceAnswerGrp);
            }
            setChipAnswers(mBinding.singleChoiceAnswerGrp, testQuestions.get(position).getAnswerList(), QuestionType.SCQ.toString());
            mBinding.singleChoiceAnswerGrp.setOnCheckedChangeListener((chipGroup, id) -> {
                Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
                if (chip != null) {
                    mapScqChips.put(id, chip);
                    setCheckedChip(mapScqChips);
                    setScqMcqChipAnswers(mBinding.singleChoiceAnswerGrp);
                }
            });

            mBinding.imgReport.setOnClickListener(v -> {
                Toast.makeText(mContext, "Hello", Toast.LENGTH_SHORT).show();
                QuestReportDialog questReportDialog = new QuestReportDialog(mContext, this);
                questReportDialog.show();
            });

        }

        private void setAnswers(ChipGroup chipGroup) {
            TestQuestion testQuestion = mapQuestAnswer.get(pos);
            //for (int i = 0; i <chipGroup.getChildCount() ; i++) {
            Chip chip = null;
            if (testQuestion.isAns1()) {
                chip = (Chip) chipGroup.getChildAt(0);
                chip.setChecked(true);
            }
            if (testQuestion.isAns2()) {
                chip = (Chip) chipGroup.getChildAt(1);
                chip.setChecked(true);
            }
            if (testQuestion.isAns3()) {
                chip = (Chip) chipGroup.getChildAt(2);
                chip.setChecked(true);
            }
            if (testQuestion.isAns4()) {
                chip = (Chip) chipGroup.getChildAt(3);
                chip.setChecked(true);
            }
            if (testQuestion.isAns5()) {
                chip = (Chip) chipGroup.getChildAt(4);
                chip.setChecked(true);
            }
            // }
        }


        private void setMcqQuestionLayout(MultiChoiceQuestionBinding multiChoiceQuestionBinding, int position) {
            setTimer(multiChoiceQuestionBinding.tvQuestTimer, 0, 0);
            multiChoiceQuestionBinding.tvQuestNo.setText(String.valueOf(testQuestions.get(position).getQuestNo()));
            multiChoiceQuestionBinding.tvQuestion.setText(testQuestions.get(position).getQuestionDesc());
            setChipAnswers(multiChoiceQuestionBinding.multiChoiceAnswerGrp, testQuestions.get(position).getAnswerList(), QuestionType.SCQ.toString());
            multiChoiceQuestionBinding.multiChoiceAnswerGrp.setOnCheckedChangeListener((chipGroup, id) -> {
                setCheckedChip(mapMcqChips);

            });
        }

        private void setTimer(TextView timer, int seconds, int minutes) {

            new CountDownTimer(60 * 1000 * 60, 1000) {
                int timerCountSeconds = seconds;
                int timerCountMinutes = minutes;

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
                            timer.setText(String.valueOf("0" + timerCountMinutes + ":0" + timerCountSeconds));
                        } else {
                            timer.setText(String.valueOf("0" + timerCountMinutes + ":" + timerCountSeconds));
                        }
                    } else {
                        if (timerCountSeconds < 10) {
                            timer.setText(String.valueOf(timerCountMinutes + ":0" + timerCountSeconds));
                        } else {
                            timer.setText(String.valueOf(timerCountMinutes + ":" + timerCountSeconds));
                        }
                    }
                }

                public void onFinish() {
                    timer.setText("done!");
                }
            }.start();
        }

        private void setChipAnswers(ChipGroup chipGroup, List<Answer> answers, String questType) {
            chipGroup.removeAllViews();
            for (int i = 0; i < answers.size(); i++) {
                Answer answer = answers.get(i);
                Chip chip = (Chip) LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.answer_chip, chipGroup, false);
                chip.setText(answer.getAnswer());
                chip.setOnClickListener(this);
                if (QuestionType.SCQ.toString().equals(questType)) {
                    chip.setTag(QuestionType.SCQ);
                    mapScqChips.put(i, chip);
                }
                if (QuestionType.MCQ.toString().equals(questType)) {
                    chip.setTag(QuestionType.MCQ);
                    mapMcqChips.put(i, chip);
                }
                chip.setId(i);
                chipGroup.addView(chip);
            }
        }

        private void setTrueFalseAnswer(ChipGroup chipGroup) {
            String[] trueFalse = {"True", "False"};
            chipGroup.removeAllViews();
            for (int i = 0; i < trueFalse.length; i++) {
                Chip chip = (Chip) LayoutInflater.from(chipGroup.getContext()).inflate(R.layout.true_false_chip, chipGroup, false);
                chip.setText(trueFalse[i]);
                chip.setTag(QuestionType.TRUE_FALSE.toString());
                chip.setOnClickListener(this);
                chip.setId(i);
                mapTrueFalseChips.put(i, chip);
                chipGroup.addView(chip);
            }
        }

        private void setCheckedChip(HashMap<Integer, Chip> map) {
            for (int i = 0; i < map.size(); i++) {
                if (map.get(i).isChecked()) {
                    map.get(i).setTextColor(Color.WHITE);
                } else {
                    map.get(i).setTextColor(Color.BLACK);
                }
            }
        }

        private void answerCharCounter(EditText etAnswer, TextView tvCounter, int maxWordLength) {

            InputFilter filter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    return null;
                }
            };
            etAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int wordCount = 0;
                    if (s.toString().contains(" ")) {
                        String[] words = s.toString().split(" ", -1);
                        wordCount = words.length;
                    }

                    if (wordCount == maxWordLength) {
                        etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length())});
                    }
                    Toast.makeText(mContext, "Word count is: " + wordCount, Toast.LENGTH_SHORT).show();
                    tvCounter.setText(maxWordLength - wordCount + "/" + String.valueOf(maxWordLength));
                }
            });
        }

        @Override
        public void onClick(View v) {
            Chip chip = (Chip) v;
            if (chip != null && chip.getTag() != null) {
                if (chip.getTag().toString().equalsIgnoreCase(QuestionType.SCQ.toString())) {
                    mapScqChips.put(chip.getId(), chip);
                    setCheckedChip(mapScqChips);
                }
                if (chip.getTag().toString().equalsIgnoreCase(QuestionType.MCQ.toString())) {
                    mapMcqChips.put(chip.getId(), chip);
                    setCheckedChip(mapMcqChips);
                }

                if (chip.getTag().toString().equalsIgnoreCase(QuestionType.TRUE_FALSE.toString())) {
                    mapTrueFalseChips.put(chip.getId(), chip);
                    setCheckedChip(mapTrueFalseChips);
                }
            }
        }

        private void showQuestReportDialog() {

        }

        private void setScqMcqChipAnswers(ChipGroup chipGroup) {
            TestQuestion testQuestion = mapQuestAnswer.get(pos);
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    if (i == 0)
                        testQuestion.setAns1(true);
                    if (i == 1)
                        testQuestion.setAns2(true);
                    if (i == 2)
                        testQuestion.setAns3(true);
                    if (i == 3)
                        testQuestion.setAns4(true);
                    if (i == 4)
                        testQuestion.setAns5(true);

                } else {
                    if (i == 0)
                        testQuestion.setAns1(false);
                    if (i == 1)
                        testQuestion.setAns2(false);
                    if (i == 2)
                        testQuestion.setAns3(false);
                    if (i == 3)
                        testQuestion.setAns4(false);
                    if (i == 4)
                        testQuestion.setAns5(false);
                }
            }
            testQuestion.setVisited(true);
            mapQuestAnswer.put(pos, testQuestion);
        }

        @Override
        public void onReportQuestSubmitClick() {
            Toast.makeText(mContext, "submit Click", Toast.LENGTH_SHORT).show();
        }
    }*/


}