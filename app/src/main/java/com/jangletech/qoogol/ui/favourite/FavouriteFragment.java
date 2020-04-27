package com.jangletech.qoogol.ui.favourite;

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
import com.jangletech.qoogol.databinding.FavouriteFragmentBinding;
import com.jangletech.qoogol.ui.edit_profile.EditProfileFragment;
import com.jangletech.qoogol.ui.questions.popular_questions.PopularQuestFragment;
import com.jangletech.qoogol.ui.questions.recent_questions.RecentQuestFragment;
import com.jangletech.qoogol.ui.questions.trending_quesions.TrendingQuestFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestFragment;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    private static final String TAG = "FavouriteFragment";
    private FavouriteViewModel mViewModel;
    private FavouriteFragmentBinding mBinding;

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.favourite_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FavouriteViewModel.class);
        setupViewPager(mBinding.viewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new TrendingQuestFragment(), getContext().getString(R.string.menu_questions));
        adapter.addFragment(new MyTestFragment(), getContext().getString(R.string.menu_test_my));
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

}
