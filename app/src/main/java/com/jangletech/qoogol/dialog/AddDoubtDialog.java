package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.DoubtGroupAdapter;
import com.jangletech.qoogol.databinding.AddDoubtBinding;
import com.jangletech.qoogol.model.DoubtGroups;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pritali on 7/27/2020.
 */
public class AddDoubtDialog  extends Dialog {

    AddDoubtBinding addDoubtBinding;
    private Activity context;
    private PreferenceManager mSettings;
    String questionId;
    List<DoubtGroups> doubtGroupsList = new ArrayList<>();
    DoubtGroupAdapter doubtGroupAdapter;

    public AddDoubtDialog(@NonNull Activity context, String questionId) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.context = context;
        this.questionId = questionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        addDoubtBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.add_doubt, null, false);
        setContentView(addDoubtBinding.getRoot());
        initView();
    }


    private void initView() {
        mSettings = new PreferenceManager(context);
        addDoubtBinding.titletv.setText("Groups");
        addDoubtBinding.btnCloseDialog.setOnClickListener(v -> dismiss());

        addDoubtBinding.doubtgroupSwiperefresh.setOnRefreshListener(() -> getData());
        getData();
    }

    private void getData() {
        doubtGroupsList.clear();

        DoubtGroups doubtGroups = new DoubtGroups();
        doubtGroups.setDoubt_group("Physics");
        doubtGroups.setProfile("000000/00000010650001.png");
        doubtGroups.setIs_joined("true");
        doubtGroupsList.add(doubtGroups);

        DoubtGroups doubtGroups1 = new DoubtGroups();
        doubtGroups1.setDoubt_group("Mathematics");
        doubtGroups1.setProfile("000000/00000010650001.png");
        doubtGroups1.setIs_joined("false");
        doubtGroupsList.add(doubtGroups1);

        DoubtGroups doubtGroups3 = new DoubtGroups();
        doubtGroups3.setDoubt_group("Chemistry");
        doubtGroups3.setProfile("000000/00000010650001.png");
        doubtGroups3.setIs_joined("false");
        doubtGroupsList.add(doubtGroups3);

        DoubtGroups doubtGroups4 = new DoubtGroups();
        doubtGroups4.setDoubt_group("Physics");
        doubtGroups4.setProfile("000000/00000010650001.png");
        doubtGroups4.setIs_joined("true");
        doubtGroupsList.add(doubtGroups4);

        initRecycler();
    }

    private void initRecycler() {
        if (addDoubtBinding.doubtgroupSwiperefresh.isRefreshing())
            addDoubtBinding.doubtgroupSwiperefresh.setRefreshing(false);

        if (doubtGroupsList.size() > 0) {
            doubtGroupAdapter = new DoubtGroupAdapter(context, doubtGroupsList);
            addDoubtBinding.doubtgroupRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            addDoubtBinding.doubtgroupRecycler.setLayoutManager(linearLayoutManager);
            addDoubtBinding.doubtgroupRecycler.setAdapter(doubtGroupAdapter);
            addDoubtBinding.tvEmptyview.setVisibility(View.GONE);
        } else {
            addDoubtBinding.tvEmptyview.setText("No data available.");
            addDoubtBinding.tvEmptyview.setVisibility(View.VISIBLE);
        }
    }
}
