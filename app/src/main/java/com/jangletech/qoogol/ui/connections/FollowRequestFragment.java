package com.jangletech.qoogol.ui.connections;


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

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FollowReqAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendRequestBinding;
import com.jangletech.qoogol.model.FollowRequest;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import static com.jangletech.qoogol.util.Constant.followrequests;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowRequestFragment extends BaseFragment implements FollowReqAdapter.updateConnectionListener {

    FragmentFriendRequestBinding mBinding;
    List<FollowRequest> connectionsList = new ArrayList<>();
    ;
    private static final String TAG = "FriendsFragment";
    FollowReqAdapter mAdapter;
    Boolean isVisible = false;
    String userId = "";
    FollowReqViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_request, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FollowReqViewModel.class);
        init();
        mViewModel.getFollowReqdList().observe(getViewLifecycleOwner(), followRequestList -> {
            connectionsList.clear();
            connectionsList.addAll(followRequestList);
            initView();
            checkRefresh();
        });
    }

    private void init() {
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        if (!isVisible) {
            isVisible = true;
            mViewModel.fetchFollowReqData(false);
        }
        mBinding.requestsSwiperefresh.setOnRefreshListener(() -> mViewModel.fetchFollowReqData(true));
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

    private void initView() {
        mAdapter = new FollowReqAdapter(getActivity(), connectionsList, followrequests, this);
        mBinding.requestRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.requestRecycler.setLayoutManager(linearLayoutManager);
        mBinding.requestRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateConnection() {
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
