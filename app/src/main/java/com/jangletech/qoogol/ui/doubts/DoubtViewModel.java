package com.jangletech.qoogol.ui.doubts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;

/**
 * Created by Pritali on 7/31/2020.
 */
public class DoubtViewModel extends AndroidViewModel {
    ApiInterface apiService;
    public final AppRepository mAppRepository;

    public DoubtViewModel(@NonNull Application application) {
        super(application);
        apiService = ApiClient.getInstance().getApi();
        mAppRepository = new AppRepository(application);
    }
}
