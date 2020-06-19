package com.jangletech.qoogol.ui.test_details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.QSet;
import com.jangletech.qoogol.model.TestDetailsResponse;
import com.jangletech.qoogol.model.TestModelNew;

import java.util.List;

public class TestDetailsViewModel extends AndroidViewModel {

    private AppRepository appRepository;

    public LiveData<List<QSet>> getQsetList() {
        return qsetList;
    }

    public void setQsetList(List<QSet> qsetList) {
        this.qsetList.setValue(qsetList);
    }

    private MutableLiveData<List<QSet>> qsetList;


    public TestDetailsViewModel(@NonNull Application application) {
        super(application);
        this.qsetList = new MutableLiveData<>();
        appRepository = new AppRepository(application);
    }

   /* public void insert(TestDetailsResponse testDetailsResponse) {
        appRepository.insertTestDetails(testDetailsResponse);
    }*/

}
