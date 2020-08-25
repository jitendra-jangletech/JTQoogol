package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.AppConfigResponse;

public class SplashViewModel extends AndroidViewModel {

    private MutableLiveData<AppConfigResponse> appConfigResponse;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        appConfigResponse = new MutableLiveData<>();
    }

    public void setAppConfigResponse(AppConfigResponse appConfigResponse) {
        this.appConfigResponse.setValue(appConfigResponse);
    }

    public LiveData<AppConfigResponse> getAppConfigResponse() {
        return appConfigResponse;
    }


}
