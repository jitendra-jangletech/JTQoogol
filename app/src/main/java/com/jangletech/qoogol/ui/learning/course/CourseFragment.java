package com.jangletech.qoogol.ui.learning.course;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.Test.TestFragment;
import com.jangletech.qoogol.databinding.FragmentCourseBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CourseFragment extends Fragment{

    private static final String TAG = "CourseFragment";
    private CourseViewModel mViewModel;
    private FragmentCourseBinding fragmentCourseBinding;
    public static int selectedPos;
    int position;

    public static CourseFragment newInstance() {
        return new CourseFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentCourseBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_course, container, false);
        setupViewPager(fragmentCourseBinding.viewpager);
        fragmentCourseBinding.resultTabs.setupWithViewPager(fragmentCourseBinding.viewpager);

        setDefaultQuestView();
        setVisitedInit();

        fragmentCourseBinding.resultTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                setVisitedView();
                fragmentCourseBinding.tvQuestNo.setText("" + (tab.getPosition() + 1));
                setQuestNoView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Toast.makeText(getActivity(), "Tab Selected : " + tab.getPosition()+1, Toast.LENGTH_SHORT).show();
            }
        });

        fragmentCourseBinding.incorrect.setOnClickListener(v -> {
            setInCorrectView();
            setQuestNoView();
        });

        fragmentCourseBinding.correct.setOnClickListener(v -> {
            setCorrectView();
            setQuestNoView();
        });


        fragmentCourseBinding.btnPrevious.setOnClickListener(v ->
                fragmentCourseBinding.viewpager.setCurrentItem(fragmentCourseBinding.resultTabs.getSelectedTabPosition() - 1, true));

        fragmentCourseBinding.btnNext.setOnClickListener(v ->
                fragmentCourseBinding.viewpager.setCurrentItem(fragmentCourseBinding.resultTabs.getSelectedTabPosition() + 1, true));

        return fragmentCourseBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void setupViewPager(ViewPager viewPager) {
        TabsPagerAdapter adapter = new TabsPagerAdapter(getFragmentManager());
        for (int i = 0; i < 50; i++) {
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
        int position = fragmentCourseBinding.resultTabs.getSelectedTabPosition();
        TabLayout.Tab tab = fragmentCourseBinding.resultTabs.getTabAt(position);
        tab.setCustomView(null);
    }

    public void setCorrectView() {
        int position = fragmentCourseBinding.resultTabs.getSelectedTabPosition();
        if(fragmentCourseBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")) {
            setEmptyView();
            fragmentCourseBinding.resultTabs.getTabAt(position).setCustomView(R.layout.layout_tab_correct);
            fragmentCourseBinding.resultTabs.getTabAt(position).setTag("Correct");
            View linearLayout = fragmentCourseBinding.resultTabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (fragmentCourseBinding.resultTabs.getTabAt(position).getPosition() + 1));
        }
    }

    public void setInCorrectView() {
        int position = fragmentCourseBinding.resultTabs.getSelectedTabPosition();
        if(fragmentCourseBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")){
            setEmptyView();
            fragmentCourseBinding.resultTabs.getTabAt(position).setCustomView(R.layout.layout_tab_incorrect);
            fragmentCourseBinding.resultTabs.getTabAt(position).setTag("Incorrect");
            View linearLayout = fragmentCourseBinding.resultTabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (fragmentCourseBinding.resultTabs.getTabAt(position).getPosition() + 1));
        }
    }

    public void setVisitedView() {

        int position = fragmentCourseBinding.resultTabs.getSelectedTabPosition();
        Log.d(TAG, "setVisitedView Tag : "+fragmentCourseBinding.resultTabs.getTabAt(position).getTag());
        if(fragmentCourseBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Default")
        || fragmentCourseBinding.resultTabs.getTabAt(position).getTag().toString().equalsIgnoreCase("Visited")){
            setEmptyView();
            fragmentCourseBinding.resultTabs.getTabAt(position).setCustomView(R.layout.layout_tab_visited);
            fragmentCourseBinding.resultTabs.getTabAt(position).setTag("Visited");
            View linearLayout = fragmentCourseBinding.resultTabs.getTabAt(position).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (fragmentCourseBinding.resultTabs.getTabAt(position).getPosition() + 1));
        }
    }

    public void setDefaultQuestView() {
        for (int i = 0; i < fragmentCourseBinding.resultTabs.getTabCount(); i++) {
            fragmentCourseBinding.resultTabs.getTabAt(i).setCustomView(R.layout.layout_tab_default);
            fragmentCourseBinding.resultTabs.getTabAt(i).setTag("Default");
            View linearLayout = fragmentCourseBinding.resultTabs.getTabAt(i).getCustomView();
            TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
            tv.setText("" + (fragmentCourseBinding.resultTabs.getTabAt(i).getPosition() + 1));

        }
    }

    public void setVisitedInit() {
        setEmptyView();
        fragmentCourseBinding.resultTabs.getTabAt(0).setCustomView(R.layout.layout_tab_visited);
        fragmentCourseBinding.resultTabs.getTabAt(0).setTag("Visited");
        View linearLayout = fragmentCourseBinding.resultTabs.getTabAt(0).getCustomView();
        TextView tv = (TextView) linearLayout.findViewById(R.id.txt);
        tv.setText("1");
    }

    public void setQuestNoView(){
        int position = fragmentCourseBinding.resultTabs.getSelectedTabPosition();
        String strQuestTag = fragmentCourseBinding.resultTabs.getTabAt(position).getTag().toString();
        if(strQuestTag.equalsIgnoreCase("Correct")){
            fragmentCourseBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_correct);
            fragmentCourseBinding.tvQuestNo.setTag("Correct");
            fragmentCourseBinding.tvQuestNo.setText(""+(position+1));
        }
        if(strQuestTag.equalsIgnoreCase("Incorrect")){
            fragmentCourseBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_incorrect);
            fragmentCourseBinding.tvQuestNo.setTag("Incorrect");
            fragmentCourseBinding.tvQuestNo.setText(""+(position+1));
        }
        if(strQuestTag.equalsIgnoreCase("Visited")){
            fragmentCourseBinding.tvQuestNo.setBackgroundResource(R.drawable.bg_quest_visited);
            fragmentCourseBinding.tvQuestNo.setTag("Visited");
            fragmentCourseBinding.tvQuestNo.setText(""+(position+1));
        }
    }

    public void setQuestTimer(){
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int seconds = 0;
                fragmentCourseBinding.tvQuestTimer.setText(""+(++seconds));
            }
        }, 0, 1000);
    }

}
