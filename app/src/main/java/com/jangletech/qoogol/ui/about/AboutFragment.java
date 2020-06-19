package com.jangletech.qoogol.ui.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.jangletech.qoogol.BuildConfig;


import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentAboutBinding;
import com.jangletech.qoogol.ui.BaseFragment;


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

public class AboutFragment extends BaseFragment {

    private FragmentAboutBinding mBinding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false);

        updateUI();

        return mBinding.getRoot();
    }

    private void updateUI() {
        mBinding.tvAppVersion.setText(String.format("v%s", BuildConfig.VERSION_NAME));

        mBinding.tvSupport.setText(Html.fromHtml(getResources().getString(R.string.spotmeets_desc)));
        mBinding.tvSupport.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvSupport.setText(Html.fromHtml(getResources().getString(R.string.support)));
        mBinding.tvSupport.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvlink.setText(Html.fromHtml(getResources().getString(R.string.link)));
        mBinding.tvlink.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvReachOut.setText(Html.fromHtml(getResources().getString(R.string.reachout_text)));
        mBinding.tvReachOut.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvInfoLink.setText(Html.fromHtml(getResources().getString(R.string.more_info_link)));
        mBinding.tvInfoLink.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.tvbtnRateus.setOnClickListener(view -> {
            String playStoreLink = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink)));
            }
        });
    }
}