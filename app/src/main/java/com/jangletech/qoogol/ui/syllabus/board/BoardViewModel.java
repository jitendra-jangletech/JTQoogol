package com.jangletech.qoogol.ui.syllabus.board;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.model.UniversityResponse;

import java.util.List;

public class BoardViewModel extends AndroidViewModel {

    private MutableLiveData<List<UniversityResponse>> universityBoardList;

    public LiveData<List<UniversityResponse>> getUniversityBoardList() {
        return universityBoardList;
    }

    public void setUniversityBoardList(List<UniversityResponse> universityBoardList) {
        this.universityBoardList.setValue(universityBoardList);
    }

    public BoardViewModel(@NonNull Application application) {
        super(application);
        if (universityBoardList != null) {
            universityBoardList = new MutableLiveData<>();
        }
    }


}
