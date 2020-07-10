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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FriendReqAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendRequestBinding;
import com.jangletech.qoogol.model.FriendRequest;
import com.jangletech.qoogol.model.FriendRequestResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.friendrequests;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends BaseFragment implements FriendReqAdapter.updateConnectionListener {

    private static final String TAG = "FriendRequestFragment";
    private FragmentFriendRequestBinding mBinding;
    //private List<FriendRequest> connectionsList = new ArrayList<>();
    private FriendReqAdapter mAdapter;
    private Boolean isVisible = false;
    private String userId = "";
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
        fetchFriendRequests();
        mViewModel.getFrienReqdList(getUserId()).observe(getViewLifecycleOwner(), friendRequestList -> {
            if (friendRequestList != null) {
                initView(friendRequestList);
            }
        });
    }

    private void fetchFriendRequests() {
        Call<FriendRequestResponse> call = apiService.fetchFriendRequests(getUserId(), friendrequests, getDeviceId(), qoogol, "0");
        call.enqueue(new Callback<FriendRequestResponse>() {
            @Override
            public void onResponse(Call<FriendRequestResponse> call, retrofit2.Response<FriendRequestResponse> response) {
                dismissRefresh(mBinding.requestsSwiperefresh);
                if (response.body() != null &&
                        response.body().getResponse().equalsIgnoreCase("200")) {
                    Log.d(TAG, "onResponse: ");
                    mViewModel.insert(response.body().getFriend_req_list());
                }else{
                    if(response.body()!=null)
                    showToast("Error : "+response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<FriendRequestResponse> call, Throwable t) {
                t.printStackTrace();
                dismissRefresh(mBinding.requestsSwiperefresh);
                showToast("Something went wrong!!");
                apiCallFailureDialog(t);
            }
        });
    }

//    private void init() {
//        //userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
//        if (!isVisible) {
//            isVisible = true;
//            mViewModel.fetchFriendReqData(false);
//        }
//        mBinding.requestsSwiperefresh.setOnRefreshListener(() -> mViewModel.fetchFriendReqData(true));
//    }

//    public void checkRefresh() {
////        if (mBinding.requestsSwiperefresh.isRefreshing()) {
////            mBinding.requestsSwiperefresh.setRefreshing(false);
////        }
////    }

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

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isVisible)
            mViewModel.fetchFriendReqData(false);
    }*/

    private void initView(List<FriendRequest> friendRequests) {
        if (mBinding.requestsSwiperefresh.isRefreshing())
            mBinding.requestsSwiperefresh.setRefreshing(false);
        mAdapter = new FriendReqAdapter(getActivity(), friendRequests, friendrequests, this);
        mBinding.requestRecycler.setHasFixedSize(true);
        mBinding.requestRecycler.setLayoutManager(linearLayoutManager);
        mBinding.requestRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onUpdateConnection() {
        mViewModel.fetchFriendReqData(true);
    }

    @Override
    public void onBottomReached(int size) {
        mViewModel.fetchFriendReqData(false);
    }

    @Override
    public void showProfileClick(Bundle bundle) {
        NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
    }
}
