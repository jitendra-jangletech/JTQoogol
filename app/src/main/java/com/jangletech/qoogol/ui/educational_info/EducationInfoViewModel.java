package com.jangletech.qoogol.ui.educational_info;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.Education;

import java.util.List;

public class EducationInfoViewModel extends AndroidViewModel {

    private MutableLiveData<List<Education>> educationList;

    public LiveData<List<Education>> getEducationList(){
        return educationList;
    }

    public void setEducationList(List<Education> educationList){
        this.educationList.setValue(educationList);
    }

    public EducationInfoViewModel(@NonNull Application application) {
        super(application);
        this.educationList = new MutableLiveData<>();
    }
}
