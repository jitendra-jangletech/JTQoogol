package com.jangletech.qoogol.ui.connections;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FollowersAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendsBinding;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.Followers;
import com.jangletech.qoogol.model.FollowersResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.followers;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends BaseFragment implements FollowersAdapter.updateConnectionListener, PublicProfileDialog.PublicProfileClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "FollowersFragment";
    private FragmentFriendsBinding mBinding;
    private List<Followers> connectionsList = new ArrayList<>();
    private List<Followers> filteredList = new ArrayList<>();
    private FollowersAdapter mAdapter;
    private Boolean isVisible = false;
    private String userId = "";
    private String pageCount = "0";
    private FollowersViewModel mViewModel;
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

    private void init() {
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        if (!isVisible) {
            isVisible = true;
            mViewModel.fetchFollowersData(false);
        }
        mBinding.connectionSwiperefresh.setOnRefreshListener(() -> mViewModel.fetchFollowersData(true));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FollowersViewModel.class);
        init();

        mViewModel.getFollowersList().observe(getViewLifecycleOwner(), followersList -> {
            connectionsList.clear();
            connectionsList.addAll(followersList);
            initView();
            checkRefresh();
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible)
            mViewModel.fetchFollowersData(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }

    public void checkRefresh() {
        if (mBinding.connectionSwiperefresh.isRefreshing()) {
            mBinding.connectionSwiperefresh.setRefreshing(false);
        }
    }

    private void initView() {
        if(connectionsList.size() > 0) {
            mAdapter = new FollowersAdapter(getActivity(), connectionsList, followers, this);
            mBinding.connectionRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
            mBinding.connectionRecycler.setAdapter(mAdapter);
            mBinding.emptyview.setVisibility(View.GONE);
        }else{
            mBinding.emptyview.setText("No Followers Yet.");
            mBinding.emptyview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUpdateConnection(String user) {
        mViewModel.deleteUpdatedConnection(user);
        mViewModel.fetchFollowersData(true);
    }

    @Override
    public void onBottomReached(int size) {
        mViewModel.fetchFollowersData(false);
    }

    @Override
    public void showProfileClick(Bundle bundle) {
        String otherUserId = bundle.getString(Constant.fetch_profile_id);
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(),otherUserId,this);
        publicProfileDialog.show();
    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.trim().toLowerCase().isEmpty()) {
            mAdapter.updateList(connectionsList);
        } else {
            filteredList.clear();
            for (Followers connections : connectionsList) {
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
        Call<FollowersResponse> call = apiService.searchFollowers(
                getUserId(getActivity()),
                followers,
                getDeviceId(getActivity()),
                qoogol,
                text,
                pageCount);
        call.enqueue(new Callback<FollowersResponse>() {
            @Override
            public void onResponse(Call<FollowersResponse> call, retrofit2.Response<FollowersResponse> response) {
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
            public void onFailure(Call<FollowersResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(mBinding.connectionSwiperefresh);
                showToast("Something went wrong!!");
                apiCallFailureDialog(t);
            }
        });
    }

    private void setSearchData(FollowersResponse response) {
        if (response != null && response.getFollowers_list().size() > 0) {
            filteredList.clear();
            mBinding.tvEmptySearch.setVisibility(View.GONE);
            mBinding.connectionRecycler.setVisibility(View.VISIBLE);
            mAdapter.updateList(response.getFollowers_list());
        } else {
            //no search results found
            mBinding.tvEmptySearch.setVisibility(View.VISIBLE);
            mBinding.connectionRecycler.setVisibility(View.GONE);
            //showToast("No Search Results Found.");
        }
    }
}
