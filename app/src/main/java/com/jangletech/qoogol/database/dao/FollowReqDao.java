package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.FollowRequest;
import com.jangletech.qoogol.model.FriendRequest;

import java.util.List;

/**
 * Created by Pritali on 6/8/2020.
 */
@Dao
public interface FollowReqDao {

    @Query("SELECT * FROM FollowRequest")
    LiveData<List<FollowRequest>> getAllFollowReq();

    @Query("SELECT * FROM FollowRequest")
    List<FollowRequest> getFollowdReq();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFollowdReq(List<FollowRequest> followRequestList);

    @Delete
    void delete(List<FollowRequest> followRequestList);

    @Update
    void updateFollowReq(List<FollowRequest> followRequestList);

    @Transaction
    default void upsertFriends(List<FollowRequest> followRequestList) {
        insertFollowdReq(followRequestList);
        updateFollowReq(followRequestList);
    }
}