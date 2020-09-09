package com.jangletech.qoogol.ui.doubts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DoubtsFragmentBinding;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.favourite.FavQueFragment;
import com.jangletech.qoogol.ui.favourite.FavouriteFragment;
import com.jangletech.qoogol.ui.test.favourite.TestFavouriteFragment;

import java.util.ArrayList;
import java.util.List;

public class DoubtsFragment extends BaseFragment {

    private DoubtsFragmentBinding mBinding;

    public static DoubtsFragment newInstance() {
        return new DoubtsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.doubts_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();

    }

    private void initViews() {
        setupViewPager(mBinding.doubtViewpager);
        mBinding.doubtTabs.setupWithViewPager(mBinding.doubtViewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        DoubtsAdapter adapter = new DoubtsAdapter(getChildFragmentManager());
        adapter.addFragment(new MyDoubtsFragment(), "My Doubts");
        adapter.addFragment(new AllDoubtsFragment(), "All Doubts");
        viewPager.setAdapter(adapter);
    }

    public static class DoubtsAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public DoubtsAdapter(FragmentManager manager) {
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