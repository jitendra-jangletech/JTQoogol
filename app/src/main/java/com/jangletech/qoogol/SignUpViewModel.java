package com.jangletech.qoogol;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jangletech.qoogol.model.ClassData;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.SignInModel;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.University;

import java.util.List;
import java.util.Map;

/**
 * Created by Pritali on 1/31/2020.
 */
public class SignUpViewModel extends ViewModel {
    private MutableLiveData<SignInModel> signinlist;
    private MutableLiveData<List<Country>> countrylist;
    private MutableLiveData<List<State>> statelist;
    private MutableLiveData<List<University>> universitylist;
    private MutableLiveData<List<Institute>> institutelist;
    private MutableLiveData<List<Degree>> degreelist;
    private MutableLiveData<List<Course>> courselist;
    private MutableLiveData<List<ClassData>> classlist;
    Map<Integer, String> mMapCountry;
    Map<Integer, String> mMapState;
    Map<Integer, String> mMapUniversity;
    Map<Integer, String> mMapInstitute;
    Map<Integer, String> mMapDegree;
    Map<Integer, String> mMapCourse;
    Map<Integer, String> mMapClass;

    public SignUpViewModel() {
        signinlist = new MutableLiveData<>();
        countrylist = new MutableLiveData<>();
        statelist = new MutableLiveData<>();
        universitylist = new MutableLiveData<>();
        institutelist = new MutableLiveData<>();
        degreelist = new MutableLiveData<>();
        courselist = new MutableLiveData<>();
        classlist = new MutableLiveData<>();
    }


    public LiveData<SignInModel> getData() {
        return signinlist;
    }

    public void setData(SignInModel signinlist) {
        this.signinlist.setValue(signinlist);
    }

    public LiveData<List<Country>> getCountry() {
        return countrylist;
    }

    public void setCountryList(List<Country> countryList) {
        countrylist.setValue(countryList);
    }

    public LiveData<List<State>> getStateList() {
        return statelist;
    }

    public void setStateList(List<State> stateList) {
        statelist.setValue(stateList);
    }

    public LiveData<List<University>> getUniversityList() {
        return universitylist;
    }

    public void setUniversityList(List<University> universityList) {
        universitylist.setValue(universityList);
    }

    public LiveData<List<Institute>> getInstituteList() {
        return institutelist;
    }

    public void setInstituteList(List<Institute> instituteList) {
        institutelist.setValue(instituteList);
    }

    public LiveData<List<Degree>> getDegreeList() {
        return degreelist;
    }

    public void setDegreeList(List<Degree> degreeList) {
        degreelist.setValue(degreeList);
    }

    public LiveData<List<Course>> getCourseList() {
        return courselist;
    }

    public void setCourseList(List<Course> courseList) {
        courselist.setValue(courseList);
    }

    public LiveData<List<ClassData>> getClassList() {
        return classlist;
    }

    public void setClassList(List<ClassData> courseList) {
        classlist.setValue(courseList);
    }
}
