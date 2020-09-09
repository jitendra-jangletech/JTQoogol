package com.jangletech.qoogol.ui.doubts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.DoubtAdapter;
import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.databinding.DoubtListingBinding;
import com.jangletech.qoogol.dialog.AddDoubtDialog;
import com.jangletech.qoogol.model.DoubtInfo;
import com.jangletech.qoogol.model.DoubtResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.AppUtils.getDeviceId;
import static com.jangletech.qoogol.util.Constant.que_doubts;

/**
 * Created by Pritali on 7/31/2020.
 */
public class DoubtListingDialog extends Dialog implements DoubtAdapter.onItemClick {
    private Activity context;
    private PreferenceManager mSettings;
    DoubtListingBinding doubtListingBinding;
    List<DoubtInfo> doubtsList = new ArrayList<>();
    DoubtAdapter doubtAdapter;
    int call_from;
    String questionId;
    AddDoubtDialog addDoubtDialog;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    AppRepository mAppRepository = new AppRepository(context);
    String subject_id;
    Call<DoubtResponse> call;
    private Boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    LinearLayoutManager linearLayoutManager;
    String pageCount = "0";

    public DoubtListingDialog(@NonNull Activity context, String questionId, String subject_id, int call_from) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.context = context;
        this.call_from = call_from;
        this.questionId = questionId;
        this.subject_id = subject_id;
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

    private void getDataFromApi() {
        doubtListingBinding.doubtSwiperefresh.setRefreshing(true);
        if (call_from == que_doubts)
            call = apiService.fetchDoubtApi(getDeviceId(), new PreferenceManager(context).getUserId(), "L", "Q", questionId, pageCount);
        else
            call = apiService.fetchDoubtApi(getDeviceId(), new PreferenceManager(context).getUserId(), "L", "T", questionId, pageCount);
        call.enqueue(new Callback<DoubtResponse>() {
            @Override
            public void onResponse(Call<DoubtResponse> call, retrofit2.Response<DoubtResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertDoubts(response.body().getDoubtInfoList()));
                        dismissRefresh(doubtListingBinding.doubtSwiperefresh);
                        try {
                            initRecycler(response.body().getDoubtInfoList());
                            pageCount = response.body().getRow();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissRefresh(doubtListingBinding.doubtSwiperefresh);
                }
            }

            @Override
            public void onFailure(Call<DoubtResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(doubtListingBinding.doubtSwiperefresh);
            }
        });
    }

    private void dismissRefresh(SwipeRefreshLayout doubtSwiperefresh) {
        if (doubtSwiperefresh.isRefreshing())
            doubtSwiperefresh.setRefreshing(false);
    }


    private void initView() {
        mSettings = new PreferenceManager(context);
        doubtListingBinding.titletv.setText("Doubts");
        doubtListingBinding.btnCloseDialog.setOnClickListener(v -> dismiss());

        doubtListingBinding.addDoubt.setOnClickListener(v -> {
            if (call_from == que_doubts) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.u_user_id, new PreferenceManager(context).getUserId());
                bundle.putString("SCREEN", "List_GROUPS");
                bundle.putString(Constant.sm_id, subject_id);
                bundle.putString(Constant.appName, "Q");
                bundle.putString(Constant.q_id, questionId);
                bundle.putString(Constant.TorQ, "Q");
                bundle.putString(Constant.device_id, getDeviceId());
                callChatChilliApp(bundle);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.u_user_id, new PreferenceManager(context).getUserId());
                bundle.putString("SCREEN", "List_GROUPS");
                bundle.putString(Constant.sm_id, subject_id);
                bundle.putString(Constant.appName, "Q");
                bundle.putString(Constant.q_id, questionId);
                bundle.putString(Constant.TorQ, "T");
                bundle.putString(Constant.device_id, getDeviceId());
                callChatChilliApp(bundle);
            }

        });
        doubtListingBinding.doubtSwiperefresh.setOnRefreshListener(() -> {
            pageCount = "0";
            dismissRefresh(doubtListingBinding.doubtSwiperefresh);
            getDataFromApi();
        });
        getDataFromApi();

        doubtListingBinding.doubtRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {
                    if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                        isScrolling = false;
                        getDataFromApi();
                    }
                }
            }
        });


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
            Intent LaunchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chatchilli.com"));
            LaunchIntent.putExtras(bundle);
            LaunchIntent.putExtra(Intent.ACTION_VIEW, bundle);
            context.startActivity(LaunchIntent);
        } else {
            Log.i("", "Application is not currently installed.");
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


    private void initRecycler(List<DoubtInfo> doubtInfoList) {
        doubtsList.clear();
        doubtsList.addAll(doubtInfoList);
        if (doubtListingBinding.doubtSwiperefresh.isRefreshing())
            doubtListingBinding.doubtSwiperefresh.setRefreshing(false);

        if (doubtsList != null && doubtsList.size() > 0) {
            doubtAdapter = new DoubtAdapter(context, doubtsList, call_from, this);
            doubtListingBinding.doubtRecycler.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(getContext());
            doubtListingBinding.doubtRecycler.setLayoutManager(linearLayoutManager);
            doubtListingBinding.doubtRecycler.setAdapter(doubtAdapter);
            doubtListingBinding.tvEmptyview.setVisibility(View.GONE);
        } else {
            doubtListingBinding.tvEmptyview.setText("No data available.");
            doubtListingBinding.tvEmptyview.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemClick(String doubt_id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.u_user_id, new PreferenceManager(context).getUserId());
        bundle.putString("SCREEN", "OPEN_POST");
        bundle.putString(Constant.crms_id, doubt_id);
        bundle.putString(Constant.appName, "Q");
        bundle.putString(Constant.device_id, getDeviceId());
        callChatChilliApp(bundle);
    }
}
