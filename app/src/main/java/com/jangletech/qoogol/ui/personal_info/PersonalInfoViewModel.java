package com.jangletech.qoogol.ui.personal_info;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.City;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.District;
import com.jangletech.qoogol.model.Language;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.UserProfile;

import java.util.List;
import java.util.Map;

public class PersonalInfoViewModel extends AndroidViewModel {

    private MutableLiveData<List<Country>> countrylist;
    private MutableLiveData<List<State>> statelist;
    private MutableLiveData<List<City>> citylist;
    private MutableLiveData<List<District>> districtList;
    private MutableLiveData<List<Language>> languageList;
    private MutableLiveData<UserProfile> userProfileResponse;

    Map<Integer, String> mMapCountry;
    Map<Integer, String> mMapState;
    Map<Integer, String> mMapCity;

    public PersonalInfoViewModel(@NonNull Application application) {
        super(application);
        countrylist = new MutableLiveData<>();
        statelist = new MutableLiveData<>();
        citylist = new MutableLiveData<>();
        userProfileResponse = new MutableLiveData<>();
        languageList = new MutableLiveData<>();
        districtList = new MutableLiveData<>();
    }

    public LiveData<List<District>> getDistricts() {
        return districtList;
    }

    public void setDistrictList(List<District> districtList) {
        this.districtList.setValue(districtList);
    }

    public LiveData<List<Language>> getLanguages() {
        return languageList;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList.setValue(languageList);
    }

    public LiveData<UserProfile> getUserProfileInfo() {
        return userProfileResponse;
    }

    public void setUserProfileResponse(UserProfile userProfileResponse) {
        this.userProfileResponse.setValue(userProfileResponse);
    }

    public LiveData<List<City>> getCities(){
        return citylist;
    }

    public LiveData<List<Country>> getCountries(){
        return countrylist;
    }

    public void setCountryList(List<Country> countryList) {
        countrylist.setValue(countryList);
    }

    public LiveData<List<State>> getStates(){
        return statelist;
    }

    public void setStateList(List<State> stateList) {
        statelist.setValue(stateList);
    }

    public void setCityList(List<City> cityList) {
       this.citylist.setValue(cityList);
    }
}
