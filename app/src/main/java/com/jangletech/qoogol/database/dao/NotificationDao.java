package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.Notification;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Notification> notifications);

    @Query("Select * from Notification")
    LiveData<List<Notification>> getAllNotifications();

    @Query("delete from Notification")
    void deleteAllNotifications();

    @Query("delete from Notification where n_id=:nId")
    void deleteNotification(String nId);

}
