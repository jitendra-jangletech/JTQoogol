package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ConnectionAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendsBinding;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.connections;
import static com.jangletech.qoogol.util.Constant.friends;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionListFragment extends BaseFragment implements ConnectionAdapter.updateConnectionListener, PublicProfileDialog.PublicProfileClickListener {

    private static final String TAG = "ConnectionListFragment";
    private FragmentFriendsBinding mBinding;
    private List<Connections> connectionsList = new ArrayList<>();
    private ConnectionAdapter mAdapter;
    private Boolean isVisible = false;
    private ConnectionsViewModel mViewModel;
    private LinearLayoutManager linearLayoutManager;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        return mBinding.getRoot();
    }

    /*private void init() {
        if (!isVisible) {
            isVisible = true;
            mViewModel.fetchConnectionsData(false);
        }
        mBinding.connectionSwiperefresh.setOnRefreshListener(() -> mViewModel.fetchConnectionsData(true));
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ConnectionsViewModel.class);
        //init();
        fetchConnections();
        mViewModel.getConnectionsList(getUserId()).observe(getViewLifecycleOwner(), connectionsList -> {
            if (connectionsList != null) {
                setConnectionAdapter(connectionsList);
            }
        });

        mBinding.connectionSwiperefresh.setOnRefreshListener(() -> fetchConnections());
    }

    private void setConnectionAdapter(List<Connections> connectionsList) {
        mAdapter = new ConnectionAdapter(getActivity(), connectionsList, friends, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);
    }

    private void fetchConnections() {
        Call<ConnectionResponse> call = apiService.fetchConnections(getUserId(), connections, getDeviceId(), qoogol, "0");
        call.enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, retrofit2.Response<ConnectionResponse> response) {
                dismissRefresh(mBinding.connectionSwiperefresh);
                if (response.body().getResponse().equalsIgnoreCase("200")) {
                    mViewModel.insert(response.body().getConnection_list());
                } else {
                    showToast("Error Code : " + response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<ConnectionResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(mBinding.connectionSwiperefresh);
                showToast("Something went wrong!!");
                apiCallFailureDialog(t);
            }
        });
    }

    /*public void checkRefresh() {
        if (mBinding.connectionSwiperefresh.isRefreshing()) {
            mBinding.connectionSwiperefresh.setRefreshing(false);
        }
    }*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*if (isVisibleToUser && isVisible)
            mViewModel.fetchConnectionsData(false);*/
    }

    private void initView() {
        mAdapter = new ConnectionAdapter(getActivity(), connectionsList, friends, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateConnection() {
        mViewModel.fetchConnectionsData(true);
    }

    @Override
    public void onBottomReached(int size) {
        mViewModel.fetchConnectionsData(false);
    }

    @Override
    public void showProfileClick(Bundle bundle) {
        String otherUserId = bundle.getString(Constant.fetch_profile_id);
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), otherUserId, this);
        publicProfileDialog.show();
    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }
}
