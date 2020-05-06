package com.jangletech.qoogol.ui.notifications;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.Notification;

import java.util.List;

public class NotificationsViewModel extends AndroidViewModel {

    private String TAG = NotificationsViewModel.class.getSimpleName();

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
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "on cleared called");
    }
}
