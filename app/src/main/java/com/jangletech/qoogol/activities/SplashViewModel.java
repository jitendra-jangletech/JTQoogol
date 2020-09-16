package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.AppConfigResponse;
import com.jangletech.qoogol.model.UserProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashViewModel extends AndroidViewModel {

    private MutableLiveData<AppConfigResponse> appConfigResponse;
    private AppRepository appRepository;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        appConfigResponse = new MutableLiveData<>();
    }

    public void setAppConfigResponse(AppConfigResponse appConfigResponse) {
        this.appConfigResponse.setValue(appConfigResponse);
    }

    public LiveData<AppConfigResponse> getAppConfigResponse() {
        return appRepository.getAppConfig();
    }

    public void insert(AppConfigResponse appConfigResponse) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> appRepository.insertAppConfig(appConfigResponse));
    }
}
