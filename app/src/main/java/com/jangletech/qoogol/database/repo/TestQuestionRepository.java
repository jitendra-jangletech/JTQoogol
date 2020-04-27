package com.jangletech.qoogol.database.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.database.dao.TestQuestionDao;
import com.jangletech.qoogol.model.TestQuestion;

import java.util.List;

public class TestQuestionRepository {

    private TestQuestionDao testQuestionDao;
    private LiveData<List<TestQuestion>> testQuestions;


    public TestQuestionRepository(Application application) {
        QoogolDatabase db = QoogolDatabase.getDatabase(application);
        testQuestionDao = db.testQuestionDao();
        testQuestions = testQuestionDao.getTestQuestions();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<TestQuestion>> getAllTestQuestions() {
        return testQuestions;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(TestQuestion testQuestion) {
        QoogolDatabase.databaseWriteExecutor.execute(() -> {
            testQuestionDao.insert(testQuestion);
        });
    }



}
