package com.jangletech.qoogol.database.repo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.database.dao.TestDao;
import com.jangletech.qoogol.model.TestModel;

import java.util.List;

public class TestRepository {

    private static final String TAG = "TestRepository";

    private TestDao testDao;
    private LiveData<List<TestModel>> tests;


    public TestRepository(Application application) {
        QoogolDatabase db = QoogolDatabase.getDatabase(application);
        testDao = db.testDao();
        tests = testDao.getAllTests();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<TestModel>> getAllTests() {
        return tests;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(TestModel testModel) {
        QoogolDatabase.databaseWriteExecutor.execute(() -> {
            testDao.insert(testModel);
        });
    }

}
