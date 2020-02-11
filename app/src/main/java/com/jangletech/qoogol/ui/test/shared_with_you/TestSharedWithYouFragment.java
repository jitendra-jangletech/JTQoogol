package com.jangletech.qoogol.ui.test.shared_with_you;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;

public class TestSharedWithYouFragment extends Fragment {

    private TestSharedWithYouViewModel mViewModel;

    public static TestSharedWithYouFragment newInstance() {
        return new TestSharedWithYouFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_shared_with_you, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestSharedWithYouViewModel.class);
        // TODO: Use the ViewModel
    }

}
