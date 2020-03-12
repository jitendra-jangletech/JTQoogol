package com.jangletech.qoogol.ui.personal_info;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.model.GetUserPersonalDetails;

public class PersonalInfoViewModel extends AndroidViewModel {

    private PersonalInfoRepository personalInfoRepository;

    public PersonalInfoViewModel(@NonNull Application application) {
        super(application);
        personalInfoRepository = new PersonalInfoRepository(application);
    }
    public LiveData<GetUserPersonalDetails> getUserPersonalDetailsLiveData() {
        return personalInfoRepository.getMutableLiveData();
    }

}
