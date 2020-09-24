package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LikeAdapter;
import com.jangletech.qoogol.databinding.LikeDialogBinding;
import com.jangletech.qoogol.model.Like;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Pritali on 6/4/2020.
 */
public class LikeListingDialog extends Dialog implements LikeAdapter.onItemClickListener {
    private static final String TAG = "LikeListingDialog";
    private LikeDialogBinding likeDialogBinding;
    private Activity context;
    private boolean isCallFromTest;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private List<Like> likeList;
    private PreferenceManager mSettings;
    private int questionId;
    private LikeAdapter likeAdapter;
    private Call<ProcessQuestion> call;
    private onItemClickListener onItemClickListener;

    public LikeListingDialog(boolean isCallFromTest, Activity context, int questionId, onItemClickListener onItemClickListener) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.questionId = questionId;
        this.context = context;
        this.isCallFromTest = isCallFromTest;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        likeDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.like_dialog, null, false);
        setContentView(likeDialogBinding.getRoot());
        initView();
        getData();
    }

    private void initView() {
        likeList = new ArrayList<>();
        mSettings = new PreferenceManager(context);
        likeDialogBinding.titletv.setText("Likes");
        likeDialogBinding.btnClose.setOnClickListener(v -> dismiss());
        likeDialogBinding.likeSwiperefresh.setOnRefreshListener(() -> getData());
    }

    private void getData() {
        try {
            if (isCallFromTest)
                call = apiService.fetchTestLikes(Integer.parseInt(mSettings.getUserId()), questionId, "L", 1);
            else
                call = apiService.fetchLikes(Integer.parseInt(mSettings.getUserId()), questionId, "L", 1);

            call.enqueue(new Callback<ProcessQuestion>() {
                @Override
                public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                    try {
                        ProgressDialog.getInstance().dismiss();
                        likeList.clear();
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            likeList = response.body().getLikeList();
                            initRecycler();
                        } else {
                            AppUtils.showToast(context, null,response.body().getMessage());
                        }
                        if (likeDialogBinding.likeSwiperefresh.isRefreshing())
                            likeDialogBinding.likeSwiperefresh.setRefreshing(false);

                        likeDialogBinding.shimmerViewContainer.hideShimmer();
                        likeDialogBinding.likeRecycler.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //ProgressDialog.getInstance().dismiss();
                        likeDialogBinding.shimmerViewContainer.hideShimmer();
                        if (likeDialogBinding.likeSwiperefresh.isRefreshing())
                            likeDialogBinding.likeSwiperefresh.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                    AppUtils.showToast(context, t,"");
                    dismiss();
                    if (likeDialogBinding.likeSwiperefresh.isRefreshing())
                        likeDialogBinding.likeSwiperefresh.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecycler() {
        likeAdapter = new LikeAdapter(context, likeList,isCallFromTest,this);
        likeDialogBinding.likeRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        likeDialogBinding.likeRecycler.setLayoutManager(linearLayoutManager);
        likeDialogBinding.likeRecycler.setAdapter(likeAdapter);
        likeDialogBinding.shimmerViewContainer.hideShimmer();
    }

    @Override
    public void onItemCLick(String user_id) {
        Log.d(TAG, "onItemCLick userId : " + user_id);
        onItemClickListener.onItemCLick(user_id);
        dismiss();
    }


    public interface onItemClickListener {
        void onItemCLick(String user_id);
    }
}
