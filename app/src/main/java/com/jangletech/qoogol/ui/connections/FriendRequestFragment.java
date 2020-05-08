package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ConnectionAdapter;
import com.jangletech.qoogol.databinding.FragmentFriendRequestBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.Connections;
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

import static com.jangletech.qoogol.util.Constant.friendrequests;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends BaseFragment implements ConnectionAdapter.updateConnectionListener {

    FragmentFriendRequestBinding mBinding;
    List<Connections> connectionsList = new ArrayList<>();;
    private static final String TAG = "FriendsFragment";
    ApiInterface apiService = ApiClient.getInstance().getApi();
    ConnectionAdapter mAdapter;
    Boolean isVisible = false;
    String userId="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_friend_request, container, false);
        setHasOptionsMenu(true);
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        return  mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible=true;
        getData(0);

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
                    // filter recycler view when query submitted
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
            getData(0);
    }

    private void getData(int pagestart) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ConnectionResponse> call = apiService.fetchConnections(userId,friendrequests, getDeviceId(), qoogol,pagestart);
        call.enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, retrofit2.Response<ConnectionResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    connectionsList.clear();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        connectionsList = response.body().getConnection_list();
                        initView();
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ConnectionResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }
    private void initView() {
        mAdapter = new ConnectionAdapter(getActivity(), connectionsList,friendrequests, this);
        mBinding.requestRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.requestRecycler.setLayoutManager(linearLayoutManager);
        mBinding.requestRecycler.setAdapter(mAdapter);


//        if (connectionsList.size()>0) {
//            mBinding.emptyview.setText("You dont't have any pending requests");
//            mBinding.emptyview.setVisibility(View.VISIBLE);
//
//        } else
//            mBinding.emptyview.setVisibility(View.GONE);
    }

    @Override
    public void onUpdateConnection() {
        getData(0);
    }

    @Override
    public void onBottomReached(int size) {
        getData(size);
    }
}
