package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
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
public class ConnectionListFragment extends BaseFragment implements ConnectionAdapter.updateConnectionListener, PublicProfileDialog.PublicProfileClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "ConnectionListFragment";
    private FragmentFriendsBinding mBinding;
    private List<Connections> connectionsList = new ArrayList<>();
    private List<Connections> filteredList = new ArrayList<>();
    private ConnectionAdapter mAdapter;
    private Boolean isVisible = false;
    private String pageCount = "0";
    private LinearLayoutManager linearLayoutManager;
    private int currentItems, scrolledOutItems, totalItems;
    private Boolean isScrolling = false;
    private ConnectionResponse connectionResponse;
    private ConnectionsViewModel mViewModel;

    private ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "Android Id : " + getDeviceId(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_conn_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ConnectionsViewModel.class);
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
                    mBinding.emptyview.setVisibility(View.GONE);
                    connectionsList.clear();
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
    public void onFriendUnFriendClick() {

    }

    @Override
    public void onFollowUnfollowClick() {

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.trim().toLowerCase().isEmpty()) {
            Log.d(TAG, "onQueryTextChange Length : " + newText.trim().length());
            mBinding.tvEmptySearch.setVisibility(View.GONE);
            mBinding.connectionRecycler.setVisibility(View.VISIBLE);
            mAdapter.updateList(connectionsList);
        } else {
            filteredList.clear();
            mBinding.tvEmptySearch.setVisibility(View.GONE);
            mBinding.connectionRecycler.setVisibility(View.VISIBLE);
            for (Connections connections : connectionsList) {
                if (connections.getU_first_name().toLowerCase().contains(newText.trim().toLowerCase()) ||
                        connections.getU_last_name().toLowerCase().contains(newText.trim().toLowerCase())) {
                    filteredList.add(connections);
                }
            }
            if (filteredList.size() > 0) {
                mAdapter.updateList(filteredList);
            } else {
                //search from server
                searchFromServer(newText.trim().toLowerCase());
            }
        }
        return true;
    }

    private void searchFromServer(String text) {
        Call<ConnectionResponse> call = apiService.searchConnections(
                getUserId(getActivity()),
                connections,
                getDeviceId(getActivity()),
                qoogol,
                text,
                pageCount
        );
        call.enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, retrofit2.Response<ConnectionResponse> response) {
                dismissRefresh(mBinding.connectionSwiperefresh);
                if (response.body().getResponse().equalsIgnoreCase("200")) {
                    //connectionResponse = response.body();
                    //mViewModel.insert(response.body().getConnection_list());
                    setSearchData(connectionResponse);
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

    private void setSearchData(ConnectionResponse connectionResponse) {
        if (connectionResponse != null && connectionResponse.getConnection_list().size() > 0) {
            filteredList.clear();
            mBinding.tvEmptySearch.setVisibility(View.GONE);
            mBinding.connectionRecycler.setVisibility(View.VISIBLE);
            mAdapter.updateList(connectionResponse.getConnection_list());
        } else {
            //no search results found
            //showToast("No Search Results Found.");
            mBinding.tvEmptySearch.setVisibility(View.VISIBLE);
            mBinding.connectionRecycler.setVisibility(View.GONE);
        }
    }
}
