package com.jangletech.qoogol.ui.test.my_test;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.TestModelNew;

import java.util.List;

public class MyTestViewModel extends AndroidViewModel {

    private MutableLiveData<List<TestModelNew>> allTestList;
    private MutableLiveData<List<FetchSubjectResponse>> allSubjectList;

    public LiveData<List<FetchSubjectResponse>> getAllSubjects(){
        return allSubjectList;
    }

    public LiveData<List<TestModelNew>> getAllTestList() {
        return allTestList;
    }

    public void setAllSubjectList(List<FetchSubjectResponse> subjectList){
        this.allSubjectList.setValue(subjectList);
    }

    public void setAllTestList(List<TestModelNew> allTestList) {
        this.allTestList.setValue(allTestList);
    }



    public MyTestViewModel(@NonNull Application application) {
        super(application);
        //mRepository = new TestRepository(application);
        allTestList = new MutableLiveData<>();
        allSubjectList = new MutableLiveData<>();
    }

   /* public void insert(TestModel testModel) {
        mRepository.insert(testModel);
    }*/
}
