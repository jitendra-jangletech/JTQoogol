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
import com.jangletech.qoogol.adapter.FollowersAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendsBinding;
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

import static com.jangletech.qoogol.util.Constant.followers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends BaseFragment implements FollowersAdapter.updateConnectionListener {

    FragmentFriendsBinding mBinding;
    List<Followers> connectionsList = new ArrayList<>();
    ;
    private static final String TAG = "FollowersFragment";
    FollowersAdapter mAdapter;
    Boolean isVisible = false;
    String userId = "";
    FollowersViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        return mBinding.getRoot();
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible)
            mViewModel.fetchFollowersData(false);
    }

    public void checkRefresh() {
        if (mBinding.connectionSwiperefresh.isRefreshing()) {
            mBinding.connectionSwiperefresh.setRefreshing(false);
        }
    }

    private void initView() {
        mAdapter = new FollowersAdapter(getActivity(), connectionsList, followers, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateConnection() {
        mViewModel.fetchFollowersData(true);
    }

    @Override
    public void onBottomReached(int size) {
        mViewModel.fetchFollowersData(false);
    }

    @Override
    public void showProfileClick(Bundle bundle) {
        NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
    }
}
