package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentConnectionsBinding;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionsFragment extends BaseFragment {

    FragmentConnectionsBinding mbinding;
    int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mbinding = DataBindingUtil.inflate(inflater, R.layout.fragment_connections, container, false);

        return mbinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
        }
        setupViewPager(mbinding.viewpager);
        mbinding.resultTabs.setupWithViewPager(mbinding.viewpager);

        mbinding.resultTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                    getActionBar().setTitle( getContext().getString(R.string.connections_list));

                if (tab.getPosition() == 1)
                    getActionBar().setTitle( getContext().getString(R.string.friends));

                if (tab.getPosition() == 2)
                    getActionBar().setTitle( getContext().getString(R.string.followers));

                if (tab.getPosition() == 3)
                    getActionBar().setTitle( getContext().getString(R.string.following));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new ConnectionListFragment(), getContext().getString(R.string.connections_list));
        adapter.addFragment(new FriendsFragment(), getContext().getString(R.string.friends));
        adapter.addFragment(new FollowersFragment(), getContext().getString(R.string.followers));
        adapter.addFragment(new FollowingFragment(), getString(R.string.following));
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(position);
    }

    public static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
