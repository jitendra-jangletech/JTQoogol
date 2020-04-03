package com.jangletech.qoogol;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.Test.TestFragment;
import com.jangletech.qoogol.databinding.ActivityCourseBinding;
import com.jangletech.qoogol.listener.QueViewClick;
import com.jangletech.qoogol.model.TestQuestion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ActivityCourseBinding activityCourseBinding;
    ViewPager viewPager;
    TabLayout resulttabs;
    int position;
    Button btnMarkNext, btnSaveNext, btnClearResponse;
    TestFragment testFragment;
    QueViewClick queViewClick;
    TextView tvTimer,tvPause,tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCourseBinding = DataBindingUtil.setContentView(this, R.layout.activity_course);
        viewPager = (ViewPager) findViewById(R.id.course_viewpager);
        resulttabs = findViewById(R.id.result_tabs);
        btnMarkNext = findViewById(R.id.btnMarkNext);
        btnSaveNext = findViewById(R.id.btnSaveNext);
        btnClearResponse = findViewById(R.id.btnClearResponse);
        resulttabs.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        setDefaultQuestView();
        setVisitedInit();
        configureActionBar();
        resulttabs.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                setVisitedView();
                int position = resulttabs.getSelectedTabPosition();
                String strQuestTag = resulttabs.getTabAt(position).getTag().toString();
                queViewClick.onTabClickClick(tab.getPosition() + 1, strQuestTag, position);
                queViewClick.onTabPositionChange(resulttabs.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnSaveNext.setOnClickListener(v -> {
            queViewClick.onTabPositionChange(resulttabs.getSelectedTabPosition());
            viewPager.setCurrentItem(resulttabs.getSelectedTabPosition() + 1, true);
        });

        btnMarkNext.setOnClickListener(v -> {
            queViewClick.onTabPositionChange(resulttabs.getSelectedTabPosition());
            viewPager.setCurrentItem(resulttabs.getSelectedTabPosition() + 1, true);
        });

        btnClearResponse.setOnClickListener(v -> {

        });
    }

    private void setTimer(TextView timer){
        new CountDownTimer(60*30*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText(new SimpleDateFormat("mm:ss").format(new Date( millisUntilFinished)));
            }

            public void onFinish() {
                timer.setText("done!");
            }
        }.start();
    }

    public void setListenerInstance(QueViewClick queViewClick) {
        this.queViewClick = queViewClick;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void getQuestNo(QueViewClick queViewClick) {
        int position = resulttabs.getSelectedTabPosition();
        String strQuestTag = resulttabs.getTabAt(position).getTag().toString();
        queViewClick.getQueViewClick(strQuestTag, position);
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
    }


    private void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        List<TestQuestion> questTypeList = setQuestionList();
        for (int i = 0; i < questTypeList.size(); i++) {
            adapter.addFragment(new TestFragment(questTypeList, i), "" + (i + 1));
        }
        viewPager.setAdapter(adapter);
    }


    public class TabsPagerAdapter extends FragmentStatePagerAdapter {
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
        List<TestQuestion> testQuestionList = new ArrayList();
        TestQuestion testQuestion = new TestQuestion(0, 1, "MCQ",
                "Web Pages are saved in which of the following format?",
                "http://", "HTML", "DOC",
                "URL","BMP");
        TestQuestion testQuestion1 = new TestQuestion(1, 2, "SCQ",
                "System software acts as a bridge between the hardware and _____ software",
                "Management", "Processing", "Utility",
                "Application", "Embedded");
        TestQuestion testQuestion2 = new TestQuestion(2, 3, "SCQ",
                "Who is appointed as the brand ambassador of VISA recently?",
                "Ram Sing Yadav", "Arpinder Singh", "PV Sindhu",
                "Sania Mirza", "Sachin Tendulkar");
        TestQuestion testQuestion3 = new TestQuestion(3, 4, "MCQ",
                "Who built Jama Masjid at Delhi?",
                "Jahangir", "Qutub-ud-din-Albak", "Akbar",
                "Aurangjeb", "None of the above");
        TestQuestion testQuestion4 = new TestQuestion(4, 5, "MCQ",
                "Which of the following helps in the blood clotting?",
                "Vitamin A", "Vitamin D", "Vitamin K",
                "Folic acid", "Calcium");

        testQuestionList.add(testQuestion);
        testQuestionList.add(testQuestion1);
        testQuestionList.add(testQuestion2);
        testQuestionList.add(testQuestion3);
        testQuestionList.add(testQuestion4);

        return testQuestionList;
    }

    private void configureActionBar(){
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
        setTimer(tvTimer);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }
}