package com.jangletech.qoogol.Test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestFragmentBinding;
import com.jangletech.qoogol.ui.learning.course.CourseFragment;

public class TestFragment extends CourseFragment {

    private TestViewModel mViewModel;
    private TestFragmentBinding testFragmentBinding;

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        testFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.test_fragment, container, false);
        return testFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestViewModel.class);

        testFragmentBinding.incorrect.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show());

    }

}
