package com.jangletech.qoogol.ui.connections;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.FriendRequest;
import com.jangletech.qoogol.model.FriendRequestResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.friendrequests;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/10/2020.
 */
public class FriendReqViewModel extends AndroidViewModel {

    private static final String TAG = "FriendReqViewModel";
    ApiInterface apiService;
    public final AppRepository mAppRepository;
    Call<FriendRequestResponse> call;
    String userId;
    String pagestart;


    public FriendReqViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplication()).getUserId();
        pagestart = "0";
    }


    LiveData<List<FriendRequest>> getFrienReqdList() {
        return mAppRepository.getFriendReqFromDb(userId);
    }

    public void insert(List<FriendRequest> friendRequests) {
        mAppRepository.insertFriendRequests(friendRequests);
    }

    public void deleteUpdatedConnection(String user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> mAppRepository.deleteFriendReq(userId, user));
    }

    void fetchFriendReqData(boolean isRefresh) {
        getData(isRefresh);
    }


    private void getData(boolean isRefresh) {
        if (isRefresh)
            call = apiService.fetchRefreshedFriendReq(userId, friendrequests, getDeviceId(getApplication()), qoogol, pagestart, forcerefresh);
        else
            call = apiService.fetchFriendRequests(userId, friendrequests, getDeviceId(getApplication()), qoogol, pagestart);

        call.enqueue(new Callback<FriendRequestResponse>() {
            @Override
            public void onResponse(Call<FriendRequestResponse> call, retrofit2.Response<FriendRequestResponse> response) {
                try {

                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        Log.d(TAG, "onResponse: ");
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertFriendReq(response.body().getFriend_req_list()));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FriendRequestResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
