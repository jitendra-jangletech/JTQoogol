package com.jangletech.qoogol.ui;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jitendra Yadav on 2/20/2020.
 */
public class GeneralViewModel extends AndroidViewModel {

    private static final String TAG = "GeneralViewModel";

    public int feedPosition = 0;
    public int groupFeedPosition = 0;
    public int connPosition;
    public int matchPosition;
    public int connectionMessagePosition;
    public int otherConnMessagePosition;
    public String feedPageFetch = "0";
    public boolean fromSignUp = false;
    private MutableLiveData<Boolean> isGPSEnabled;
    private MutableLiveData<Map<String, String>> mapMutableLiveReportData;

    private final ExecutorService executor;

    public boolean playWhenReady = true;
    public int currentWindow = 0;
    public long playbackPosition = 0;
    public float lastPlaySpeed = 1.0f;

    public GeneralViewModel(@NonNull Application application) {
        super(application);
        isGPSEnabled = new MutableLiveData<>();
        executor = Executors.newSingleThreadExecutor();
        mapMutableLiveReportData = new MutableLiveData<>();
    }


    public void setReportMapData(Map<String, String> reportMapData) {
        mapMutableLiveReportData.setValue(reportMapData);
    }

    public LiveData<Map<String, String>> getReportMapData() {
        return mapMutableLiveReportData;
    }


    public LiveData<Boolean> isGPSEnabled() {
        return isGPSEnabled;
    }

    public void setIsGPSEnabled() {
        isGPSEnabled.setValue(false);
    }


}
