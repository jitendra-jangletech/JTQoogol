package com.jangletech.qoogol.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.model.Notification;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private AppRepository appRepository;

    public LiveData<DashBoard> getDashBoard() {
        return dashBoard;
    }

    public void setDashBoard(DashBoard dashBoard) {
        this.dashBoard.setValue(dashBoard);
    }

    private MutableLiveData<DashBoard> dashBoard;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        dashBoard = new MutableLiveData<>();
    }

    public void insert(DashBoard dashBoard) {
        appRepository.insertDashboardData(dashBoard);
    }


    public LiveData<DashBoard> getDashboardDetails(String uId) {
        return appRepository.getDashBoardData(uId);
    }

}