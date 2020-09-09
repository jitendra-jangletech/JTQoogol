package com.jangletech.qoogol.ui.doubts;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.DoubtAdapter;
import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.databinding.DoubtListBinding;
import com.jangletech.qoogol.dialog.AddDoubtDialog;
import com.jangletech.qoogol.model.DoubtInfo;
import com.jangletech.qoogol.model.DoubtResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.AppUtils.getDeviceId;
import static com.jangletech.qoogol.util.Constant.my_doubts;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyDoubtsFragment extends Fragment implements DoubtAdapter.onItemClick {

    DoubtListBinding mBinding;
    private boolean isFragmentVisible = false;
    private PreferenceManager mSettings;
    AddDoubtDialog addDoubtDialog;
    String pageCount = "0";

    private ApiInterface apiService = ApiClient.getInstance().getApi();
    AppRepository mAppRepository = new AppRepository(getActivity());
    String subject_id;
    Call<DoubtResponse> call;
    private Boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    LinearLayoutManager linearLayoutManager;
    DoubtAdapter doubtAdapter;


    public MyDoubtsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.doubt_list, container, false);
        return mBinding.getRoot();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFragmentVisible) {
            getDataFromApi();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        if (!isFragmentVisible) {
            isFragmentVisible = true;
            getDataFromApi();
        }
    }

    private void dismissRefresh(SwipeRefreshLayout doubtSwiperefresh) {
        if (doubtSwiperefresh.isRefreshing())
            doubtSwiperefresh.setRefreshing(false);
    }

    private void initView() {

        mSettings = new PreferenceManager(getActivity());

        mBinding.addDoubt.setOnClickListener(v -> {
                addDoubtDialog = new AddDoubtDialog(getActivity(), null);
                addDoubtDialog.show();

        });
        mBinding.doubtSwiperefresh.setOnRefreshListener(() -> {
            pageCount = "0";
            dismissRefresh(mBinding.doubtSwiperefresh);
            getDataFromApi();
        });
        getDataFromApi();

        mBinding.doubtRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void initRecycler(List<DoubtInfo> doubtInfoList) {
        if (mBinding.doubtSwiperefresh.isRefreshing())
            mBinding.doubtSwiperefresh.setRefreshing(false);

        if (doubtInfoList != null && doubtInfoList.size() > 0) {
            doubtAdapter = new DoubtAdapter(getActivity(), doubtInfoList, my_doubts, this);
            mBinding.doubtRecycler.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(getContext());
            mBinding.doubtRecycler.setLayoutManager(linearLayoutManager);
            mBinding.doubtRecycler.setAdapter(doubtAdapter);
            mBinding.tvEmptyview.setVisibility(View.GONE);
        } else {
            mBinding.tvEmptyview.setText("No data available.");
            mBinding.tvEmptyview.setVisibility(View.VISIBLE);
        }
    }



    private void getDataFromApi() {
        mBinding.doubtSwiperefresh.setRefreshing(true);
        call = apiService.fetchDoubtApi(getDeviceId(), new PreferenceManager(getActivity()).getUserId(), "S", pageCount);
        call.enqueue(new Callback<DoubtResponse>() {
            @Override
            public void onResponse(Call<DoubtResponse> call, retrofit2.Response<DoubtResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertDoubts(response.body().getDoubtInfoList()));
                        dismissRefresh(mBinding.doubtSwiperefresh);
                        try {
                            initRecycler(response.body().getDoubtInfoList());
                            pageCount = response.body().getRow();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissRefresh(mBinding.doubtSwiperefresh);
                }
            }

            @Override
            public void onFailure(Call<DoubtResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(mBinding.doubtSwiperefresh);
            }
        });
    }


    public boolean isAppInstalled() {
        PackageManager pm = getActivity().getPackageManager();
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
            startActivity(LaunchIntent);
        } else {
            Log.i("", "Application is not currently installed.");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Alert")
                    .setMessage("Chatchilli App is not installed on this device.\n " +
                            "Please install app to explore more things.")
                    .setPositiveButton("Install", (dialog, which) -> {
                        String appPackageName = "com.jangletech.chatchilli"; //
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    @Override
    public void onItemClick(String doubt_id) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.u_user_id, new PreferenceManager(getActivity()).getUserId());
        bundle.putString("SCREEN", "OPEN_POST");
        bundle.putString(Constant.crms_id, doubt_id);
        bundle.putString(Constant.appName, Constant.qoogol);
        bundle.putString(Constant.device_id, getDeviceId());
        callChatChilliApp(bundle);

    }
}
