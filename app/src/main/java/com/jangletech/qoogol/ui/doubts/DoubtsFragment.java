package com.jangletech.qoogol.ui.doubts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DoubtsFragmentBinding;
import com.jangletech.qoogol.ui.BaseFragment;

public class DoubtsFragment extends BaseFragment {

    private DoubtsViewModel mViewModel;
    private DoubtsFragmentBinding mBinding;

    public static DoubtsFragment newInstance() {
        return new DoubtsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.doubts_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DoubtsViewModel.class);

    }

}