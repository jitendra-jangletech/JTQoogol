package com.jangletech.qoogol.ui.test.attended_by_friends;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;

public class TestAttentedByFriendsFragment extends Fragment {

    private TestAttentedByFriendsViewModel mViewModel;

    public static TestAttentedByFriendsFragment newInstance() {
        return new TestAttentedByFriendsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_attented_by_friends, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestAttentedByFriendsViewModel.class);
    }

}
