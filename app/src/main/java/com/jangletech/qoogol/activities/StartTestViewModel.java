package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.repo.TestQuestionRepository;
import com.jangletech.qoogol.model.TestQuestion;

import java.util.List;

public class StartTestViewModel extends AndroidViewModel {

    private TestQuestionRepository mRepository;
    private LiveData<List<TestQuestion>> mAllQuestions;

    public StartTestViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TestQuestionRepository(application);
        mAllQuestions = mRepository.getAllTestQuestions();
    }

    LiveData<List<TestQuestion>> getAllQuestions() {
        return mAllQuestions;
    }

    public void insert(TestQuestion testQuestion) {
        mRepository.insert(testQuestion);
    }
}
