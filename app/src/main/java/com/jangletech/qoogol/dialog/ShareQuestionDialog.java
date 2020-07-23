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
import com.jangletech.qoogol.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.connection_list;
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
    private List<ShareModel> shareModelList;
    private Call<ResponseObj> call;
    private String actionId;
    private int pageCount = 0;
    LinearLayoutManager linearLayoutManager;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public ShareQuestionDialog(@NonNull Activity mContext, String actionId) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.actionId = actionId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_share_question, null, false);
        setContentView(mBinding.getRoot());
        shareModelList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getContext());
        selectedconnectionsList = new ArrayList<>();
        getData(0);

        mBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //mAdapter.filterList(filterContacts(query.toLowerCase().trim()));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.shareButton.setOnClickListener(v -> {
            callShareAPI();
        });

        mBinding.shareRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int ydy = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLastVisible()) {
                    Log.i(TAG, "Calling next part of data. ");
                    getData(pageCount);
                }
            }
        });
    }

    private boolean isLastVisible() {
        int pos = linearLayoutManager.findLastVisibleItemPosition();
        int numItems = mBinding.shareRecycler.getAdapter().getItemCount();
        if (pos < 1) {
            return false;
        }
        return (pos >= (numItems - 2));
    }

  /*  private List<ShareModel> filterContacts(String query) {
        List<ShareModel> filteredList = new ArrayList<>();
        for (ShareModel shareModel : shareModelList) {
            if (query.contains(shareModel.getU_first_name().toLowerCase().trim()) ||
                    query.contains(shareModel.getU_last_name().toLowerCase().trim())) {
                filteredList.add(shareModel);
            }
            Log.d(TAG, "filterContacts First Name : " + shareModel.getU_first_name().toLowerCase());
            Log.d(TAG, "filterContacts Last Name : " + shareModel.getU_last_name().toLowerCase());
        }
        Log.d(TAG, "filterContacts Size : " + filteredList.size());
        return filteredList;
        // mAdapter.filterList(filteredList);
    }*/

    private void getData(int pagestart) {
        //ProgressDialog.getInstance().show(mContext);
        Log.d(TAG, "getData Called: ");
        Call<ShareResponse> call = apiService.fetchConnectionsforShare(AppUtils.getUserId(),
                friends_and_groups, AppUtils.getDeviceId(), qoogol, pagestart);
        call.enqueue(new Callback<ShareResponse>() {
            @Override
            public void onResponse(Call<ShareResponse> call, retrofit2.Response<ShareResponse> response) {
                try {
                    //ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        if (pageCount == 0) {
                            Log.d(TAG, "onResponse If PageCount : " + pageCount);
                            pageCount = response.body().getRow_count();
                            initRecycler(response.body().getConnection_list());
                        } else {
                            //shareModelList = response.body().getConnection_list();
                            pageCount = response.body().getRow_count();
                            Log.d(TAG, "onResponse Else PageCount : " + pageCount);
                            if (response.body().getConnection_list() != null) {
                                shareModelList.addAll(response.body().getConnection_list());
                                Log.d(TAG, "onResponse: "+shareModelList.size());
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        AppUtils.showToast(mContext, response.body().getResponse());
                        //Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ShareResponse> call, Throwable t) {
                t.printStackTrace();
                //ProgressDialog.getInstance().dismiss();
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
        Log.d(TAG, "callShareAPI Test Id : " + actionId);
        Log.d(TAG, "callShareAPI Comment : " + comment);
        Log.d(TAG, "callShareAPI Model Action : " + modelAction);
        Log.d(TAG, "callShareAPI Device Id : " + AppUtils.getDeviceId());
        Log.d(TAG, "callShareAPI UserId : " + AppUtils.getUserId());

        if (modelAction != null && modelAction.isEmpty()) {
            AppUtils.showToast(mContext, "Please, select atleast 1 connection or group to share with.");
        } else {
            ProgressDialog.getInstance().show(mContext);
            call = apiService.shareAPI(actionId, test_share, "F", AppUtils.getDeviceId(), AppUtils.getUserId(), modelAction, "1.68", qoogol, comment);

            call.enqueue(new Callback<ResponseObj>() {
                @Override
                public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                    try {
                        ProgressDialog.getInstance().dismiss();
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

    private void initRecycler(List<ShareModel> shareModelList) {
        mAdapter = new ShareAdapter(mContext, shareModelList, this);
        mBinding.shareRecycler.setHasFixedSize(true);
        mBinding.shareRecycler.setLayoutManager(linearLayoutManager);
        mBinding.shareRecycler.setAdapter(mAdapter);
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
