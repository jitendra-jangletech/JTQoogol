package com.jangletech.qoogol.ui.learning;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LikeAdapter;
import com.jangletech.qoogol.databinding.LikeDialogBinding;
import com.jangletech.qoogol.databinding.LikeitemBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Like;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.connectonId;

/**
 * Created by Pritali on 6/4/2020.
 */
public class LikeListingDialog  extends Dialog implements LikeAdapter.onItemClickListener {
    LikeDialogBinding likeDialogBinding;
    private Activity context;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    List<Like> likeList;
    private PreferenceManager mSettings;
    String questionId;
    LikeAdapter likeAdapter;
    onItemClickListener onItemClickListener;

    public LikeListingDialog(@NonNull Activity context, String questionId, onItemClickListener onItemClickListener) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.questionId = questionId;
        this.context = context;
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
    }

    private void getData() {
        ProgressDialog.getInstance().show(context);
        Call<ProcessQuestion> call;
            call = apiService.fetchComments(mSettings.getUserId(), questionId, "L");;

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
                        Toast.makeText(context, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                    if (likeDialogBinding.likeSwiperefresh.isRefreshing())
                        likeDialogBinding.likeSwiperefresh.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                    if (likeDialogBinding.likeSwiperefresh.isRefreshing())
                        likeDialogBinding.likeSwiperefresh.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
                if (likeDialogBinding.likeSwiperefresh.isRefreshing())
                    likeDialogBinding.likeSwiperefresh.setRefreshing(false);
            }
        });
    }

    private void initRecycler() {
        likeAdapter = new LikeAdapter(context, likeList, this);
        likeDialogBinding.likeRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        likeDialogBinding.likeRecycler.setLayoutManager(linearLayoutManager);
        likeDialogBinding.likeRecycler.setAdapter(likeAdapter);
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
