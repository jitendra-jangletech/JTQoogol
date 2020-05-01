package com.jangletech.qoogol.ui.test.my_test;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.TestRepository;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.model.TestModelNew;

import java.util.List;

public class MyTestViewModel extends AndroidViewModel {

    private TestRepository mRepository;
    //private LiveData<List<TestModel>> mAllTests;

    public LiveData<List<TestModelNew>> getAllTestList() {
        return allTestList;
    }

    public void setAllTestList(List<TestModelNew> allTestList) {
        this.allTestList.setValue(allTestList);
    }

    private MutableLiveData<List<TestModelNew>> allTestList;

    public MyTestViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TestRepository(application);
        //mAllTests = mRepository.getAllTests();
        allTestList = new MutableLiveData<>();
    }

//    LiveData<List<TestModel>> getAllTests() {
//        return mAllTests;
//    }

    public void insert(TestModel testModel) {
        mRepository.insert(testModel);
    }
}
