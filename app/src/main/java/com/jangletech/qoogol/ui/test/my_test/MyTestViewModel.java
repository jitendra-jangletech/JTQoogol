package com.jangletech.qoogol.ui.test.my_test;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.Chapter;
import com.jangletech.qoogol.model.ClassResponse;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.model.UserPreferences;

import java.util.List;

public class MyTestViewModel extends AndroidViewModel {

    private AppRepository appRepository;
    private MutableLiveData<List<TestModelNew>> allTestList;
    private MutableLiveData<List<FetchSubjectResponse>> allSubjectList;
    private MutableLiveData<UserPreferences> userPreference;
    private MutableLiveData<List<ClassResponse>> classes;
    private MutableLiveData<List<Degree>> degrees;
    private MutableLiveData<List<Course>> courses;
    private MutableLiveData<List<Institute>> institutes;
    private MutableLiveData<List<Chapter>> allChapterList;

    public void setAllChapterList(List<Chapter> chapterList) {
        this.allChapterList.setValue(chapterList);
    }

    public LiveData<List<Chapter>> getAllChapter() {
        return allChapterList;
    }

    public LiveData<List<Degree>> getDegrees() {
        return degrees;
    }

    public void setDegrees(List<Degree> degrees) {
        this.degrees.setValue(degrees);
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses.setValue(courses);
    }

    public LiveData<List<Institute>> getInstitutes() {
        return institutes;
    }

    public void setInstitutes(List<Institute> institutes) {
        this.institutes.setValue(institutes);
    }

    public LiveData<List<ClassResponse>> getClasses() {
        return classes;
    }

    public void setClassList(List<ClassResponse> classList) {
        this.classes.setValue(classList);
    }

    public LiveData<UserPreferences> getPreferences() {
        return userPreference;
    }

    public void setUserPreference(UserPreferences userPreference) {
        this.userPreference.setValue(userPreference);
    }

    public LiveData<List<FetchSubjectResponse>> getAllSubjects() {
        return allSubjectList;
    }

    public LiveData<List<TestModelNew>> getAllTestList() {
        return allTestList;
    }

    public void setAllSubjectList(List<FetchSubjectResponse> subjectList) {
        this.allSubjectList.setValue(subjectList);
    }

    public void setAllTestList(List<TestModelNew> allTestList) {
        this.allTestList.setValue(allTestList);
    }


    public MyTestViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        allTestList = new MutableLiveData<>();
        allSubjectList = new MutableLiveData<>();
        allChapterList = new MutableLiveData<>();
        userPreference = new MutableLiveData<>();
        classes = new MutableLiveData<>();
        degrees = new MutableLiveData<>();
        courses = new MutableLiveData<>();
        institutes = new MutableLiveData<>();
    }

    public void insert(List<TestModelNew> testModelNewList) {
        appRepository.insertTests(testModelNewList);
    }

    public LiveData<List<TestModelNew>> getAllTests(String flag) {
        return appRepository.getAllTests(flag);
    }

    /*public void insertAttemptedTest(List<AttemptedTest> attemptedTests) {
        appRepository.insertAttemptedTest(attemptedTests);
    }

    public void deleteAttemptedTest() {
        appRepository.deleteAttemptedTest();
    }

    public LiveData<List<AttemptedTest>> getAllAttemptedTests() {
        return appRepository.getAllAttemptedTests();
    }*/
}
