package com.jangletech.qoogol.ui.notifications;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.Notification;

import java.util.List;

public class NotificationsViewModel extends AndroidViewModel {

    private String TAG = NotificationsViewModel.class.getSimpleName();
    private AppRepository appRepository;

    public MutableLiveData<List<Notification>> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList.setValue(notificationList);
    }

    private MutableLiveData<List<Notification>> notificationList;

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        notificationList = new MutableLiveData<>();
        appRepository = new AppRepository(application);
    }

    public void insert(List<Notification> notifications) {
        appRepository.insertNotifications(notifications);
    }

    public void deleteNotification(String nId) {
        appRepository.deleteNotification(nId);
    }




    public LiveData<List<Notification>> getAllNotifications() {
        return appRepository.getAllNotifications();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "on cleared called");
    }
}
