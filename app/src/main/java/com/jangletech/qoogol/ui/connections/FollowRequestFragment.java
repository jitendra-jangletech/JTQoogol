package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ConnectionAdapter;
import com.jangletech.qoogol.adapter.FollowReqAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendRequestBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.model.FollowRequest;
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

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.following;
import static com.jangletech.qoogol.util.Constant.followrequests;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.friendrequests;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowRequestFragment extends BaseFragment implements FollowReqAdapter.updateConnectionListener{

    FragmentFriendRequestBinding mBinding;
    List<FollowRequest> connectionsList = new ArrayList<>();;
    private static final String TAG = "FriendsFragment";
    ApiInterface apiService = ApiClient.getInstance().getApi();
    FollowReqAdapter mAdapter;
    Boolean isVisible = false;
    String userId="";
    Call<ConnectionResponse> call;
    FollowReqViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_request, container, false);
        return  mBinding.getRoot();
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
        if ( mBinding.requestsSwiperefresh.isRefreshing()) {
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
        NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile,bundle);
    }
}
