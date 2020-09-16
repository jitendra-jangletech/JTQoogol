package com.jangletech.qoogol.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FriendsAdapter;
import com.jangletech.qoogol.databinding.ActivityTestingBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Friends;
import com.jangletech.qoogol.model.FriendsResponse;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.connections.FriendsViewModel;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.friends;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class TestingActivity extends AppCompatActivity {

    private static final String TAG = "TestingActivity";
    private ActivityTestingBinding mBinding;
    private String encoded = "";
    private LinearLayoutManager layoutManager;
    private FriendsAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<Friends> connectionsList = new ArrayList<>();
    private List<Friends> filteredList = new ArrayList<>();
    private List<String> strings;
    private List<ShareModel> shareModelList;
    private Boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    private int pageCount = 0;
    private boolean isSearching = false;
    private List<ShareModel> filteredModelList;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private FriendsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_testing);
        mViewModel = ViewModelProviders.of(this).get(FriendsViewModel.class);
    }
}

        /*linearLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FriendsAdapter(this, connectionsList, friends, this);
        mBinding.connectionRecycler.setHasFixedSize(true);
        mBinding.connectionRecycler.setLayoutManager(linearLayoutManager);
        mBinding.connectionRecycler.setAdapter(mAdapter);*/
       /* mViewModel.fetchFriendsData(false);

        mViewModel.getFriendList().observe(this, friendsList -> {
            if (friendsList != null) {
                mAdapter.updateList(friendsList);
                //setFriendsList(friendsList);
            }
        });
    }*/

    /*private void setFriendsList(List<Friends> friendsList) {
        if (friendsList.size() > 0) {
            mBinding.emptyview.setVisibility(View.GONE);
            mAdapter.updateList(friendsList);
        } else {
            mBinding.emptyview.setText("No Friends Added.");
            mBinding.emptyview.setVisibility(View.VISIBLE);
        }
    }*/

    /*@Override
    public void onUpdateConnection(String user) {

    }

    @Override
    public void showProfileClick(Bundle bundle) {

    }*/