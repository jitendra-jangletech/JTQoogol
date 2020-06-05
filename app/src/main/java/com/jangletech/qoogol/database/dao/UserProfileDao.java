package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.UserProfile;

@Dao
public interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile userProfile);

    @Query("Select * from userprofile where userId =:userId")
    LiveData<UserProfile> getUserProfile(String userId);

    @Query("Select * from userprofile where userId =:userId")
    UserProfile getUserProfilePrev(String userId);

    @Query("delete from userprofile")
    void deleteUserProfile();
}
