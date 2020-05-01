package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.TestQuestionNew;

import java.util.List;

public class StartTestViewModel extends AndroidViewModel {

    public MutableLiveData<List<TestQuestionNew>> getTestQuestAnsList() {
        return testQuestAnsList;
    }

    public void setTestQuestAnsList(List<TestQuestionNew> testQuestAnsList) {
        this.testQuestAnsList.setValue(testQuestAnsList);
    }

    //private TestQuestionRepository mRepository;
    private MutableLiveData<List<TestQuestionNew>> testQuestAnsList;

    public StartTestViewModel(@NonNull Application application) {
        super(application);
        //mRepository = new TestQuestionRepository(application);
        testQuestAnsList = new MutableLiveData<>();
    }
}
