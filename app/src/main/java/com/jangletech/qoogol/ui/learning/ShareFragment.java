package com.jangletech.qoogol.ui.learning;


import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ShareAdapter;
import com.jangletech.qoogol.databinding.FragmentShareBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.model.ShareResponse;
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

import static com.jangletech.qoogol.util.Constant.friends_and_groups;
import static com.jangletech.qoogol.util.Constant.learning;
import static com.jangletech.qoogol.util.Constant.qoogol;
import static com.jangletech.qoogol.util.Constant.question_share;
import static com.jangletech.qoogol.util.Constant.test;
import static com.jangletech.qoogol.util.Constant.test_share;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends BaseFragment implements ShareAdapter.OnItemClickListener {


    FragmentShareBinding shareBinding;
    ShareAdapter mAdapter;
    List<ShareModel> connectionsList;
    List<ShareModel> selectedconnectionsList;
    private static final String TAG = "ShareFragment";
    ApiInterface apiService = ApiClient.getInstance().getApi();
    String userId = "", question_id = "", testId = "";
    int call_from;
    Call<ResponseObj> call;


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
        Call<ShareResponse> call = apiService.fetchConnectionsforShare(userId, friends_and_groups, getDeviceId(), qoogol, pagestart);
        call.enqueue(new Callback<ShareResponse>() {
            @Override
            public void onResponse(Call<ShareResponse> call, retrofit2.Response<ShareResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    connectionsList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        connectionsList = response.body().getConnection_list();
                        initRecycler();
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ShareResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void initView() {
        connectionsList = new ArrayList<>();
        selectedconnectionsList = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getInt("call_from") == learning) {
                question_id = bundle.getString("QuestionId");
            } else {
                call_from = test;
                testId = bundle.getString("testId");
            }
        }
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
    }

    private void initRecycler() {
        mAdapter = new ShareAdapter(getActivity(), connectionsList, this);
        shareBinding.shareRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        shareBinding.shareRecycler.setLayoutManager(linearLayoutManager);
        shareBinding.shareRecycler.setAdapter(mAdapter);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forward_action_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final MenuItem saveItem = menu.findItem(R.id.action_save);

        saveItem.setOnMenuItemClickListener(item -> {
            callShareAPI();
            return true;
        });

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
    public void actionPerformed(ShareModel connections, int position) {
        if (selectedconnectionsList.contains(connections))
            selectedconnectionsList.remove(connections);
        else
            selectedconnectionsList.add(connections);
    }

    @Override
    public void onBottomReached(int position) {
    }

    private void callShareAPI() {
        String modelAction = TextUtils.join(",", selectedconnectionsList).replace(",,", ",");
        modelAction = modelAction.replace("D", "U");
        modelAction = modelAction.replace("A", "U");
        String comment = shareBinding.shareComment.getText().toString();

        ProgressDialog.getInstance().show(getActivity());

        if (call_from == learning)
            call = apiService.shareAPI(question_id, question_share, "F", getDeviceId(), userId, modelAction, "1.68", qoogol, comment);
        else
            call = apiService.shareAPI(testId, test_share, "F", getDeviceId(), userId, modelAction, "1.68", qoogol, comment);

        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    connectionsList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        Log.i(TAG, "shared successfully");

                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("call_from", "share");
                    if (call_from == learning)
                        NavHostFragment.findNavController(ShareFragment.this).navigate(R.id.nav_learning, bundle);
                    else
                        NavHostFragment.findNavController(ShareFragment.this).navigate(R.id.nav_test_my, bundle);
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


}
