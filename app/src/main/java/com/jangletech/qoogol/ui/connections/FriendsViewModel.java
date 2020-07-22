package com.jangletech.qoogol.ui.connections;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Friends;
import com.jangletech.qoogol.model.FriendsResponse;
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
import static com.jangletech.qoogol.util.Constant.friends;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/8/2020.
 */
public class FriendsViewModel extends AndroidViewModel {
    private ApiInterface apiService;
    public final AppRepository mAppRepository;
    private Call<FriendsResponse> call;
    private String userId;
    private String pagestart;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplication()).getUserId();
        pagestart = "0";
    }

    LiveData<List<Friends>> getFriendList() {
        return mAppRepository.getFriendsFromDb(userId);
    }

    void deleteUpdatedConnection(String user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> mAppRepository.deleteriends(userId,user));
    }

    void fetchFriendsData(boolean isRefresh) {
        getData(isRefresh);
    }


    private void getData(boolean isRefresh) {
        if (isRefresh)
            call = apiService.fetchRefreshedFriendss(userId, friends, getDeviceId(getApplication()), qoogol, pagestart, forcerefresh);
        else
            call = apiService.fetchFriends(userId, friends, getDeviceId(getApplication()), qoogol, pagestart);
        call.enqueue(new Callback<FriendsResponse>() {
            @Override
            public void onResponse(Call<FriendsResponse> call, retrofit2.Response<FriendsResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertFriends(response.body().getFriends_list()));
                    }else if (response.body().getResponse().equals("501")) {
                        //resetSettingAndLogout();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FriendsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
