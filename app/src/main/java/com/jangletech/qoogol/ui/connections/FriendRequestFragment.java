package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FriendReqAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendRequestBinding;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.FriendRequest;
import com.jangletech.qoogol.model.FriendRequestResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.friendrequests;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends BaseFragment implements FriendReqAdapter.updateConnectionListener, PublicProfileDialog.PublicProfileClickListener {

    private static final String TAG = "FriendRequestFragment";
    private FragmentFriendRequestBinding mBinding;
    private FriendReqAdapter mAdapter;
    private Boolean isVisible = false;
    private FriendReqViewModel mViewModel;
    private LinearLayoutManager linearLayoutManager;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_request, container, false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        setHasOptionsMenu(true);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FriendReqViewModel.class);
        //init();
        fetchFriendRequests();
        mViewModel.getFrienReqdList().observe(getViewLifecycleOwner(), friendRequestList -> {
            if (friendRequestList != null) {
                List<FriendRequest> list = new ArrayList<>();
                for (FriendRequest friendRequest : friendRequestList) {
                    String fName = AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), friendRequest.getU_first_name());
                    String lName = AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key2), friendRequest.getU_last_name());
                    friendRequest.setU_first_name(fName);
                    friendRequest.setU_last_name(lName);
                    list.add(friendRequest);
                }
                initView(list);

                if(list.size() > 0){
                    mBinding.emptyview.setVisibility(View.GONE);
                }else{
                    mBinding.emptyview.setText("No Requests.");
                    mBinding.emptyview.setVisibility(View.VISIBLE);
                }
            }
            dismissRefresh(mBinding.requestsSwiperefresh);
        });

        mBinding.requestsSwiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFriendRequests();
            }
        });
    }

    private void fetchFriendRequests() {
        mBinding.emptyview.setText("Fetching Requests...");
        Call<FriendRequestResponse> call = apiService.fetchFriendRequests(getUserId(getActivity()), friendrequests, getDeviceId(getActivity()), qoogol, "0");
        call.enqueue(new Callback<FriendRequestResponse>() {
            @Override
            public void onResponse(Call<FriendRequestResponse> call, retrofit2.Response<FriendRequestResponse> response) {
                dismissRefresh(mBinding.requestsSwiperefresh);
                if (response.body() != null &&
                        response.body().getResponse().equalsIgnoreCase("200")) {
                    if(response.body().getFriend_req_list().size() == 0){
                        mBinding.emptyview.setText("No Requests.");
                    }
                    mViewModel.insert(response.body().getFriend_req_list());
                } else {
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<FriendRequestResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(mBinding.requestsSwiperefresh);
                apiCallFailureDialog(t);
            }
        });
    }

    /*private void init() {
        if (!isVisible) {
            isVisible = true;
            mViewModel.fetchFriendReqData(false);
        }
        mBinding.requestsSwiperefresh.setOnRefreshListener(() -> mViewModel.fetchFriendReqData(true));
    }

    public void checkRefresh() {
        if (mBinding.requestsSwiperefresh.isRefreshing()) {
            mBinding.requestsSwiperefresh.setRefreshing(false);
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forward_action_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_save).setVisible(false);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    mAdapter.getFilter().filter(query);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                try {
                    // filter recycler view when text is changed
                    mAdapter.getFilter().filter(query);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
                return false;
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible)
            mViewModel.fetchFriendReqData(false);
    }

    private void initView(List<FriendRequest> friendRequests) {
        if (mBinding.requestsSwiperefresh.isRefreshing())
            mBinding.requestsSwiperefresh.setRefreshing(false);
        mAdapter = new FriendReqAdapter(getActivity(), friendRequests, friendrequests, this);
        mBinding.requestRecycler.setHasFixedSize(true);
        mBinding.requestRecycler.setLayoutManager(linearLayoutManager);
        mBinding.requestRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateConnection(String user) {
        mViewModel.deleteUpdatedConnection(user);
        mViewModel.fetchFriendReqData(true);
    }

    @Override
    public void onBottomReached(int size) {
        mViewModel.fetchFriendReqData(false);
    }

    @Override
    public void showProfileClick(Bundle bundle) {
        String userId = bundle.getString(Constant.fetch_profile_id);
        Log.d(TAG, "showProfileClick User Id : " + userId);
        PublicProfileDialog dialog = new PublicProfileDialog(getActivity(), userId, this);
        dialog.show();
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
}
