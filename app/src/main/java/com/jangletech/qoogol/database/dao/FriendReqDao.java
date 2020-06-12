package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.FriendRequest;
import com.jangletech.qoogol.model.Friends;

import java.util.List;

/**
 * Created by Pritali on 6/8/2020.
 */
@Dao
public interface FriendReqDao {

    @Query("SELECT * FROM FriendRequest")
    LiveData<List<FriendRequest>> getAllFriendReq();

    @Query("SELECT * FROM FriendRequest")
    List<FriendRequest> getFriendReq();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriendReq(List<FriendRequest> friendRequestList);

    @Delete
    void delete(List<FriendRequest> friendRequestList);

    @Update
    void updateFriendReq(List<FriendRequest> friendRequestList);

    @Transaction
    default void upsertFriends(List<FriendRequest> friendRequestList) {
        insertFriendReq(friendRequestList);
        updateFriendReq(friendRequestList);
    }
}