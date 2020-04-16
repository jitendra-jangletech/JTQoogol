package com.jangletech.qoogol.ui.questions.questions;

import android.graphics.Color;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentQuestionsBinding;
import com.jangletech.qoogol.ui.edit_profile.EditProfileFragment;
import com.jangletech.qoogol.ui.questions.popular_questions.PopularQuestFragment;
import com.jangletech.qoogol.ui.questions.recent_questions.RecentQuestFragment;
import com.jangletech.qoogol.ui.questions.trending_quesions.TrendingQuestFragment;

import java.util.ArrayList;
import java.util.List;

public class QuestionsFragment extends Fragment {

    private QuestionsViewModel mViewModel;
    private FragmentQuestionsBinding mBinding;

    public static QuestionsFragment newInstance() {
        return new QuestionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_questions, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(QuestionsViewModel.class);

        //set pie chart
        mBinding.piechart.setUsePercentValues(true);
        mBinding.piechart.getDescription().setEnabled(false);
        mBinding.piechart.setExtraOffsets(5,10,5,5);
        mBinding.piechart.setDragDecelerationFrictionCoef(0.95f);

        mBinding.piechart.setDrawHoleEnabled(true);
        mBinding.piechart.setHoleColor(Color.WHITE);
        mBinding.piechart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(30,"Trending"));
        yValues.add(new PieEntry(40,"Popular"));
        yValues.add(new PieEntry(30,"Recent"));

        mBinding.piechart.animateY(1000,Easing.EaseInOutCubic);

        PieDataSet pieDataSet = new PieDataSet(yValues,"Questions");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.YELLOW);

        mBinding.piechart.setData(pieData);

        setupViewPager(mBinding.viewpager);
        mBinding.resultTabs.setupWithViewPager(mBinding.viewpager);
    }

    private void setupViewPager(ViewPager viewPager){
        EditProfileFragment.Adapter adapter = new EditProfileFragment.Adapter(getChildFragmentManager());
        adapter.addFragment(new TrendingQuestFragment(), getContext().getString(R.string.menu_quest_trending));
        adapter.addFragment(new PopularQuestFragment(), getContext().getString(R.string.menu_quest_popular));
        adapter.addFragment(new RecentQuestFragment(), getString(R.string.menu_quest_recent));
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
