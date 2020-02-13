package com.jangletech.qoogol.ui.edit_profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jangletech.qoogol.model.ProfileData;

/**
 * Created by Pritali on 2/13/2020.
 */
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<ProfileData> personalData;
    ProfileData mpersonalData;

    public ProfileViewModel() {
        personalData = new MutableLiveData<>();
    }

    public LiveData<ProfileData> getPersonalData() {
        return personalData;
    }

    public void setData(ProfileData profileData) {
        this.personalData.setValue(profileData);
    }

}
