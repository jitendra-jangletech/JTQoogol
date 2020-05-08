package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ConnectionAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.friends;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends BaseFragment implements ConnectionAdapter.updateConnectionListener {

    FragmentFriendsBinding mBinding;
    List<Connections> connectionsList = new ArrayList<>();;
    private static final String TAG = "FriendsFragment";
    ApiInterface apiService = ApiClient.getInstance().getApi();
    ConnectionAdapter mAdapter;
    Boolean isVisible = false;
    String userId="";
    Call<ConnectionResponse> call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        init();
        return  mBinding.getRoot();
    }

    private void init() {
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        mBinding.connectionSwiperefresh.setOnRefreshListener(() -> getData(0,"refresh"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        getData(0,"init");

    }

    public void checkRefresh() {
        if ( mBinding.connectionSwiperefresh.isRefreshing()) {
            mBinding.connectionSwiperefresh.setRefreshing(false);
        }
    }

    private void getData(int pagestart, String call_from) {
        ProgressDialog.getInstance().show(getActivity());
        if (call_from.equalsIgnoreCase("refresh"))
            call = apiService.fetchConnections(userId,friends, getDeviceId(), qoogol,pagestart);
        else
            call = apiService.fetchRefreshedConnections(userId,friends, getDeviceId(), qoogol,pagestart,forcerefresh);

        call.enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, retrofit2.Response<ConnectionResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    connectionsList.clear();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        connectionsList = response.body().getConnection_list();
                        initView();
                        checkRefresh();
                    } else {
                        checkRefresh();
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    checkRefresh();
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ConnectionResponse> call, Throwable t) {
                t.printStackTrace();
                checkRefresh();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible)
            getData(0,"init");
    }


    private void initView() {
        mAdapter = new ConnectionAdapter(getActivity(), connectionsList,friends, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);

//        if (connectionsList.size()>0) {
//            mBinding.emptyview.setVisibility(View.VISIBLE);
//            mBinding.emptyview.setText("There is no friends in your connection.");
//        } else
//            mBinding.emptyview.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateConnection() {
        getData(0,"refresh");
    }

    @Override
    public void onBottomReached(int size) {
        getData(size,"refresh");
    }
}
