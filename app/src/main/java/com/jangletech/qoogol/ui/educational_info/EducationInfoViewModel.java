package com.jangletech.qoogol.ui.educational_info;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.model.VerifyResponse;

import java.util.List;

public class EducationInfoViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private MutableLiveData<List<Education>> educationList;
    private MutableLiveData<VerifyResponse> deleteResponse;


    public LiveData<VerifyResponse> getDeleteResponse(){
        return deleteResponse;
    }

    public void setDeleteResponse(VerifyResponse verifyResponse){
        this.deleteResponse.setValue(verifyResponse);
    }

    public EducationInfoViewModel(@NonNull Application application) {
        super(application);
        this.educationList = new MutableLiveData<>();
        this.deleteResponse = new MutableLiveData<>();
        appRepository = new AppRepository(application);
    }

    public void insert(List<Education> educations) {
        appRepository.insertEducationInfo(educations);
    }

    public void delete(String id) {
        appRepository.deleteEducationInfo(id);
    }

    public LiveData<List<Education>> getAllEducations() {
        return appRepository.getUserEducations();
    }
}
