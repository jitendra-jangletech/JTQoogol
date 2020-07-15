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
import com.jangletech.qoogol.adapter.FollowersAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendsBinding;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.Followers;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

import static com.jangletech.qoogol.util.Constant.followers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends BaseFragment implements FollowersAdapter.updateConnectionListener, PublicProfileDialog.PublicProfileClickListener {

    private static final String TAG = "FollowersFragment";
    private FragmentFriendsBinding mBinding;
    private List<Followers> connectionsList = new ArrayList<>();
    private FollowersAdapter mAdapter;
    private Boolean isVisible = false;
    private String userId = "";
    private FollowersViewModel mViewModel;

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
}
