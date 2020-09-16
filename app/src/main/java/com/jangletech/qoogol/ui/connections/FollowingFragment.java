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
import com.jangletech.qoogol.adapter.FollowingsAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendsBinding;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.Following;
import com.jangletech.qoogol.model.FollowingResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.following;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFragment extends BaseFragment implements FollowingsAdapter.updateConnectionListener, PublicProfileDialog.PublicProfileClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "FollowingFragment";
    private FragmentFriendsBinding mBinding;
    private List<Following> connectionsList = new ArrayList<>();
    private List<Following> filteredList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private FollowingsAdapter mAdapter;
    private Boolean isVisible = false;
    private String pageCount = "0";
    private int currentItems, scrolledOutItems, totalItems;
    private Boolean isScrolling = false;
    private String userId = "";
    private FollowingViewModel mViewModel;
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
        mViewModel = ViewModelProviders.of(this).get(FollowingViewModel.class);
        init();
        mViewModel.getFollowingList().observe(getViewLifecycleOwner(), followingList -> {
            if (followingList != null) {
                connectionsList = followingList;
                initView();
                checkRefresh();
            }
        });
    }

    private void init() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new FollowingsAdapter(getActivity(), connectionsList, following, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);
        //userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        if (!isVisible) {
            isVisible = true;
            mViewModel.fetchFollowingsData(false);
        }
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
                        //mViewModel.fetchFollowingsData(false);
                    }
                }
            }
        });
        mBinding.connectionSwiperefresh.setOnRefreshListener(() -> mViewModel.fetchFollowingsData(true));
    }

    public void checkRefresh() {
        if (mBinding.connectionSwiperefresh.isRefreshing()) {
            mBinding.connectionSwiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible)
            mViewModel.fetchFollowingsData(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }

    private void initView() {
        if (connectionsList.size() > 0) {
            mAdapter.updateList(connectionsList);
            mBinding.emptyview.setVisibility(View.GONE);
        } else {
            mBinding.emptyview.setText("No Followings Found.");
            mBinding.emptyview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.trim().toLowerCase().isEmpty()) {
            mBinding.tvEmptySearch.setVisibility(View.GONE);
            mBinding.connectionRecycler.setVisibility(View.VISIBLE);
            mAdapter.updateList(connectionsList);
        } else {
            filteredList.clear();
            for (Following connections : connectionsList) {
                if (connections.getU_first_name().toLowerCase().contains(newText.trim().toLowerCase()) ||
                        connections.getU_last_name().toLowerCase().contains(newText.trim().toLowerCase())) {
                    filteredList.add(connections);
                }
            }
            if (filteredList.size() > 0) {
                mBinding.tvEmptySearch.setVisibility(View.GONE);
                mBinding.connectionRecycler.setVisibility(View.VISIBLE);
                mAdapter.updateList(filteredList);
            } else {
                //search from server
                searchFromServer(newText.trim().toLowerCase());
            }
        }
        return true;
    }

    private void searchFromServer(String text) {
        Call<FollowingResponse> call = apiService.searchFollowing(
                getUserId(getActivity()),
                following,
                getDeviceId(getActivity()),
                qoogol,
                text,
                pageCount);
        call.enqueue(new Callback<FollowingResponse>() {
            @Override
            public void onResponse(Call<FollowingResponse> call, retrofit2.Response<FollowingResponse> response) {
                dismissRefresh(mBinding.connectionSwiperefresh);
                if (response.body().getResponse().equalsIgnoreCase("200")) {
                    setSearchData(response.body());
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    showToast("Error Code : " + response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<FollowingResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(mBinding.connectionSwiperefresh);
                showToast("Something went wrong!!");
                apiCallFailureDialog(t);
            }
        });
    }

    private void setSearchData(FollowingResponse response) {
        if (response != null && response.getFollowing_list().size() > 0) {
            filteredList.clear();
            List<Following> followingList = new ArrayList<>();
            for (Following following : response.getFollowing_list()) {
                following.setU_first_name(getDecryptedField(following.getU_first_name(), Constant.cf_key1));
                following.setU_last_name(getDecryptedField(following.getU_last_name(), Constant.cf_key2));
                followingList.add(following);
            }
            mBinding.tvEmptySearch.setVisibility(View.GONE);
            mBinding.connectionRecycler.setVisibility(View.VISIBLE);
            mAdapter.updateList(followingList);
        } else {
            //no search results found
            mBinding.tvEmptySearch.setVisibility(View.VISIBLE);
            mBinding.connectionRecycler.setVisibility(View.GONE);
        }
    }


    @Override
    public void onUpdateConnection(String user) {
        mViewModel.deleteUpdatedConnection(user);
        mViewModel.fetchFollowingsData(true);
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
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

}
