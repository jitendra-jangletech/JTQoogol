package com.jangletech.qoogol.ui.connections;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FollowReqAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendRequestBinding;
import com.jangletech.qoogol.model.FollowRequest;
import com.jangletech.qoogol.model.FollowRequestResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.followrequests;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowRequestFragment extends BaseFragment implements FollowReqAdapter.updateConnectionListener {

    private FragmentFriendRequestBinding mBinding;
    private LinearLayoutManager linearLayoutManager;
    private FollowReqAdapter mAdapter;
    private Boolean isVisible = false;
    private FollowReqViewModel mViewModel;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_request, container, false);
        linearLayoutManager = new LinearLayoutManager(getContext());
        return mBinding.getRoot();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FollowReqViewModel.class);
        init();
        fetchFollowReq();
        mViewModel.getFollowReqdList().observe(getViewLifecycleOwner(), followRequestList -> {
            if (followRequestList != null && followRequestList.size()>0) {
                initView(followRequestList);
                mBinding.emptyview.setVisibility(View.GONE);
            } else {
                mBinding.emptyview.setText("You don not have any pending requests.");
                mBinding.emptyview.setVisibility(View.VISIBLE);
            }
            dismissRefresh(mBinding.requestsSwiperefresh);
        });
    }


    public void fetchFollowReq() {
        Call<FollowRequestResponse> call = apiService.fetchRefreshedFollowReq(getUserId(), followrequests, getDeviceId(), qoogol, "0", forcerefresh);
        call.enqueue(new Callback<FollowRequestResponse>() {
            @Override
            public void onResponse(Call<FollowRequestResponse> call, retrofit2.Response<FollowRequestResponse> response) {
                dismissRefresh(mBinding.requestsSwiperefresh);
                if (response.body() != null &&
                        response.body().getResponse().equalsIgnoreCase("200")) {
                    mViewModel.insert(response.body().getFollowreq_list());
                } else {
                    if (response.body() != null)
                        showToast("Error : " + response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<FollowRequestResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(mBinding.requestsSwiperefresh);
                showToast("Something went wrong!!");
                apiCallFailureDialog(t);
            }
        });
    }

    private void init() {
        if (!isVisible) {
            isVisible = true;
            mViewModel.fetchFollowReqData(false);
        }
        mBinding.requestsSwiperefresh.setOnRefreshListener(() -> {
            mViewModel.fetchFollowReqData(true);
            dismissRefresh(mBinding.requestsSwiperefresh);

        });
    }

    public void checkRefresh() {
        if (mBinding.requestsSwiperefresh.isRefreshing()) {
            mBinding.requestsSwiperefresh.setRefreshing(false);
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
            mViewModel.fetchFollowReqData(false);
    }

    private void initView(List<FollowRequest> followRequests) {
        mAdapter = new FollowReqAdapter(getActivity(), followRequests, followrequests, this);
        mBinding.requestRecycler.setHasFixedSize(true);
        mBinding.requestRecycler.setLayoutManager(linearLayoutManager);
        mBinding.requestRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateConnection(String user) {
        mViewModel.deleteUpdatedConnection(user);
        mViewModel.fetchFollowReqData(true);
    }

    @Override
    public void onBottomReached(int size) {
        mViewModel.fetchFollowReqData(false);
    }

    @Override
    public void showProfileClick(Bundle bundle) {
        NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
    }
}
