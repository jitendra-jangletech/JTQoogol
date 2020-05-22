package com.jangletech.qoogol.ui.practise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.PracticeMcqBinding;

public class PractiseMtpFragment extends Fragment {

    private static final String TAG = "PractiseMcqFragment";
    private PracticeMcqBinding mBinding;
    private static final String ARG_COUNT = "param1";
    private Integer counter;

    public static PractiseMtpFragment newInstance(Integer counter) {
        PractiseMtpFragment fragment = new PractiseMtpFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.practice_mcq, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {

    }
}
