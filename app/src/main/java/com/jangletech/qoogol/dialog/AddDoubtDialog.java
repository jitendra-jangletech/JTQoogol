package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.DoubtGroupAdapter;
import com.jangletech.qoogol.databinding.AddDoubtBinding;
import com.jangletech.qoogol.model.DoubtGroups;
import com.jangletech.qoogol.model.SubjectClass;
import com.jangletech.qoogol.model.SubjectResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.AppUtils.getDeviceId;

/**
 * Created by Pritali on 7/27/2020.
 */
public class AddDoubtDialog  extends Dialog implements DoubtGroupAdapter.onItemCliclListener {

    AddDoubtBinding addDoubtBinding;
    private Activity context;
    private PreferenceManager mSettings;
    String questionId;
    List<SubjectClass> subjectClassList = new ArrayList<>();
    DoubtGroupAdapter doubtGroupAdapter;
    private static final String TAG = "AddDoubtDialog";
    private ApiInterface apiService = ApiClient.getInstance().getApi();

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
        addDoubtBinding.titletv.setText("Select Subject");
        addDoubtBinding.btnCloseDialog.setOnClickListener(v -> dismiss());
        addDoubtBinding.doubtgroupSwiperefresh.setOnRefreshListener(() -> getData());
        getData();
    }


    public boolean isAppInstalled() {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.jangletech.chatchilli", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    private void callChatChilliApp(Bundle bundle) {
        if (isAppInstalled()) {
            //This intent will help you to launch if the package is already installed
            Intent LaunchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chatchilli.com"));
            LaunchIntent.putExtras(bundle);
            LaunchIntent.putExtra(Intent.ACTION_VIEW, bundle);
            context.startActivity(LaunchIntent);
        } else {
            Log.i(TAG, "Application is not currently installed.");
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
            builder.setTitle("Alert")
                    .setMessage("Chatchilli App is not installed on this device.\n " +
                            "Please install app to explore more things.")
                    .setPositiveButton("Install", (dialog, which) -> {
                        String appPackageName = "com.jangletech.chatchilli"; //
                        try {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }


    private void getData() {
        subjectClassList.clear();

        Call<SubjectResponse> call = apiService.fetchSubjectList(
                new PreferenceManager(context).getUserId(),
                getDeviceId(),
                "Q",
                "L");
        call.enqueue(new Callback<SubjectResponse>() {
            @Override
            public void onResponse(Call<SubjectResponse> call, retrofit2.Response<SubjectResponse> response) {
                dismissRefresh(addDoubtBinding.doubtgroupSwiperefresh);
                if (response.body().getResponse().equalsIgnoreCase("200")) {
                    subjectClassList = response.body().getSubjectList();
                    initRecycler();
                }
            }

            @Override
            public void onFailure(Call<SubjectResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(addDoubtBinding.doubtgroupSwiperefresh);
            }
        });

    }

    private void dismissRefresh(SwipeRefreshLayout doubtgroupSwiperefresh) {
        if (doubtgroupSwiperefresh.isRefreshing())
            doubtgroupSwiperefresh.setRefreshing(false);
    }

    private void initRecycler() {
        if (addDoubtBinding.doubtgroupSwiperefresh.isRefreshing())
            addDoubtBinding.doubtgroupSwiperefresh.setRefreshing(false);
        if (subjectClassList.size() > 0) {
            doubtGroupAdapter = new DoubtGroupAdapter(context, subjectClassList,this);
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



    @Override
    public void onItemClick(String sub_id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.u_user_id, new PreferenceManager(context).getUserId());
        bundle.putString("SCREEN", "List_GROUPS");
        bundle.putString(Constant.sm_id,sub_id);
        bundle.putString(Constant.appName,"Q");
        bundle.putString(Constant.q_id,questionId);
        bundle.putString(Constant.TorQ,"Q");
        callChatChilliApp(bundle);
    }
}
