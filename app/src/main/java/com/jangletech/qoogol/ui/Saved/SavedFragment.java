package com.jangletech.qoogol.ui.Saved;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.SavedFragmentBinding;
import com.jangletech.qoogol.ui.favourite.FavouriteFragment;
import com.jangletech.qoogol.ui.learning.LearningFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestFragment;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    private SavedViewModel mViewModel;
    private SavedFragmentBinding mBinding;

    public static SavedFragment newInstance() {
        return new SavedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.saved_fragment, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        setupViewPager(mBinding.savedViewpager);
        mBinding.savedtabs.setupWithViewPager(mBinding.savedViewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        FavouriteFragment.Adapter adapter = new FavouriteFragment.Adapter(getChildFragmentManager());
        adapter.addFragment(new LearningFragment(), "Questions");
        adapter.addFragment(new MyTestFragment(),"Test");
        viewPager.setAdapter(adapter);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SavedViewModel.class);
    }
}
