package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LikeAdapter;
import com.jangletech.qoogol.adapter.ShareUserAdapter;
import com.jangletech.qoogol.databinding.LikeDialogBinding;
import com.jangletech.qoogol.databinding.ShareuserDialogBinding;
import com.jangletech.qoogol.model.Like;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.ShareUserResponse;
import com.jangletech.qoogol.model.SharedQuestions;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.AppUtils.getDeviceId;
import static com.jangletech.qoogol.util.Constant.sharedby;

/**
 * Created by Pritali on 6/4/2020.
 */
public class ShareUserListingDialog extends Dialog implements ShareUserAdapter.onItemClickListener {
    ShareuserDialogBinding shareuserDialogBinding;
    private Activity context;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    List<SharedQuestions> sharedQuestions;
    private PreferenceManager mSettings;
    int questionId;
    ShareUserAdapter shareUserAdapter;
    onItemClickListener onItemClickListener;
    int call_from;

    public ShareUserListingDialog(@NonNull Activity context, int questionId, onItemClickListener onItemClickListener,int call_from) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.questionId = questionId;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.call_from = call_from;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareuserDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.shareuser_dialog, null, false);
        setContentView(shareuserDialogBinding.getRoot());
        initView();
        getData();
    }

    private void initView() {
        sharedQuestions = new ArrayList<>();
        mSettings = new PreferenceManager(context);
        shareuserDialogBinding.titletv.setText("Users");
        shareuserDialogBinding.btnClose.setOnClickListener(v -> dismiss());
        shareuserDialogBinding.shareuserSwiperefresh.setOnRefreshListener(() -> getData());
    }

    private void getData() {
        //ProgressDialog.getInstance().show(context);
        Call<ShareUserResponse> call;
        if (call_from==sharedby)
            call = apiService.fetchSharedByUsers(mSettings.getUserId(), questionId, getDeviceId(),mSettings.getUserId());
        else
            call = apiService.fetchSharedToUsers(mSettings.getUserId(), questionId, getDeviceId(),mSettings.getUserId());

        call.enqueue(new Callback<ShareUserResponse>() {
            @Override
            public void onResponse(Call<ShareUserResponse> call, retrofit2.Response<ShareUserResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    sharedQuestions.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        sharedQuestions = response.body().getUserList();
                        initRecycler();
                    } else {
                        Toast.makeText(context, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                    if (shareuserDialogBinding.shareuserSwiperefresh.isRefreshing())
                        shareuserDialogBinding.shareuserSwiperefresh.setRefreshing(false);

                    shareuserDialogBinding.shimmerViewContainer.hideShimmer();
                    shareuserDialogBinding.shareuserSwiperefresh.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    //ProgressDialog.getInstance().dismiss();
                    shareuserDialogBinding.shimmerViewContainer.hideShimmer();
                    if (shareuserDialogBinding.shareuserSwiperefresh.isRefreshing())
                        shareuserDialogBinding.shareuserSwiperefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ShareUserResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
                if (shareuserDialogBinding.shareuserSwiperefresh.isRefreshing())
                    shareuserDialogBinding.shareuserSwiperefresh.setRefreshing(false);
            }
        });
    }

    private void initRecycler() {
        shareUserAdapter = new ShareUserAdapter(context, sharedQuestions, this,call_from);
        shareuserDialogBinding.shareuserRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        shareuserDialogBinding.shareuserRecycler.setLayoutManager(linearLayoutManager);
        shareuserDialogBinding.shareuserRecycler.setAdapter(shareUserAdapter);
        shareuserDialogBinding.shimmerViewContainer.hideShimmer();
    }

    @Override
    public void onItemCLick(String user_id) {
        onItemClickListener.onItemCLick(user_id);
        dismiss();
    }


    public interface onItemClickListener {
        void onItemCLick(String user_id);
    }
}
