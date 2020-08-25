package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ShareAdapter;
import com.jangletech.qoogol.databinding.DialogShareQuestionBinding;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.model.ShareResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.friends_and_groups;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class ShareQuestionDialog extends Dialog implements ShareAdapter.OnItemClickListener {

    private static final String TAG = "ShareQuestionDialog";
    private Activity mContext;
    private DialogShareQuestionBinding mBinding;
    private List<ShareModel> selectedconnectionsList;
    private ShareAdapter mAdapter;
    private List<ShareModel> shareModelList;
    private List<ShareModel> filteredModelList;
    private Call<ResponseObj> call;
    private String actionId;
    private String userId;
    private String deviceId;
    private int pageCount = 0;
    private String tOrQ = "";
    private boolean isSearching = false;
    private Boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    private LinearLayoutManager linearLayoutManager;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public ShareQuestionDialog(@NonNull Activity mContext, String actionId,
                               String userId, String deviceId, String tOrQ) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.actionId = actionId;
        this.userId = userId;
        this.deviceId = deviceId;
        this.tOrQ = tOrQ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_share_question, null, false);
        setContentView(mBinding.getRoot());
        shareModelList = new ArrayList<>();
        filteredModelList = new ArrayList<>();
        getData(pageCount, "");
        linearLayoutManager = new LinearLayoutManager(getContext());
        selectedconnectionsList = new ArrayList<>();
        mAdapter = new ShareAdapter(mContext, shareModelList, this);
        mBinding.shareRecycler.setLayoutManager(linearLayoutManager);
        mBinding.shareRecycler.setAdapter(mAdapter);

        mBinding.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.searchView.setIconified(false);
            }
        });

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                filteredModelList.clear();
                query = query.toLowerCase().trim();
                for (ShareModel shareModel : shareModelList) {
                    if (shareModel.getRecordType().equalsIgnoreCase("U")) {
                        if (shareModel.getU_first_name().trim().toLowerCase().contains(query) ||
                                shareModel.getU_last_name().trim().toLowerCase().contains(query)) {
                            filteredModelList.add(shareModel);
                        }
                    } else {
                        if (shareModel.getU_first_name().trim().toLowerCase().contains(query)) {
                            filteredModelList.add(shareModel);
                        }
                    }
                }
                if (filteredModelList.size() > 0) {
                    mAdapter.updateList(filteredModelList);
                } else {
                    //mAdapter.updateList(shareModelList);
                    if (!query.isEmpty() && !isSearching) {
                        isSearching = true;
                        getData(0, query);
                    } else {
                        mAdapter.updateList(shareModelList);
                    }
                }
                return true;
            }
        });

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.shareButton.setOnClickListener(v -> {
            callShareAPI();
        });

        mBinding.shareRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        getData(pageCount, "");
                    }
                }
            }
        });
    }

    private void getData(int pagestart, String text) {
        //ProgressDialog.getInstance().show(getActivity());
        mBinding.progress.setVisibility(View.VISIBLE);
        Call<ShareResponse> call = apiService.fetchConnectionsforShare(userId, friends_and_groups,
                deviceId, qoogol, pagestart, text);
        call.enqueue(new Callback<ShareResponse>() {
            @Override
            public void onResponse(Call<ShareResponse> call, retrofit2.Response<ShareResponse> response) {
                try {
                    mBinding.progress.setVisibility(View.GONE);
                    if (response != null && response.body() != null) {
                        if (response.body().getResponse().equalsIgnoreCase("200")) {
                            setData(response.body());
                        } else {
                            AppUtils.showToast(mContext, " Error : " + response.body().getResponse());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isSearching = false;
                    mBinding.progress.setVisibility(View.GONE);
                    AppUtils.showToast(mContext, "Something went wrong!!");
                }
            }

            @Override
            public void onFailure(Call<ShareResponse> call, Throwable t) {
                t.printStackTrace();
                isSearching = false;
                mBinding.progress.setVisibility(View.GONE);
                //ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void setData(ShareResponse shareResponse) {
        Log.d(TAG, "setData PageStart : " + pageCount);
        Log.d(TAG, "setData List Size : " + shareResponse.getConnection_list().size());
        if (shareResponse != null) {
            List<ShareModel> sharingList = new ArrayList<>();
            if (shareResponse.getConnection_list().size() > 0) {
                for (ShareModel shareModel : shareResponse.getConnection_list()) {
                    //Log.d(TAG, "setData First Name : " + AESSecurities.getInstance().decrypt(TinyDB.getInstance(mContext).getString(Constant.cf_key1), shareModel.getU_first_name()));
                    shareModel.setU_first_name(AESSecurities.getInstance().decrypt(TinyDB.getInstance(mContext).getString(Constant.cf_key1), shareModel.getU_first_name()));
                    shareModel.setU_last_name(AESSecurities.getInstance().decrypt(TinyDB.getInstance(mContext).getString(Constant.cf_key2), shareModel.getU_last_name()));
                    sharingList.add(shareModel);
                }
            }
            if (!isSearching) {
                if (sharingList.size() > 0) {
                    pageCount = shareResponse.getRow_count();
                    shareModelList.addAll(sharingList);
                    mAdapter.updateList(shareModelList);
                } else {
                    AppUtils.showToast(mContext, "No more connections available.");
                }
            } else {
                isSearching = false;
                filteredModelList.clear();
                filteredModelList.addAll(sharingList);
                if (filteredModelList.size() > 0) {
                    mAdapter.updateList(filteredModelList);
                } else {
                    AppUtils.showToast(mContext, "No search results found.");
                    mAdapter.updateList(shareModelList);
                }
            }
        }
    }

    private void callShareAPI() {
        String modelAction = TextUtils.join(",", selectedconnectionsList).replace(",,", ",");
        modelAction = modelAction.replace("D", "U");
        modelAction = modelAction.replace("A", "U");
        String comment = AppUtils.encodedString(mBinding.shareComment.getText().toString().trim());

        Log.d(TAG, "callShareAPI Test Id : " + actionId);
        Log.d(TAG, "callShareAPI Comment : " + comment);
        Log.d(TAG, "callShareAPI Model Action : " + modelAction);
        Log.d(TAG, "callShareAPI Device Id : " + deviceId);
        Log.d(TAG, "callShareAPI UserId : " + userId);

        if (modelAction != null && modelAction.isEmpty()) {
            AppUtils.showToast(mContext, "Please, select atleast 1 connection or group to share with.");
        } else {
            ProgressDialog.getInstance().show(mContext);
            call = apiService.shareAPI(actionId, tOrQ, "F", deviceId, userId,
                    modelAction, Constant.APP_VERSION, qoogol, comment);
            call.enqueue(new Callback<ResponseObj>() {
                @Override
                public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                    ProgressDialog.getInstance().dismiss();
                    dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        Log.i(TAG, "shared successfully");
                    } else {
                        AppUtils.showToast(mContext, UtilHelper.getAPIError(String.valueOf(response.body())));
                    }
                }

                @Override
                public void onFailure(Call<ResponseObj> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                    dismiss();
                }
            });
        }
    }

    @Override
    public void actionPerformed(ShareModel connections, int position) {
        Log.d(TAG, "actionPerformed: ");
        if (selectedconnectionsList.contains(connections))
            selectedconnectionsList.remove(connections);
        else
            selectedconnectionsList.add(connections);
    }

    @Override
    public void onBottomReached(int position) {

    }
}
