package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ShareAdapter;
import com.jangletech.qoogol.databinding.DialogShareQuestionBinding;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.model.ShareResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.friends_and_groups;
import static com.jangletech.qoogol.util.Constant.qoogol;
import static com.jangletech.qoogol.util.Constant.question_share;
import static com.jangletech.qoogol.util.Constant.test_share;

public class ShareQuestionDialog extends Dialog implements ShareAdapter.OnItemClickListener {

    private static final String TAG = "ShareQuestionDialog";
    private Activity mContext;
    private DialogShareQuestionBinding mBinding;
    private List<ShareModel> selectedconnectionsList;
    private ShareAdapter mAdapter;
    private List<ShareModel> connectionsList;
    private Call<ResponseObj> call;
    private String testId = "";
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public ShareQuestionDialog(@NonNull Activity mContext) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_share_question, null, false);
        setContentView(mBinding.getRoot());
        connectionsList = new ArrayList<>();
        selectedconnectionsList = new ArrayList<>();
        getData(0);

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.tvShare.setOnClickListener(v -> {
            callShareAPI();
        });
    }

    private void getData(int pagestart) {
        ProgressDialog.getInstance().show(mContext);
        Call<ShareResponse> call = apiService.fetchConnectionsforShare(AppUtils.getUserId(), friends_and_groups, AppUtils.getDeviceId(), qoogol, pagestart);
        call.enqueue(new Callback<ShareResponse>() {
            @Override
            public void onResponse(Call<ShareResponse> call, retrofit2.Response<ShareResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    connectionsList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        connectionsList = response.body().getConnection_list();
                        initRecycler();
                    } else {
                        AppUtils.showToast(mContext, response.body().getResponse());
                        //Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ShareResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void callShareAPI() {

        String modelAction = TextUtils.join(",", selectedconnectionsList).replace(",,", ",");
        modelAction = modelAction.replace("D", "U");
        modelAction = modelAction.replace("A", "U");
        String comment = AppUtils.encodedString(mBinding.shareComment.getText().toString().trim());

        //logs
        Log.d(TAG, "callShareAPI Test Share : " + test_share);
        Log.d(TAG, "callShareAPI Question Share : " + question_share);
        Log.d(TAG, "callShareAPI Test Id : " + testId);
        Log.d(TAG, "callShareAPI Comment : " + comment);
        Log.d(TAG, "callShareAPI Model Action : " + modelAction);
        Log.d(TAG, "callShareAPI Device Id : " + AppUtils.getDeviceId());
        Log.d(TAG, "callShareAPI UserId : " + AppUtils.getUserId());

        if (modelAction != null && modelAction.isEmpty()) {
            AppUtils.showToast(mContext, "Please, select atleast 1 connection or group to share with.");
        } else {
            ProgressDialog.getInstance().show(mContext);
            call = apiService.shareAPI(testId, test_share, "F", AppUtils.getDeviceId(), AppUtils.getUserId(), modelAction, "1.68", qoogol, comment);

            call.enqueue(new Callback<ResponseObj>() {
                @Override
                public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                    try {
                        ProgressDialog.getInstance().dismiss();
                        connectionsList.clear();
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            Log.i(TAG, "shared successfully");
                            dismiss();
                        } else {
                            AppUtils.showToast(mContext, response.body().getResponse());
                            //Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("call_from", "share");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseObj> call, Throwable t) {
                    t.printStackTrace();
                    AppUtils.showToast(mContext, "Something went wrong!!");
                    ProgressDialog.getInstance().dismiss();
                }
            });
        }
    }

    private void initRecycler() {
        if (connectionsList != null) {
            mAdapter = new ShareAdapter(mContext, connectionsList, this);
            mBinding.shareRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mBinding.shareRecycler.setLayoutManager(linearLayoutManager);
            mBinding.shareRecycler.setAdapter(mAdapter);
        }
    }

    @Override
    public void actionPerformed(ShareModel connections, int position) {
        if (selectedconnectionsList.contains(connections))
            selectedconnectionsList.remove(connections);
        else
            selectedconnectionsList.add(connections);
    }

    @Override
    public void onBottomReached(int position) {

    }
}
