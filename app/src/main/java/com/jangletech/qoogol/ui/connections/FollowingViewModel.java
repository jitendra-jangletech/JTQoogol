package com.jangletech.qoogol.ui.connections;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.Following;
import com.jangletech.qoogol.model.FollowingResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.following;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/10/2020.
 */
public class FollowingViewModel extends AndroidViewModel {
    private ApiInterface apiService;
    public final AppRepository mAppRepository;
    private Call<FollowingResponse> call;
    private String userId;
    private String pagestart;

    public FollowingViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplication()).getUserId();
        pagestart = "0";
    }

    LiveData<List<Following>> getFollowingList() {
        return mAppRepository.getFollowingFromDb(userId);
    }

    void fetchFollowingsData(boolean isRefresh) {
        getData(isRefresh);
    }

    public void deleteUpdatedConnection(String user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> mAppRepository.deleteFollowings(userId, user));
    }


    private void getData(boolean isRefresh) {
        if (isRefresh)
            call = apiService.fetchRefreshedFollowings(userId, following, getDeviceId(getApplication()), qoogol, pagestart, forcerefresh);
        else
            call = apiService.fetchFollowings(userId, following, getDeviceId(getApplication()), qoogol, pagestart);

        call.enqueue(new Callback<FollowingResponse>() {
            @Override
            public void onResponse(Call<FollowingResponse> call, retrofit2.Response<FollowingResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertFollowings(response.body().getFollowing_list()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FollowingResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
