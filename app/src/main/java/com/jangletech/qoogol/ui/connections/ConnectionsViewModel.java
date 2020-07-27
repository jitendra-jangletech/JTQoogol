package com.jangletech.qoogol.ui.connections;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;
import static com.jangletech.qoogol.util.Constant.connections;
import static com.jangletech.qoogol.util.Constant.forcerefresh;
import static com.jangletech.qoogol.util.Constant.qoogol;

/**
 * Created by Pritali on 6/8/2020.
 */
public class ConnectionsViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;
    Call<ConnectionResponse> call;
    String userId;
    String pagestart;
    private MutableLiveData<ConnectionResponse> connectionResponseMutableLiveData;

    public ConnectionsViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
        userId = new PreferenceManager(getApplication()).getUserId();
        pagestart = "0";
        connectionResponseMutableLiveData = new MutableLiveData<>();
    }

    LiveData<List<Connections>> getConnectionsList(String userId) {
        return mAppRepository.getConnectionsFromDb(userId);
    }

    public void insert(List<Connections> connections) {
        mAppRepository.insertConnections(connections);
    }

    void deleteUpdatedConnection(String user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> mAppRepository.deleteConnections(userId, user));
    }

    void fetchConnectionsData(boolean isRefresh) {
        getData(isRefresh);
    }

    private void getData(boolean isRefresh) {
        if (isRefresh)
            call = apiService.fetchRefreshedConnections(userId, connections, getDeviceId(getApplication()), qoogol, pagestart, forcerefresh);
        else
            call = apiService.fetchConnections(userId, connections, getDeviceId(getApplication()), qoogol, pagestart);
        call.enqueue(new Callback<ConnectionResponse>() {
            @Override
            public void onResponse(Call<ConnectionResponse> call, retrofit2.Response<ConnectionResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertConnections(response.body().getConnection_list()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ConnectionResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
