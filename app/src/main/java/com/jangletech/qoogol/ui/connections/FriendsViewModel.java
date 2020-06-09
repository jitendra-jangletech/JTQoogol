package com.jangletech.qoogol.ui.connections;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;

/**
 * Created by Pritali on 6/8/2020.
 */
public class FriendsViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
    }

    void fetchFriendsData() {

    }
}
