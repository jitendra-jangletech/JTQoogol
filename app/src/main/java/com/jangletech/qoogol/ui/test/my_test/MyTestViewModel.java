package com.jangletech.qoogol.ui.test.my_test;

import android.app.Application;
import android.util.Log;
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
import com.jangletech.qoogol.model.UserPreferenceResponse;
import com.jangletech.qoogol.model.UserPreferences;

import java.util.List;

public class MyTestViewModel extends AndroidViewModel {

    private static final String TAG = "MyTestViewModel";
    private AppRepository appRepository;
    private MutableLiveData<List<TestModelNew>> allTestList;
    private MutableLiveData<List<FetchSubjectResponse>> allSubjectList;
    private MutableLiveData<UserPreferenceResponse> userPreference;
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

    public LiveData<UserPreferenceResponse> getPreferences() {
        return userPreference;
    }

    public void setUserPreference(UserPreferenceResponse userPreferenceResponse) {
        this.userPreference.setValue(userPreferenceResponse);
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

    public LiveData<List<TestModelNew>> getAllTests(String flag, String userId) {
        Log.d(TAG, "getAllTests Flag : " + flag);
        Log.d(TAG, "getAllTests userId : " + userId);
        return appRepository.getAllTests(flag, userId);
    }

    public LiveData<List<TestModelNew>> getAllFavTests(String flag, String userId) {
        Log.d(TAG, "getAllTests Flag : " + flag);
        Log.d(TAG, "getAllTests userId : " + userId);
        return appRepository.getAllFavTests(flag, userId);
    }

    public void updateFav(String flag, String userId, int tmId, boolean value) {
        appRepository.updateFavTest(flag, userId, tmId, value);
    }

    public LiveData<List<TestModelNew>> getAllTestByDifficultyLevel(String flag, String userId, String diffLevel) {
        Log.d(TAG, "getAllTests Flag : " + flag);
        Log.d(TAG, "getAllTests userId : " + userId);
        Log.d(TAG, "getAllTests DiffLevel : " + diffLevel);
        return appRepository.getAllTestsFiltered(flag, userId, diffLevel);
    }

    public LiveData<List<TestModelNew>> getAllTestByAvgRating(String flag, String userId, String avgRating) {
        Log.d(TAG, "getAllTests Flag : " + flag);
        Log.d(TAG, "getAllTests userId : " + userId);
        Log.d(TAG, "getAllTests Avg Rating : " + avgRating);
        return appRepository.getAllTestsAvgRating(flag, userId, avgRating);
    }
}
