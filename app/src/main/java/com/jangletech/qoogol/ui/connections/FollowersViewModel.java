package com.jangletech.qoogol.ui.connections;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.Followers;
import com.jangletech.qoogol.model.FollowersResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
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
    private Application application;
    private MutableLiveData<Boolean> isEmptyList;

    public FollowersViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplication()).getUserId();
        isEmptyList = new MutableLiveData<>();
        pagestart = "0";
    }

    public LiveData<Boolean> getStatus() {
        return isEmptyList;
    }

    public void setStatus(Boolean bool) {
        isEmptyList.setValue(bool);
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
                    if (response.body() != null &&
                            response.body().getResponse().equalsIgnoreCase("200")) {
                        List<Followers> followersList = new ArrayList<>();
                        for (Followers followers : response.body().getFollowers_list()) {
                            followers.setU_first_name(AESSecurities.getInstance().decrypt(TinyDB.getInstance(application).getString(Constant.cf_key1), followers.getU_first_name()));
                            followers.setU_last_name(AESSecurities.getInstance().decrypt(TinyDB.getInstance(application).getString(Constant.cf_key2), followers.getU_last_name()));
                            followersList.add(followers);
                        }
                        if (followersList.size() > 0) {
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            executor.execute(() -> mAppRepository.insertFollowers(followersList));
                        } else {
                            isEmptyList.setValue(true);
                        }
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
