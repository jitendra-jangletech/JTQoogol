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
import com.jangletech.qoogol.util.AppUtils;
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
public class ShareFragment extends BaseFragment implements ShareAdapter.OnItemClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "ShareFragment";
    private FragmentShareBinding shareBinding;
    private ShareAdapter mAdapter;
    private List<ShareModel> connectionsList;
    private List<ShareModel> selectedconnectionsList;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private String userId = "", question_id = "", testId = "";
    private int call_from;
    private Call<ResponseObj> call;

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
                question_id = String.valueOf(bundle.getInt("QuestionId"));
            } else {
                call_from = test;
                testId = bundle.getString("testId");
            }
        }
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
    }

    private void initRecycler() {
        if (connectionsList != null) {
            mAdapter = new ShareAdapter(getActivity(), connectionsList, this);
            shareBinding.shareRecycler.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            shareBinding.shareRecycler.setLayoutManager(linearLayoutManager);
            shareBinding.shareRecycler.setAdapter(mAdapter);
        }
    }

    private List<ShareModel> filter(List<ShareModel> models, String query) {
        query = query.trim().toLowerCase();
        Log.d(TAG, "filter Query : " + query);
        List<ShareModel> filteredModelList = new ArrayList<>();
        for (ShareModel model : models) {
            String searchName = model.getU_first_name().trim().toLowerCase();
            Log.d(TAG, "filter SearchName : " + searchName);
            Log.d(TAG, "filter LastName : " + model.getU_last_name());
            if (searchName.contains(query)) {
                Log.d(TAG, "filter Matched : " + query);
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
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
        searchView.setOnQueryTextListener(this);
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                final List<ShareModel> shareList = filter(connectionsList, query);
                mAdapter.filterList(shareList);
                return true;
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
        });*/
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
        String comment = AppUtils.encodedString(shareBinding.shareComment.getText().toString().trim());

        //logs
        Log.d(TAG, "callShareAPI Test Share : " + test_share);
        Log.d(TAG, "callShareAPI Question Share : " + question_share);
        Log.d(TAG, "callShareAPI Test Id : " + testId);
        Log.d(TAG, "callShareAPI Comment : " + comment);
        Log.d(TAG, "callShareAPI Model Action : " + modelAction);
        Log.d(TAG, "callShareAPI Device Id : " + getDeviceId());
        Log.d(TAG, "callShareAPI UserId : " + getUserId());
        Log.d(TAG, "callShareAPI Call From : " + call_from);

        if (modelAction != null && modelAction.isEmpty()) {
            showToast("Please, select atleast 1 connection or group to share with.");
        } else {
            ProgressDialog.getInstance().show(getActivity());
            if (call_from == learning)
                call = apiService.shareAPI(question_id, question_share, "F", getDeviceId(), getUserId(), modelAction, "1.68", qoogol, comment);
            else
                call = apiService.shareAPI(testId, test_share, "F", getDeviceId(), getUserId(), modelAction, "1.68", qoogol, comment);

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

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ShareModel> filteredModelList = filter(connectionsList, newText);
        if (filteredModelList.size() > 0)
            mAdapter.filterList(filteredModelList);
        else
            mAdapter.filterList(connectionsList);
        return false;
    }
}
