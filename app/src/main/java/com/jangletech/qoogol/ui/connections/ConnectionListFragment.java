package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private String pageCount = "0";
    private int currentItems, scrolledOutItems, totalItems;
    private Boolean isScrolling = false;
    private ConnectionResponse connectionResponse;
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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ConnectionsViewModel.class);
        mBinding.emptyview.setVisibility(View.GONE);
        fetchConnections(pageCount);
        mAdapter = new ConnectionAdapter(getActivity(), connectionsList, friends, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);
        mViewModel.getConnectionsList(getUserId(getActivity())).observe(getViewLifecycleOwner(), connections -> {
            if (connections != null) {
                if (connectionResponse != null)
                    pageCount = connectionResponse.getRow_count();
                if (connections.size() > 0) {
                    connectionsList.addAll(connections);
                    mAdapter.updateList(connectionsList);
                } else {
                    mBinding.emptyview.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.connectionRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        fetchConnections(pageCount);
                    }
                }
            }
        });

        mBinding.connectionSwiperefresh.setOnRefreshListener(() -> fetchConnections("0"));
    }


    private void fetchConnections(String pageCount) {
        Call<ConnectionResponse> call = apiService.fetchConnections(getUserId(getActivity()), connections, getDeviceId(getActivity()), qoogol, pageCount);
        call.enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, retrofit2.Response<ConnectionResponse> response) {
                dismissRefresh(mBinding.connectionSwiperefresh);
                if (response.body().getResponse().equalsIgnoreCase("200")) {
                    connectionResponse = response.body();
                    mViewModel.insert(response.body().getConnection_list());
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
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

//    private void initView() {
//        mAdapter = new ConnectionAdapter(getActivity(), connectionsList, friends, this);
//        mBinding.connectionRecycler.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
//        mBinding.connectionRecycler.setAdapter(mAdapter);
//    }

    @Override
    public void onUpdateConnection(String user) {
        mViewModel.deleteUpdatedConnection(user);
        mViewModel.fetchConnectionsData(true);
    }

    @Override
    public void onBottomReached(int size) {

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
