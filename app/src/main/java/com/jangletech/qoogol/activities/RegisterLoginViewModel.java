package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.RegisterLoginModel;

public class RegisterLoginViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private MutableLiveData<RegisterLoginModel> registerLoginModel;

    public LiveData<RegisterLoginModel> getRegisterLoginData(){
        return registerLoginModel;
    }

    public void setRegisterLoginModel(RegisterLoginModel registerLoginModel){
        this.registerLoginModel.setValue(registerLoginModel);
    }

    public RegisterLoginViewModel(@NonNull Application application) {
        super(application);
        registerLoginModel = new MutableLiveData<>();
        appRepository = new AppRepository(application);
    }

    public void deleteTests() {
        appRepository.deleteTests();
    }

    public void deleteNotifications() {
        appRepository.deleteNotifications();
    }

    public void deleteEducations() {
        appRepository.deleteAllEducations();
    }

    public void deletePersonalInfo() {
        appRepository.deletePersonalInfo();
    }

    public void deleteEducation(String id) {
        appRepository.deleteEducationInfo(id);
    }

    public void deleteDashboard() {
        appRepository.deleteDashboardData();
    }

}
