package com.jangletech.qoogol.activities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jangletech.qoogol.Test.TestFragment;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.ui.TestQuestionFragment;

import java.util.List;

public class ViewPagerAdapterNew extends FragmentStateAdapter {

    private static final String TAG = "ViewPagerAdapterNew";
    private static final int CARD_ITEM_SIZE = 8;
    private List<TestQuestionNew> testQuestionNewList;

    public ViewPagerAdapterNew(@NonNull FragmentActivity fragmentActivity, List<TestQuestionNew> testQuestionNewList) {
        super(fragmentActivity);
        this.testQuestionNewList = testQuestionNewList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "createFragment: ");
        return TestQuestionFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return testQuestionNewList.size();
    }
}
