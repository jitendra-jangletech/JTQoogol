package com.jangletech.qoogol.activities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jangletech.qoogol.Test.TestFragment;

public class ViewPagerAdapterNew extends FragmentStateAdapter {

    private static final String TAG = "ViewPagerAdapterNew";
    private static final int CARD_ITEM_SIZE = 8;
    public ViewPagerAdapterNew(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull @Override public Fragment createFragment(int position) {
        Log.d(TAG, "createFragment: ");
        return TestFragment.newInstance(position);
    }
    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }

}
