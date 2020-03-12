package com.jangletech.qoogol.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.Test.TestFragment;
import com.jangletech.qoogol.databinding.ActivityViewPagerBinding;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity {

    private static final String TAG = "ViewPagerActivity";
    private ActivityViewPagerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_view_pager);
        setupViewPager(mBinding.viewpager);
        mBinding.resultTabs.setupWithViewPager(mBinding.viewpager);

        setDefaultQuestView();
        setVisitedInit();

        mBinding.resultTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setVisitedView();
                mBinding.tvQuestNo.setText("" + (tab.getPosition() + 1));
                setQuestNoView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mBinding.incorrect.setOnClickListener(v -> {
            setInCorrectView();
            setQuestNoView();
        });

        mBinding.correct.setOnClickListener(v -> {
            setCorrectView();
            setQuestNoView();
        });


        mBinding.btnPrevious.setOnClickListener(v ->
                mBinding.viewpager.setCurrentItem(mBinding.resultTabs.getSelectedTabPosition() - 1, true));

        mBinding.btnNext.setOnClickListener(v ->
                mBinding.viewpager.setCurrentItem(mBinding.resultTabs.getSelectedTabPosition() + 1, true));
    }

    public void setCorrectView() {
        int position = mBinding.resultTabs.getSelectedTabPosition();
        if(mBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")) {
            setEmptyView();
            mBinding.resultTabs.getTabAt(position).setCustomView(R.layout.layout_tab_correct);
            mBinding.resultTabs.getTabAt(position).setTag("Correct");
            View linearLayout = mBinding.resultTabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (mBinding.resultTabs.getTabAt(position).getPosition() + 1));
        }
    }

    public void setInCorrectView() {
        int position = mBinding.resultTabs.getSelectedTabPosition();
        if(mBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")){
            setEmptyView();
            mBinding.resultTabs.getTabAt(position).setCustomView(R.layout.layout_tab_incorrect);
            mBinding.resultTabs.getTabAt(position).setTag("Incorrect");
            View linearLayout = mBinding.resultTabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (mBinding.resultTabs.getTabAt(position).getPosition() + 1));
        }
    }

    public void setVisitedView() {

        int position = mBinding.resultTabs.getSelectedTabPosition();
        Log.d(TAG, "setVisitedView Tag : "+mBinding.resultTabs.getTabAt(position).getTag());
        if(mBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Default")
                || mBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")){
            setEmptyView();
            mBinding.resultTabs.getTabAt(position).setCustomView(R.layout.layout_tab_visited);
            mBinding.resultTabs.getTabAt(position).setTag("Visited");
            View linearLayout = mBinding.resultTabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (mBinding.resultTabs.getTabAt(position).getPosition() + 1));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < 40; i++) {
            adapter.addFragment(new TestFragment(), "" + (i + 1));
            viewPager.setAdapter(adapter);
        }
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {
        private final List<TestFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager manager) {
            super(manager);
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

    public void setEmptyView() {
        int position = mBinding.resultTabs.getSelectedTabPosition();
        TabLayout.Tab tab = mBinding.resultTabs.getTabAt(position);
        tab.setCustomView(null);
    }

    public void setDefaultQuestView() {
        for (int i = 0; i < mBinding.resultTabs.getTabCount(); i++) {
            mBinding.resultTabs.getTabAt(i).setCustomView(R.layout.layout_tab_default);
            mBinding.resultTabs.getTabAt(i).setTag("Default");
            View linearLayout = mBinding.resultTabs.getTabAt(i).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (mBinding.resultTabs.getTabAt(i).getPosition() + 1));

        }
    }

    public void setVisitedInit() {
        setEmptyView();
        mBinding.resultTabs.getTabAt(0).setCustomView(R.layout.layout_tab_visited);
        mBinding.resultTabs.getTabAt(0).setTag("Visited");
        View linearLayout = mBinding.resultTabs.getTabAt(0).getCustomView();
        TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
        tv.setText("1");
    }

    public void setQuestNoView(){
        int position = mBinding.resultTabs.getSelectedTabPosition();
        String strQuestTag = mBinding.resultTabs.getTabAt(position).getTag().toString();
        if(strQuestTag.equalsIgnoreCase("Correct")){
            mBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_correct);
            mBinding.tvQuestNo.setTag("Correct");
            mBinding.tvQuestNo.setText(""+(position+1));
        }
        if(strQuestTag.equalsIgnoreCase("Incorrect")){
            mBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_incorrect);
            mBinding.tvQuestNo.setTag("Incorrect");
            mBinding.tvQuestNo.setText(""+(position+1));
        }
        if(strQuestTag.equalsIgnoreCase("Visited")){
            mBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_visited);
            mBinding.tvQuestNo.setTag("Visited");
            mBinding.tvQuestNo.setText(""+(position+1));
        }
    }
}
