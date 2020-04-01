package com.jangletech.qoogol.Test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;

import java.util.ArrayList;
import java.util.List;

public class QuestFragment extends Fragment {

    private QuestViewModel mViewModel;
    TextView textView;
    ViewPager mViewPager;
    TabLayout tabLayout;

    public static QuestFragment newInstance() {
        return new QuestFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.quest_fragment, container, false);
        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout)view.findViewById(R.id.result_tabs);
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
        //Toast.makeText(getActivity(), ""+QuestionsActivity.selectedPos, Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(QuestViewModel.class);
    }

    private void setupViewPager(ViewPager viewPager){
        TabsPagerAdapter adapter = new TabsPagerAdapter(getFragmentManager());
        for (int i = 0; i <100 ; i++) {
            adapter.addFragment(new TestFragment(""),""+(i+1));
            viewPager.setAdapter(adapter);
        }
    }

    public  class TabsPagerAdapter extends FragmentPagerAdapter {
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

}
