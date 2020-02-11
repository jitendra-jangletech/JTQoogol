package com.jangletech.qoogol.ui.test.my_test;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;

public class MyTestFragment extends Fragment {

    private com.jangletech.qoogol.ui.test.my_test.MyTestViewModel mViewModel;

    public static MyTestFragment newInstance() {
        return new MyTestFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_my, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(com.jangletech.qoogol.ui.test.my_test.MyTestViewModel.class);
        // TODO: Use the ViewModel
    }

}
