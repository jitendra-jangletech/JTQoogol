package com.jangletech.qoogol.ui.notifications;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private String TAG = NotificationsViewModel.class.getSimpleName();

    private MutableLiveData<List<Notification>> notificationList;

    LiveData<List<Notification>> getNotifications() {
        if (notificationList == null) {
            notificationList = new MutableLiveData<>();
            loadNotifications();
        }
        return notificationList;
    }

    private void loadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("Arjun shared Test with you",R.drawable.profile_img2));
        notifications.add(new Notification("Krishna commented on your Test(Shapes & Angles)",R.drawable.profile_img));
        notifications.add(new Notification("Spruha likes your Test(Reading Comprehension)",R.drawable.female_profile_img));
        notifications.add(new Notification("Krishna added new Test(Economy)",R.drawable.profile_img2));
        notifications.add(new Notification("Arjit added feedback on your Test(Gravity)",R.drawable.profile_img));
        notifications.add(new Notification("Shilpa taken Test created by you",R.drawable.female_profile_img));
        notificationList.setValue(notifications);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "on cleared called");
    }

}
