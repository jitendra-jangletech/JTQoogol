package com.jangletech.qoogol;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ActivityCourseBinding activityCourseBinding;
    ViewPager viewPager;
    TabLayout resulttabs;
    int position;
    TextView prev, next;
    TestFragment testFragment;
    QueViewClick queViewClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCourseBinding = DataBindingUtil.setContentView(this, R.layout.activity_course);
        viewPager = (ViewPager) findViewById(R.id.course_viewpager);
        resulttabs = findViewById(R.id.result_tabs);
        prev = findViewById(R.id.btnPrevious);
        next = findViewById(R.id.btnNext);
        resulttabs.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        setDefaultQuestView();
        setVisitedInit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        resulttabs.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                setVisitedView();
                int position = resulttabs.getSelectedTabPosition();
                String strQuestTag = resulttabs.getTabAt(position).getTag().toString();
                queViewClick.onTabClickClick(tab.getPosition() + 1,strQuestTag, position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        prev.setOnClickListener(v ->
               viewPager.setCurrentItem(resulttabs.getSelectedTabPosition() - 1, true));

        next.setOnClickListener(v ->
                viewPager.setCurrentItem(resulttabs.getSelectedTabPosition() + 1, true));
    }

    public void setListenerInstance(QueViewClick queViewClick) {
        this.queViewClick = queViewClick;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void getQuestNo(QueViewClick queViewClick){
        int position = resulttabs.getSelectedTabPosition();
        String strQuestTag = resulttabs.getTabAt(position).getTag().toString();
        queViewClick.getQueViewClick(strQuestTag,position);
    }

    public void setVisitedView() {
        int position = resulttabs.getSelectedTabPosition();
        Log.d("", "setVisitedView Tag : "+resulttabs.getTabAt(position).getTag());
        if(resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Default")
                || resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")){
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
        if(resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")) {
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
        if(resulttabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")){
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
        List<String> questTypeList = new ArrayList();
        for (int i = 0; i <100 ; i++) {
            if(i % 2 == 0){
                questTypeList.add("SCQ");
            }else{
                questTypeList.add("MCQ");
            }
        }

        for (int i = 0; i < 100; i++) {
            adapter.addFragment(new TestFragment(questTypeList.get(i)), "" + (i + 1));
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
        if(item.getItemId() == android.R.id.home) {
            if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
            else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}