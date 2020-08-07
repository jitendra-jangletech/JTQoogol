package com.jangletech.qoogol.ui.connections;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.FollowRequest;
import com.jangletech.qoogol.model.FollowRequestResponse;
import com.jangletech.qoogol.model.FriendRequest;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;


import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.followrequests;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/10/2020.
 */
public class FollowReqViewModel extends AndroidViewModel {
    private static final String TAG = "FollowReqViewModel";
    private ApiInterface apiService;
    public final AppRepository mAppRepository;
    private Call<FollowRequestResponse> call;
    private String userId;
    private String pagestart;

    public FollowReqViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplication()).getUserId();
        pagestart = "0";

    }

    LiveData<List<FollowRequest>> getFollowReqdList() {
        return mAppRepository.getFollowReqFromDb(userId);
    }

    public void insert(List<FollowRequest> followRequests) {
        mAppRepository.insertFollowRequests(followRequests);
    }

    void deleteUpdatedConnection(String user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> mAppRepository.deleteFollowReq(userId,user));
    }

    void fetchFollowReqData(boolean isRefresh) {
        getData(isRefresh);
    }

    private void getData(boolean isRefresh) {
        if (isRefresh)
            call = apiService.fetchFollowRequests(userId, followrequests, getDeviceId(getApplication()), qoogol, pagestart);
        else
            call = apiService.fetchRefreshedFollowReq(userId, followrequests, getDeviceId(getApplication()), qoogol, pagestart, forcerefresh);

        call.enqueue(new Callback<FollowRequestResponse>() {
            @Override
            public void onResponse(Call<FollowRequestResponse> call, retrofit2.Response<FollowRequestResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        Log.d(TAG, "onResponse Follow Request : "+response.body().getFollowreq_list().size());
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertFollowdReq(response.body().getFollowreq_list()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FollowRequestResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
