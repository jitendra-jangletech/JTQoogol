package com.jangletech.qoogol.ui.test_details;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jangletech.qoogol.model.QSet;

import java.util.List;

public class TestDetailsViewModel extends AndroidViewModel {

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
    }
}
