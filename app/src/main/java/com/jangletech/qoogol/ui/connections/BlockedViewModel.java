package com.jangletech.qoogol.ui.connections;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.BlockedConnResp;
import com.jangletech.qoogol.model.BlockedConnections;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.block;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/8/2020.
 */
public class BlockedViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;
    Call<BlockedConnResp> call;
    String userId;
    String pagestart;

    public BlockedViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplicationContext()).getUserId();
        pagestart = "0";
    }

    LiveData<List<BlockedConnections>> getBlockList() {
        return mAppRepository.getBlockListFromDb(userId);
    }


    void fetchBlockConnData(boolean isRefresh) {
        getData(isRefresh);
    }


    private void getData(boolean isRefresh) {
        if (isRefresh)
            call = apiService.fetchRefreshedBlockedConn(userId, block, getDeviceId(), qoogol, pagestart, forcerefresh);
        else
            call = apiService.fetchBlockedConnections(userId, block, getDeviceId(), qoogol, Integer.parseInt(pagestart));
        call.enqueue(new Callback<BlockedConnResp>() {
            @Override
            public void onResponse(Call<BlockedConnResp> call, retrofit2.Response<BlockedConnResp> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertBlockedList(response.body().getBlocked_list()));
//                        pagestart = response.body().getRow_count();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BlockedConnResp> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
