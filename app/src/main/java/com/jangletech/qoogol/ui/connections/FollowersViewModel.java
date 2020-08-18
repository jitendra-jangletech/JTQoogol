package com.jangletech.qoogol.ui.connections;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.Followers;
import com.jangletech.qoogol.model.FollowersResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.followers;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/10/2020.
 */
public class FollowersViewModel extends AndroidViewModel {

    ApiInterface apiService;
    public final AppRepository mAppRepository;
    Call<FollowersResponse> call;
    String userId;
    String pagestart;

    public FollowersViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplication()).getUserId();
        pagestart = "0";
    }

    LiveData<List<Followers>> getFollowersList() {
        return mAppRepository.getFollowersFromDb(userId);
    }


    void fetchFollowersData(boolean isRefresh) {
        getData(isRefresh);
    }

    public void deleteUpdatedConnection(String user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> mAppRepository.deleteFollowers(userId, user));
    }

    private void getData(boolean isRefresh) {
        if (isRefresh) {
            pagestart = "0";
            call = apiService.fetchRefreshedFollowers(userId, followers, getDeviceId(getApplication()), qoogol, pagestart, forcerefresh);
        } else {
            call = apiService.fetchFollowers(userId, followers, getDeviceId(getApplication()), qoogol, pagestart);
        }


        call.enqueue(new Callback<FollowersResponse>() {
            @Override
            public void onResponse(Call<FollowersResponse> call, retrofit2.Response<FollowersResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertFollowers(response.body().getFollowers_list()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FollowersResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
