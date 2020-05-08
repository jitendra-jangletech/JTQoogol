package com.jangletech.qoogol.ui.learning;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearingAdapter;
import com.jangletech.qoogol.adapter.ShareAdapter;
import com.jangletech.qoogol.databinding.FragmentShareBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.friends;
import static com.jangletech.qoogol.util.Constant.qoogol;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends BaseFragment {


    FragmentShareBinding shareBinding;
    ShareAdapter mAdapter;
    List<Connections> connectionsList;
    private static final String TAG = "ShareFragment";
    ApiInterface apiService = ApiClient.getInstance().getApi();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        shareBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_share, container, false);
        setHasOptionsMenu(true);
        initView();
        getData(0);
        return shareBinding.getRoot();
    }


    private void getData(int pagestart) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ConnectionResponse> call = apiService.fetchConnections("1069",friends,getDeviceId(), qoogol,pagestart);
        call.enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, retrofit2.Response<ConnectionResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    connectionsList.clear();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        connectionsList = response.body().getConnection_list();
                        mAdapter.updateList(connectionsList);
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
        connectionsList = new ArrayList<>();
        mAdapter = new ShareAdapter(getActivity(), connectionsList);
        shareBinding.shareRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        shareBinding.shareRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        shareBinding.shareRecycler.setLayoutManager(linearLayoutManager);
        shareBinding.shareRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forward_action_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

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

}
