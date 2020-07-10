package com.jangletech.qoogol.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentCodeConductBinding;


/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class CodeConductFragment extends Fragment {

    private FragmentCodeConductBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_code_conduct, container, false);

        mBinding.tvConduct.setText(Html.fromHtml(getResources().getString(R.string.code_conduct_head)));
        mBinding.tvConduct.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvConductDesc.setText(Html.fromHtml(getResources().getString(R.string.code_conduct_desc)));
        mBinding.tvConductDesc.setMovementMethod(LinkMovementMethod.getInstance());

        return mBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

}
