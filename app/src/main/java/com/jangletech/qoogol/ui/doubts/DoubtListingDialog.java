package com.jangletech.qoogol.ui.doubts;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.DoubtAdapter;
import com.jangletech.qoogol.databinding.DoubtListingBinding;
import com.jangletech.qoogol.dialog.AddDoubtDialog;
import com.jangletech.qoogol.model.Doubts;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import static com.jangletech.qoogol.util.Constant.tq_doubts;

/**
 * Created by Pritali on 7/31/2020.
 */
public class DoubtListingDialog extends Dialog {
    private Activity context;
    private PreferenceManager mSettings;
    DoubtListingBinding doubtListingBinding;
    List<Doubts> doubtsList = new ArrayList<>();
    DoubtAdapter doubtAdapter;
    int call_from;
    String questionId;
    AddDoubtDialog addDoubtDialog;



    public DoubtListingDialog(@NonNull Activity context, String questionId, int call_from) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.context = context;
        this.call_from = call_from;
        this.questionId = questionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        doubtListingBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.doubt_listing, null, false);
        setContentView(doubtListingBinding.getRoot());
        initView();
    }

    private void initView() {
        mSettings = new PreferenceManager(context);
        doubtListingBinding.titletv.setText("Doubts");
        doubtListingBinding.btnCloseDialog.setOnClickListener(v -> dismiss());

        doubtListingBinding.addDoubt.setOnClickListener(v -> {
            if (call_from==tq_doubts)
                 addDoubtDialog = new AddDoubtDialog(context,questionId);
            else
                 addDoubtDialog = new AddDoubtDialog(context,null);
            addDoubtDialog.show();
        });
        doubtListingBinding.doubtSwiperefresh.setOnRefreshListener(() -> getData());
        getData();
    }



    private void getData() {
        doubtsList.clear();

        Doubts doubts = new Doubts();
        doubts.setFirst_name("John");
        doubts.setLast_name("Miller");
        doubts.setProfile("000000/00000010650001.png");
        doubts.setPosted_date("1 hour ago");
        doubts.setPosted_group("Chemistry");
        doubts.setDoubt("Doubt text will be here");
        doubts.setDoubt_link("Doubt link will be here");
        doubtsList.add(doubts);

        Doubts doubts1 = new Doubts();
        doubts1.setFirst_name("Angel");
        doubts1.setLast_name("Moses");
        doubts1.setProfile("000000/00000010650001.png");
        doubts1.setPosted_date("1 hour ago");
        doubts1.setPosted_group("Chemistry");
        doubts1.setDoubt("Doubt text will be here");
        doubts1.setDoubt_link("Doubt link will be here");
        doubtsList.add(doubts1);


        Doubts doubts2 = new Doubts();
        doubts2.setFirst_name("Tania");
        doubts2.setLast_name("Melvin");
        doubts2.setProfile("000000/00000010650001.png");
        doubts2.setPosted_date("1 hour ago");
        doubts2.setPosted_group("Chemistry");
        doubts2.setDoubt("Doubt text will be here");
        doubts2.setDoubt_link("Doubt link will be here");
        doubtsList.add(doubts2);

        Doubts doubts3 = new Doubts();
        doubts3.setFirst_name("Dennis");
        doubts3.setLast_name("Swint");
        doubts3.setProfile("000000/00000010650001.png");
        doubts3.setPosted_date("1 hour ago");
        doubts3.setPosted_group("Chemistry");
        doubts3.setDoubt("Doubt text will be here");
        doubts3.setDoubt_link("Doubt link will be here");
        doubtsList.add(doubts3);

        Doubts doubts4 = new Doubts();
        doubts4.setFirst_name("Erwin");
        doubts4.setLast_name("Delton");
        doubts4.setProfile("000000/00000010650001.png");
        doubts4.setPosted_date("1 hour ago");
        doubts4.setPosted_group("Chemistry");
        doubts4.setDoubt("Doubt text will be here");
        doubts4.setDoubt_link("Doubt link will be here");
        doubtsList.add(doubts4);

        initRecycler();
    }

    private void initRecycler() {
        if (doubtListingBinding.doubtSwiperefresh.isRefreshing())
            doubtListingBinding.doubtSwiperefresh.setRefreshing(false);

        if (doubtsList.size() > 0) {
            doubtAdapter = new DoubtAdapter(context, doubtsList, call_from);
            doubtListingBinding.doubtRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            doubtListingBinding.doubtRecycler.setLayoutManager(linearLayoutManager);
            doubtListingBinding.doubtRecycler.setAdapter(doubtAdapter);
            doubtListingBinding.tvEmptyview.setVisibility(View.GONE);
        } else {
            doubtListingBinding.tvEmptyview.setText("No data available.");
            doubtListingBinding.tvEmptyview.setVisibility(View.VISIBLE);
        }
    }
}
