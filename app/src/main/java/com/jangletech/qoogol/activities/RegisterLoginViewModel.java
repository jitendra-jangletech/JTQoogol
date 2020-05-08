package com.jangletech.qoogol.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.RegisterLoginModel;

public class RegisterLoginViewModel extends AndroidViewModel {

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
    }
}
