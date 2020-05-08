package com.jangletech.qoogol.ui.connections;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.BlockedConnectionAdapter;
import com.jangletech.qoogol.databinding.FragmentBlockedBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.model.ResponseObj;
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

import static com.jangletech.qoogol.util.Constant.block;
import static com.jangletech.qoogol.util.Constant.qoogol;
import static com.jangletech.qoogol.util.Constant.unblock;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlockedFragment extends BaseFragment implements BlockedConnectionAdapter.BlockedItemClick {

    FragmentBlockedBinding mBinding;
    List<Connections> connectionsList = new ArrayList<>();;
    private static final String TAG = "FriendsFragment";
    ApiInterface apiService = ApiClient.getInstance().getApi();
    BlockedConnectionAdapter mAdapter;
    String userId = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_blocked, container, false);
        setHasOptionsMenu(true);
        init();
        return  mBinding.getRoot();
    }

    private void init() {
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        mBinding.blockedSwiperefresh.setOnRefreshListener(() -> getData(0));
    }

    public void checkRefresh() {
        if ( mBinding.blockedSwiperefresh.isRefreshing()) {
            mBinding.blockedSwiperefresh.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData(0);

    }

    private void getData(int pagestart) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ConnectionResponse> call = apiService.fetchConnections(userId,block, getDeviceId(), qoogol,pagestart);
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
                    checkRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                    checkRefresh();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ConnectionResponse> call, Throwable t) {
                t.printStackTrace();
                checkRefresh();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }
    private void initView() {
        mAdapter = new BlockedConnectionAdapter(getActivity(), connectionsList, this);
        mBinding.blockedRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.blockedRecycler.setLayoutManager(linearLayoutManager);
        mBinding.blockedRecycler.setAdapter(mAdapter);
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

    private void updateConnection(String user, String Processcase) {
        ApiInterface apiService = ApiClient.getInstance().getApi();
        ProgressDialog.getInstance().show(getActivity());
        Call<ResponseObj> call = apiService.updateConnections(userId,Processcase, getDeviceId(), qoogol,user);
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        getData(0);
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseObj> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    @Override
    public void unblockUser(String userId) {
        updateConnection(userId, unblock);
    }
}
