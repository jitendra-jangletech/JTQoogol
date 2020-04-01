package com.jangletech.qoogol.ui.personal_info;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.CityObject;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.GetUserPersonalDetails;
import com.jangletech.qoogol.model.State;

import java.util.List;
import java.util.Map;

public class PersonalInfoViewModel extends AndroidViewModel {

    private MutableLiveData<List<Country>> countrylist;
    private MutableLiveData<List<State>> statelist;
    private MutableLiveData<List<CityObject>> citylist;


    Map<Integer, String> mMapCountry;
    Map<Integer, String> mMapState;
    Map<Integer, String> mMapCity;

    public PersonalInfoViewModel(@NonNull Application application) {
        super(application);
        countrylist = new MutableLiveData<>();
        statelist = new MutableLiveData<>();
        citylist = new MutableLiveData<>();
    }


    public void setCountryList(List<Country> countryList) {
        countrylist.setValue(countryList);
    }

    public void setStateList(List<State> stateList) {
        statelist.setValue(stateList);
    }

    public void setCityList(List<CityObject> cityList) {
        citylist.setValue(cityList);
    }

}