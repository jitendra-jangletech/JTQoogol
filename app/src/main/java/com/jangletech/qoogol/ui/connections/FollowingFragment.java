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
import com.jangletech.qoogol.adapter.FollowingsAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendsBinding;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.Following;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import static com.jangletech.qoogol.util.Constant.following;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowingFragment extends BaseFragment implements FollowingsAdapter.updateConnectionListener, PublicProfileDialog.PublicProfileClickListener {

    private static final String TAG = "FollowingFragment";
    private FragmentFriendsBinding mBinding;
    private List<Following> connectionsList = new ArrayList<>();
    private FollowingsAdapter mAdapter;
    private Boolean isVisible = false;
    private String userId = "";
    private FollowingViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FollowingViewModel.class);
        init();
        mViewModel.getFollowingList().observe(getViewLifecycleOwner(), followingList -> {
            connectionsList.clear();
            connectionsList.addAll(followingList);
            initView();
            checkRefresh();
        });
    }

    private void init() {
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        if (!isVisible) {
            isVisible = true;
            mViewModel.fetchFollowingsData(false);
        }
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
            mAdapter = new FollowingsAdapter(getActivity(), connectionsList, following, this);
            mBinding.connectionRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
            mBinding.connectionRecycler.setAdapter(mAdapter);
            mBinding.emptyview.setVisibility(View.GONE);
        } else {
            mBinding.emptyview.setText("No Connections Found.");
            mBinding.emptyview.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onUpdateConnection(String user) {
        mViewModel.deleteUpdatedConnection(user);
        mViewModel.fetchFollowingsData(true);
    }

    @Override
    public void onBottomReached(int size) {
        mViewModel.fetchFollowingsData(false);
    }

    @Override
    public void showProfileClick(Bundle bundle) {
        String otherUserId = bundle.getString(Constant.fetch_profile_id);
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), otherUserId, this);
        publicProfileDialog.show();
        //NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }
}
