package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.List;

public class PracticeTestViewModel extends AndroidViewModel {

    private MutableLiveData<List<LearningQuestionsNew>> practiceQAList;

    public LiveData<List<LearningQuestionsNew>> getPracticeQA(){
        return practiceQAList;
    }

    public void setPracticeQAList(List<LearningQuestionsNew> learningQuestionsNew){
        this.practiceQAList.setValue(learningQuestionsNew);
    }

    public PracticeTestViewModel(@NonNull Application application) {
        super(application);
        practiceQAList = new MutableLiveData<>();
    }

}
