package com.jangletech.qoogol.ui.create_test;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.TestSection;

import java.util.List;

public class TestSectionViewModel extends AndroidViewModel {

    private MutableLiveData<List<TestSection>> testSections;

    public TestSectionViewModel(@NonNull Application application) {
        super(application);
        testSections = new MutableLiveData<>();
    }

    public void setTestSections(List<TestSection> testSections) {
        this.testSections.setValue(testSections);
    }

    public LiveData<List<TestSection>> getTestSections() {
        return testSections;
    }
}
