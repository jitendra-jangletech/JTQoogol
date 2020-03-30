package com.jangletech.qoogol.Test;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    ViewPager mViewPager;
    TabLayout tabLayout;
    public static int selectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.result_tabs);
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);

       /* tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                selectedPos = position;

                tabLayout = (TabLayout) findViewById(R.id.result_tabs);
                if(tabLayout.getTabAt(position).getTag()== null) {
                    //Toast.makeText(QuestionsActivity.this, "" + tabLayout.getTabAt(position).getTag(), Toast.LENGTH_SHORT).show();
                    tabLayout.getTabAt(position).setCustomView(R.layout.layout_tab_correct);
                    tabLayout.getTabAt(position).setTag("Seen");
                    View linearLayout = tab.getCustomView();
                    TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
                    tv.setText("" + (position + 1));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

    }

    private void visitedTabColors(int selectedPosition) {
        for (int i = selectedPosition; i >=0 ; i--) {
            LinearLayout tabsContainer = (LinearLayout) tabLayout.getChildAt(i);
            LinearLayout item = (LinearLayout) tabsContainer.getChildAt(i);
            TextView tv = (TextView) item.getChildAt(i);
            tv.setTextColor(Color.YELLOW);
        }

    }



    private void setupViewPager(ViewPager viewPager){
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i <100 ; i++) {
            adapter.addFragment(new QuestFragment(),""+(i+1));
            viewPager.setAdapter(adapter);
        }
    }

    public  class TabsPagerAdapter extends FragmentPagerAdapter {
        private final List<QuestFragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public QuestFragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(QuestFragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
