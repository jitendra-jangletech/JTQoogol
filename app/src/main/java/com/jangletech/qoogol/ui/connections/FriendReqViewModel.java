package com.jangletech.qoogol.ui.connections;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.model.FriendRequest;
import com.jangletech.qoogol.model.FriendRequestResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.friendrequests;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/10/2020.
 */
public class FriendReqViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;
    Call<FriendRequestResponse> call;
    String userId;
    String pagestart;


    public FriendReqViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplicationContext()).getUserId();
        pagestart = "0";
    }


    LiveData<List<FriendRequest>> getFrienReqdList(String uId) {
        return mAppRepository.getFriendReqFromDb(uId);
    }

    public void insert(List<FriendRequest> friendRequests) {
        mAppRepository.insertFriendRequests(friendRequests);
    }

    void fetchFriendReqData(boolean isRefresh) {
        getData(isRefresh);
    }


    private void getData(boolean isRefresh) {
        if (isRefresh)
            call = apiService.fetchRefreshedFriendReq(userId,friendrequests, getDeviceId(), qoogol,pagestart,forcerefresh);
        else
            call = apiService.fetchFriendRequests(userId,friendrequests, getDeviceId(), qoogol,pagestart);

        call.enqueue(new Callback<FriendRequestResponse>() {
            @Override
            public void onResponse(Call<FriendRequestResponse> call, retrofit2.Response<FriendRequestResponse> response) {
                try {
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
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
