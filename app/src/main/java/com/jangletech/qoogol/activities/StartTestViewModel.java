package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.TestQuestionNew;

import java.util.List;

public class StartTestViewModel extends AndroidViewModel {

    private MutableLiveData<List<TestQuestionNew>> testQuestAnsList;
    private MutableLiveData<StartResumeTestResponse> startResumeTestObj;

    public MutableLiveData<List<TestQuestionNew>> getTestQuestAnsList() {
        return testQuestAnsList;
    }

    public void setTestQuestAnsList(List<TestQuestionNew> testQuestAnsList) {
        this.testQuestAnsList.setValue(testQuestAnsList);
    }

    public MutableLiveData<StartResumeTestResponse> getStartResumeTestResponse() {
        return startResumeTestObj;
    }

    public void setStartResumeTestResponse(StartResumeTestResponse startResumeTestResponse) {
        this.startResumeTestObj.setValue(startResumeTestResponse);
    }


    public StartTestViewModel(@NonNull Application application) {
        super(application);
        startResumeTestObj = new MutableLiveData<>();
        testQuestAnsList = new MutableLiveData<>();
    }
}
