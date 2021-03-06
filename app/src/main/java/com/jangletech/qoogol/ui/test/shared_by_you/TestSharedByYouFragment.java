package com.jangletech.qoogol.ui.test.shared_by_you;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;

public class TestSharedByYouFragment extends Fragment {

    private TestSharedByYouViewModel mViewModel;

    public static TestSharedByYouFragment newInstance() {
        return new TestSharedByYouFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_shared_by_you, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestSharedByYouViewModel.class);
        // TODO: Use the ViewModel
    }

}
